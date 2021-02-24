package com.github.adizbek.easydownload.library

import okhttp3.*
import java.io.File
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.math.abs

class DownloadRequest(
    val url: String,
    private val savePath: File,
    private val callback: DownloadCallback
) {
    var forceDownload = false
    private var call: Call? = null

    suspend fun download(manager: DownloadManager) {
        try {
            callback.onDownloadStart()

            if (hasFileAlreadyDownloaded()) {
                callback.onDownloadProgress(savePath.length(), savePath.length(), 0)
            } else {
                processDownload(manager)
            }

            callback.onDownloadEnd()
        } catch (e: Exception) {
            callback.onDownloadError(e)
        }
    }

    private suspend fun processDownload(manager: DownloadManager) {
        val request = Request.Builder()
            .url(url)
            .build()

        call = manager.client.newCall(request)


        suspendCoroutine<Any> { promise->
            call?.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    promise.resume(0)
                }

                override fun onResponse(call: Call, response: Response) {
                    response.body?.let {
                        readBody(manager, it)
                    }

                    promise.resume(1)
                }
            })
        }
    }

    private fun readBody(manager: DownloadManager, body: ResponseBody) {
        val folder = savePath.parentFile ?: throw Exception("Has no folder")

        if (!folder.exists()) {
            folder.mkdirs()
        }

        savePath.outputStream().use {
            var notifyTime = System.currentTimeMillis()
            val totalBytes = body.contentLength()
            var lastDownloadedBytes = 0L

            body.source().inputStream().copyToWithProgress(it) { totalCopied, copiedNow ->
                val timeDiff = System.currentTimeMillis() - notifyTime

                if (timeDiff >= manager.notifyDownloadProgressInterval) {
                    val speed = abs(totalCopied - lastDownloadedBytes) * 1_000 / timeDiff
                    callback.onDownloadProgress(totalCopied, totalBytes, speed / 1024)

                    notifyTime = System.currentTimeMillis()
                    lastDownloadedBytes = totalCopied
                }
            }

            // send the latest progress as all downloaded
            callback.onDownloadProgress(totalBytes, totalBytes, 0)
        }
    }

    private fun hasFileAlreadyDownloaded(): Boolean {
        if (forceDownload)
            return false

        return savePath.exists() && savePath.isFile
    }

    fun cancel() {
        call?.cancel()
        callback.onDownloadCancel()
    }
}