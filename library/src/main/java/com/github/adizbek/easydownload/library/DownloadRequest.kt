package com.github.adizbek.easydownload.library

import okhttp3.Request
import okhttp3.ResponseBody
import java.io.File

class DownloadRequest(
    private val url: String,
    private val savePath: File,
    private val callback: DownloadCallback
) {
    var forceDownload = false

    fun download(manager: DownloadManager) {
        try {
            callback.onDownloadStart()

            if (hasFileAlreadyDownloaded()) {
                callback.onDownloadProgress(savePath.length(), savePath.length())
            } else {
                processDownload(manager)
            }

            callback.onDownloadEnd()
        } catch (e: Exception) {
            callback.onDownloadError(e)
        }
    }

    private fun processDownload(manager: DownloadManager) {
        val request = Request.Builder()
            .url(url)
            .build()

        val response = manager.client.newCall(request)
            .execute()

        response.body?.let {
            readBody(manager, it)
        }
    }

    private fun readBody(manager: DownloadManager, body: ResponseBody) {
        savePath.outputStream().use {
            var notifyTime = System.currentTimeMillis()
            val totalBytes = body.contentLength()

            body.source().inputStream().copyToWithProgress(it) { totalCopied, copiedNow ->
                if (System.currentTimeMillis() - notifyTime >= manager.notifyDownloadProgressInterval) {
                    callback.onDownloadProgress(totalCopied, totalBytes)

                    notifyTime = System.currentTimeMillis()
                }
            }

            // send the latest progress as all downloaded
            callback.onDownloadProgress(totalBytes, totalBytes)
        }
    }

    private fun hasFileAlreadyDownloaded(): Boolean {
        if (forceDownload)
            return false

        return savePath.exists() && savePath.isFile
    }
}