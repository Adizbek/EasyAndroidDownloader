package com.github.adizbek.easydownload.library

import kotlinx.coroutines.*

class DownloadPool(
        private val manager: DownloadManager,
        private val poolSize: Int = 4
) {

    private val downloading = hashMapOf<DownloadRequest, Job>()

    fun lookupAndDownload() {
        if (!hasFreePool)
            return

        val request = manager.getDownloadRequestFromQueue()

        if (request == null) {
            return
        }

        val job = GlobalScope.launch {
            request.download(manager)

            lookupAndDownload()
        }

        downloading[request] = job
    }

    private val hasFreePool: Boolean
        get() = downloading.size < poolSize
}