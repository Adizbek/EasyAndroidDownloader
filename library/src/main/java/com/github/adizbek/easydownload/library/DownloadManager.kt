package com.github.adizbek.easydownload.library

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import kotlin.math.max

class DownloadManager(val client: OkHttpClient = OkHttpClient()) {
    private val queue = ArrayList<DownloadRequest>()
    private val downloading = hashMapOf<DownloadRequest, Job>()
    private val poolSize: Int = 4

    var notifyDownloadProgressInterval = 30
        set(value) {
            field = max(value, 30)
        }


    fun queueDownload(download: DownloadRequest) {
        queue.add(download)

        lookupAndDownload()
    }

    private fun getDownloadRequestFromQueue(): DownloadRequest? {
        return try {
            queue.removeFirst()
        } catch (e: NoSuchElementException) {
            null
        }
    }

    private fun lookupAndDownload() {
        if (!hasFreePool)
            return

        val request = getDownloadRequestFromQueue() ?: return

        val job = GlobalScope.launch {
            request.download(this@DownloadManager)

            lookupAndDownload()

            downloading.remove(request)
        }

        downloading[request] = job
    }

    fun cancelDownload(download: DownloadRequest) {
        if (downloading.containsKey(download)) {
            downloading[download]?.cancel("Interrupted by user")

            downloading.remove(download)
        }
    }

    fun cancelByUrl(url:String) {
        downloading.keys.firstOrNull { it.url == url }?.let {
            cancelDownload(it)
        }
    }

    private val hasFreePool: Boolean
        get() = downloading.size < poolSize
}