@file:JvmName("JsoupKtx")

package com.tfowl.jsoup.ktx

import org.jsoup.Connection
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.nodes.FormElement
import java.io.InputStream

/**
 * Key component
 */
operator fun Connection.KeyVal.component1(): String = key()

/**
 * Value Component
 */
operator fun Connection.KeyVal.component2(): String = value()

/**
 * [InputStream] component (if one is present)
 */
operator fun Connection.KeyVal.component3(): InputStream? = if (hasInputStream()) inputStream() else null

/**
 * Content-Type component (if one is set)
 */
operator fun Connection.KeyVal.component4(): String? = if (contentType()?.isNotBlank() == true) contentType() else null

/**
 * Note that mutations on the returned [MutableMap] do not affect the DOM
 */
fun FormElement.formDataAsMutableMap(): MutableMap<String, Connection.KeyVal> =
    formData().associateBy { it.key() }.toMutableMap()

/**
 * Convenience function to cast as a [FormElement]
 */
fun Element.asFormElement(): FormElement = this as FormElement

/**
 * Convenience function to try casting as a [FormElement]
 */
fun Element.asFormElementOrNull(): FormElement? = this as? FormElement

val Element.textNonEmpty: String? get() = text().takeUnless { it.isEmpty() }
val Element.textNonBlank: String? get() = text().takeUnless { it.isBlank() }

val Element.hrefRelative: String? get() = attr("href")

val Element.href: String? get() = attr("abs:href")

fun Document.metaContentOrNull(property: String): String? {
    return selectFirst("meta[property='$property']")?.attr("content")
}

fun Document.metaContent(property: String): String = requireNotNull(metaContentOrNull(property))