package com.github.adizbek.easydownload.library

import okhttp3.OkHttpClient
import kotlin.math.max

class DownloadManager(val client: OkHttpClient = OkHttpClient()) {
    private val queue = ArrayList<DownloadRequest>()
    private val pool = DownloadPool(this)

    var notifyDownloadProgressInterval = 30
        set(value) {
            field = max(value, 30)
        }


    fun queueDownload(download: DownloadRequest) {
        queue.add(download)

        pool.lookupAndDownload()
    }

    fun getDownloadRequestFromQueue(): DownloadRequest? {
        return try {
            queue.removeFirst()
        } catch (e: NoSuchElementException) {
            null
        }
    }

}