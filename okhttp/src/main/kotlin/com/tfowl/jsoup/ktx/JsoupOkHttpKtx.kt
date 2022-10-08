@file:JvmName("JsoupOkHttpKtx")

package com.tfowl.jsoup.ktx

import okhttp3.FormBody
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okio.BufferedSink
import okio.buffer
import okio.source
import org.jsoup.Connection
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import java.io.InputStream

internal interface InputStreamProvider {
    fun provide(): InputStream
}

internal fun InputStream.asProvider(): InputStreamProvider = when (this) {
    // If we have a FileInputStream, simply create new ones referencing the same FileDescriptor
    is FileInputStream      -> object : InputStreamProvider {
        val fd = this@asProvider.fd

        override fun provide(): InputStream = FileInputStream(fd)
    }

    // If we have a ByteArrayInputStream, simply create new streams referencing the same buffer
    is ByteArrayInputStream -> object : InputStreamProvider {
        override fun provide(): InputStream = ByteArrayInputStream(this@asProvider.readBytes())
    }

    // If we have an unknown type, we have no option but to read all bytes into memory
    else                    -> object : InputStreamProvider {
        val baos = ByteArrayOutputStream()

        init {
            this@asProvider.copyTo(baos)
        }

        override fun provide(): InputStream = ByteArrayInputStream(baos.toByteArray())
    }
}

internal class InputStreamProviderRequestBody(
    private val provider: InputStreamProvider,
    private val contentType: MediaType? = null,
) : RequestBody() {
    override fun contentType(): MediaType? = contentType

    override fun writeTo(sink: BufferedSink) {
        sink.writeAll(provider.provide().source().buffer())
    }
}

fun FormBody.Builder.add(kv: Connection.KeyVal): FormBody.Builder = add(kv.key(), kv.value())
fun FormBody.Builder.addEncoded(kv: Connection.KeyVal): FormBody.Builder = addEncoded(kv.key(), kv.value())

fun FormBody.Builder.addAll(data: List<Connection.KeyVal>): FormBody.Builder = this.also { data.forEach { add(it) } }
fun FormBody.Builder.addAllEncoded(data: List<Connection.KeyVal>): FormBody.Builder =
    this.also { data.forEach { addEncoded(it) } }

fun List<Connection.KeyVal>.toFormBody(): FormBody = FormBody.Builder().addAll(this).build()
fun List<Connection.KeyVal>.toFormBodyEncoded(): FormBody = FormBody.Builder().addAllEncoded(this).build()

fun Connection.KeyVal.toMultipartBodyPart(): MultipartBody.Part {
    TODO("Not yet implemented")
}

fun MultipartBody.Builder.add(kv: Connection.KeyVal): MultipartBody.Builder = addPart(kv.toMultipartBodyPart())

fun MultipartBody.Builder.addAllParts(data: List<Connection.KeyVal>): MultipartBody.Builder =
    this.also { data.forEach { add(it) } }

fun List<Connection.KeyVal>.toMultipartBody(): MultipartBody = MultipartBody.Builder().addAllParts(this).build()


