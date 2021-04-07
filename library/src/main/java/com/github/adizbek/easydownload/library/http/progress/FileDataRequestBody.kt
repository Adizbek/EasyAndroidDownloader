package com.github.adizbek.easydownload.library.http.progress

import com.github.adizbek.easydownload.library.helpers.UriFileData
import com.github.adizbek.easydownload.library.helpers.copyToWithProgress
import com.github.adizbek.easydownload.library.helpers.openInputStream
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okio.BufferedSink


interface FileDataUploadProgress {
    fun onProgress(part: FileDataRequestBody, now: Int, written: Long, total: Long)

    fun onFinished(part: FileDataRequestBody)
}

class FileDataRequestBody(
    private val fileData: UriFileData,
) : RequestBody() {
    var progressListener: FileDataUploadProgress? = null

    override fun contentType() = fileData.mime.toMediaTypeOrNull()

    override fun contentLength() = fileData.fileSize

    override fun writeTo(sink: BufferedSink) {
        val totalToRead = contentLength()

        fileData.uri.openInputStream().buffered().use {
            it.copyToWithProgress(sink.outputStream()) { total, now ->
                progressListener?.onProgress(this, now, total, totalToRead)
            }

            progressListener?.onFinished(this)
        }
    }

    fun filename(): String {
        return fileData.fileName
    }
}