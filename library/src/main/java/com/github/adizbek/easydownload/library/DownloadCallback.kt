package com.github.adizbek.easydownload.library

interface DownloadCallback {
    fun onStart()

    fun onError(error: Throwable)

    fun onProgress(downloaded: Long, totalBytes: Long)

    fun onEnd()
}