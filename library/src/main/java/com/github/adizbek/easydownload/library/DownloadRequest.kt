package com.github.adizbek.easydownload.library

import okhttp3.Request
import okhttp3.ResponseBody
import java.io.File
import java.lang.Exception

class DownloadRequest(
        private val url: String,
        private val savePath: File,
        private val callback: DownloadCallback
) {

    fun download(manager: DownloadManager) {
        val httpRequest = Request.Builder()
                .url(url)
                .build()

        callback.onStart()

        try {
            val response = manager.client.newCall(httpRequest)
                    .execute()

            response.body?.let {
                readBody(manager, it)
            }
        } catch (e: Exception) {
            callback.onError(e)
        }

        callback.onEnd()
    }

    private fun readBody(manager: DownloadManager, body: ResponseBody) {
        savePath.outputStream().use {
            var notifyTime = System.currentTimeMillis()
            val totalBytes = body.contentLength()

            body.source().inputStream().copyToWithProgress(it) { totalCopied, copiedNow ->
                if (System.currentTimeMillis() - notifyTime >= manager.notifyDownloadProgressInterval) {
                    callback.onProgress(totalCopied, totalBytes)

                    notifyTime = System.currentTimeMillis()
                }
            }

            // send the latest progress as all downloaded
            callback.onProgress(totalBytes, totalBytes)
        }
    }

}