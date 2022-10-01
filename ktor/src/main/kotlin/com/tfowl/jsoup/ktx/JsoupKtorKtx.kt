@file:JvmName("JsoupKtorKtx")

package com.tfowl.jsoup.ktx

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.streams.*
import org.jsoup.Connection
import org.jsoup.nodes.FormElement
import java.io.InputStream

fun ParametersBuilder.append(kv: Connection.KeyVal): Unit = append(kv.key(), kv.value())

fun ParametersBuilder.appendAll(data: List<Connection.KeyVal>): Unit = data.forEach { append(it) }

fun List<Connection.KeyVal>.toParameters(): Parameters = Parameters.build {
    appendAll(this@toParameters)
}

internal fun InputStream.toInputProvider(): InputProvider = InputProvider(
    block = { this.asInput() }
)

@Suppress("UNCHECKED_CAST")
fun  Connection.KeyVal.toFormPart(): FormPart<Any> {
    // Retrieve the input stream (if it exists) only once in case retrieving it is expensive
    val stream = if (hasInputStream()) inputStream() else null

    // Sanity check
    return if (hasInputStream() && stream != null) {
        val headers = Headers.build {
            value().takeIf { it.isNotBlank() }
                ?.let { name -> append(HttpHeaders.ContentDisposition, "filename=\"${name}\"") }
            contentType()?.takeIf { it.isNotBlank() }
                ?.let { type -> append(HttpHeaders.ContentType, type) }
        }
        FormPart(key(), stream.toInputProvider(), headers)
    } else {
        val headers = when (val type = contentType()) {
            null -> Headers.Empty
            else -> Headers.build { this@build.append(HttpHeaders.ContentType, type) }
        }

        FormPart(key(), value(), headers)
    }
}

fun FormBuilder.append(kv: Connection.KeyVal) = append(kv.toFormPart())

fun FormBuilder.appendAll(data: List<Connection.KeyVal>) = data.forEach { append(it) }

fun List<Connection.KeyVal>.toMultipartFormDataContent(): MultiPartFormDataContent {
    return MultiPartFormDataContent(parts = formData {
        appendAll(this@toMultipartFormDataContent)
    })
}

suspend fun HttpClient.submitFormElement(form: FormElement, block: HttpRequestBuilder.() -> Unit = {}): HttpResponse {
    val url = if (form.hasAttr("action")) form.absUrl("action") else form.baseUri()
    val method = if (form.attr("method").equals("POST", ignoreCase = true)) HttpMethod.Post else HttpMethod.Get

    val data = form.formData()

    return if (data.any { it.hasInputStream() }) {
        submitFormWithBinaryData(url = url, formData = formData {
            appendAll(data)
        }, block)
    } else {
        submitForm(
            url = url,
            formParameters = data.toParameters(),
            encodeInQuery = method == HttpMethod.Get,
            block
        )
    }
}