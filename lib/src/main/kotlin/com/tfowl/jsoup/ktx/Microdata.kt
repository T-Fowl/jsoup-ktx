package com.tfowl.jsoup.ktx

import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.temporal.TemporalAccessor

data class MicrodataItem(
    val properties: Map<String, MicrodataValue>,
    val type: Set<String>? = null,
    val id: String? = null,
)

sealed class MicrodataValue {
    data class Text(val text: String) : MicrodataValue()
    data class Url(val url: String) : MicrodataValue()
    data class Item(val item: MicrodataItem) : MicrodataValue()
    data class Datetime(val temporal: TemporalAccessor) : MicrodataValue()
    data class Collection(val values: List<MicrodataValue>) : MicrodataValue()

    companion object {
        val EmptyText = Text("")
    }
}

private fun Element.findMicrodataProperties(): List<Element> {
    val root = this

    val results = mutableListOf<Element>()
    val memory = mutableListOf<Element>()
    val pending = mutableListOf<Element>()

    memory += root
    pending += root.children()

    if (root.hasAttr("itemref")) {
        for (id in root.attr("itemref").split(" ")) {
            ownerDocument()?.getElementById(id)?.let { ref -> pending += ref }
        }
    }

    while (pending.isNotEmpty()) {
        val current = pending.removeFirst()

        if (current in memory) {
            // Microdata error - just continue
            continue
        }

        memory += current

        if (!current.hasAttr("itemscope")) {
            pending += current.children()
        }

        if (current.hasAttr("itemprop") && current.attr("itemprop").isNotEmpty()) {
            results += current
        }

        // TODO: sort results in tree order
    }

    return results
}

fun Element.getMicrodataItem(): MicrodataItem {
    val tagsWithSrcAttr = listOf("audio", "embed", "iframe", "img", "source", "track", "video")

    fun Element.textAttr(name: String): MicrodataValue = MicrodataValue.Text(attr(name))

    fun Element.textContent(): MicrodataValue = MicrodataValue.Text(text())

    fun Element.urlAttr(name: String): MicrodataValue =
        if (hasAttr(name) && attr(name).isNotEmpty()) MicrodataValue.Url(attr("abs:$name")) else MicrodataValue.EmptyText

    fun Element.datetime(): MicrodataValue {
        val content = if (hasAttr("datetime")) attr("datetime") else text().trim()

        val formatters = listOf<DateTimeFormatter>(
            DateTimeFormatter.ofPattern("uuuu-MM"),
            DateTimeFormatter.ISO_LOCAL_DATE,
            DateTimeFormatter.ofPattern("[--]MM-dd"),
            DateTimeFormatter.ISO_LOCAL_TIME,
            DateTimeFormatter.ISO_LOCAL_DATE_TIME,
            DateTimeFormatter.ofPattern("X"),
            DateTimeFormatter.ISO_OFFSET_DATE_TIME,
            DateTimeFormatter.ofPattern("uuuu-'W'ww"),
            DateTimeFormatter.ofPattern("uu"),
            DateTimeFormatter.ofPattern("uuu"),
            DateTimeFormatter.ofPattern("uuuu"),
            DateTimeFormatter.ofPattern("uuuuu"),
            DateTimeFormatter.ofPattern("uuuuuu"),
        )

        for (formatter in formatters) {
            try {
                return MicrodataValue.Datetime(formatter.parse(content))
            } catch (ignored: DateTimeParseException) {
            }
        }

        // TODO: Duration

        return MicrodataValue.EmptyText
    }


    val properties = mutableMapOf<String, MicrodataValue>()

    findMicrodataProperties().forEach { element ->
        val keys = element.attr("itemprop").split(' ').toSet()

        val value = if (element.hasAttr("itemscope")) {
            MicrodataValue.Item(element.getMicrodataItem())
        } else when (element.tag().normalName()) {
            "meta"              -> element.textAttr("content")
            in tagsWithSrcAttr  -> element.urlAttr("src")
            "a", "area", "link" -> element.urlAttr("href")
            "object"            -> element.urlAttr("data")
            "data", "meter"     -> element.textAttr("value")
            "time"              -> element.datetime()
            else                -> element.textContent()
        }

        fun MicrodataValue.merge(rhs: MicrodataValue): MicrodataValue = when (this) {
            is MicrodataValue.Collection -> when (rhs) {
                is MicrodataValue.Collection -> MicrodataValue.Collection(values + rhs.values)
                else                         -> MicrodataValue.Collection(values + rhs)
            }
            else                         -> when (rhs) {
                is MicrodataValue.Collection -> MicrodataValue.Collection(listOf(this) + rhs.values)
                else                         -> MicrodataValue.Collection(listOf(this, rhs))
            }
        }

        keys.forEach { key ->
            properties.merge(key, value) { a, b -> a.merge(b) }
        }
    }

    val type = if (hasAttr("itemtype")) attr("itemtype").split(' ').toSet() else null
    val id = if (hasAttr("itemid")) attr("itemid").trim() else null

    return MicrodataItem(properties, type, id)
}

fun Document.getMicrodataItems(): List<MicrodataItem> {
    // Finds all top-level items (items that are not themselves properties of other items)
    return select("[itemscope]:not([itemprop])").map { it.getMicrodataItem() }
}