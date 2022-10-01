package com.tfowl.jsoup.ktx

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

private const val ROOT_PROPERTY_KEY = "."

data class OpenGraphPropertyValue(val content: String, val extended: Map<String, String>) {
    val hasStructuredData = extended.isNotEmpty()

    override fun toString(): String {
        if (extended.isEmpty()) return content
        return buildString {
            append(content)
            extended.toList().joinTo(this, prefix = " (", postfix = ")") { (k,v) -> "$k=$v" }
        }
    }
}

class OpenGraph(private val data: Map<String, List<OpenGraphPropertyValue>>) {
    val keys: Set<String> = data.keys

    fun getContent(key: String): String? = data[key]?.firstOrNull()?.content

    fun getAllContent(key: String): List<String> = data[key]?.map { it.content } ?: emptyList()

    fun getStructured(key: String): OpenGraphPropertyValue? = data[key]?.firstOrNull()

    fun getAllStructured(key: String): List<OpenGraphPropertyValue> = data[key] ?: emptyList()

    override fun toString(): String = buildString {
        fun indent(level: Int): String = " ".repeat(4 * level)

        appendLine("OpenGraph {")

        val maxRootPropertyLength = data.keys.maxOf { it.length }

        for ((property, items) in data) {
            append(indent(level = 1))
            append("%-${maxRootPropertyLength}s".format(property))
            append(": ")

            val maxNestedPropertyLength = items.maxOfOrNull { it.extended.keys.maxOfOrNull { it.length } ?: 0 } ?: 0

            for ((index, item) in items.withIndex()) {
                val root = item.content
                val nestedKeys = item.extended.keys.minus(ROOT_PROPERTY_KEY)

                if (index > 0)
                    append(indent(level = 1) + " ".repeat(maxRootPropertyLength) + "  ")
                appendLine(root)


                for (key in nestedKeys) {
                    append(indent(level = 1) + " ".repeat(maxRootPropertyLength) + "  " + indent(level = 1))
                    append("%${maxNestedPropertyLength}s".format(key))
                    append(": ")
                    append(item.extended[key])
                    appendLine()
                }
            }
        }

        append("}")
    }

    companion object {
        fun from(document: Document): OpenGraph {
            val ogPropertiesAndContent = document.select("head meta[property^='og:']").map { meta ->
                val property = meta.attr("property").removePrefix("og:")
                val content = meta.attr("content")
                property to content
            }

            val data = mutableMapOf<String, MutableList<MutableMap<String, String>>>()

            for ((property, content) in ogPropertiesAndContent) {

                // Here we represent the root property with a special key
                if (":" !in property) {
                    data.computeIfAbsent(property) { mutableListOf() }
                        .add(mutableMapOf(ROOT_PROPERTY_KEY to content))
                }

                // Structured properties
                if (":" in property) {
                    val base = property.substringBefore(":")
                    val structuredProperty = property.substringAfter(":")

                    data.computeIfAbsent(base) { mutableListOf() }.lastOrInsertIfEmpty { mutableMapOf() }
                        .put(structuredProperty, content)
                }
            }

            return OpenGraph(data.map { (k, v) ->
                k to v.map { m ->
                    OpenGraphPropertyValue(
                        m.getValue(ROOT_PROPERTY_KEY), m.minus(
                            ROOT_PROPERTY_KEY
                        )
                    )
                }
            }.toMap())
        }
    }
}

private fun <T> MutableList<T>.lastOrInsertIfEmpty(f: () -> T): T {
    if (isEmpty()) add(f())
    return last()
}

fun Document.getOpenGraphData(): OpenGraph = OpenGraph.from(this)

fun main() {
    val document = Jsoup.parse(
        """<html prefix="og: https://ogp.me/ns#">
<head>
<title>The Rock (1996)</title>
<meta property="og:title" content="The Rock" />
<meta property="og:type" content="video.movie" />
<meta property="og:url" content="https://www.imdb.com/title/tt0117500/" />
<meta property="og:audio" content="https://example.com/bond/theme.mp3" />
<meta property="og:description" 
  content="Sean Connery found fame and fortune as the suave, sophisticated British agent, James Bond." />
<meta property="og:determiner" content="the" />
<meta property="og:locale" content="en_GB" />
<meta property="og:locale:alternate" content="fr_FR" />
<meta property="og:locale:alternate" content="es_ES" />
<meta property="og:site_name" content="IMDb" />
<meta property="og:image" content="https://example.com/ogp.jpg" />
<meta property="og:image:secure_url" content="https://secure.example.com/ogp.jpg" />
<meta property="og:image:type" content="image/jpeg" />
<meta property="og:image:width" content="400" />
<meta property="og:image:height" content="300" />
<meta property="og:image:alt" content="A shiny red apple with a bite taken out" />
<meta property="og:video" content="https://example.com/movie.swf" />
<meta property="og:video:secure_url" content="https://secure.example.com/movie.swf" />
<meta property="og:video:type" content="application/x-shockwave-flash" />
<meta property="og:video:width" content="400" />
<meta property="og:video:height" content="300" />
<meta property="og:audio" content="https://example.com/sound.mp3" />
<meta property="og:audio:secure_url" content="https://secure.example.com/sound.mp3" />
<meta property="og:audio:type" content="audio/mpeg" />
<meta property="og:image" content="https://example.com/rock.jpg" />
<meta property="og:image:width" content="300" />
<meta property="og:image:height" content="300" />
<meta property="og:image" content="https://example.com/rock2.jpg" />
<meta property="og:image" content="https://example.com/rock3.jpg" />
<meta property="og:image:height" content="1000" />
</head>
</html>"""
    )


    val og = document.getOpenGraphData()

    println(og)

    println(og.getContent("image"))

    println(og.getAllContent("image"))

    println(og.getStructured("image"))

    println(og.getAllStructured("image"))
}