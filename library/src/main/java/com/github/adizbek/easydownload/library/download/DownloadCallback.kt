package com.github.adizbek.easydownload.library.download

interface DownloadCallback {
    fun onDownloadStart()

    fun onDownloadError(error: Throwable)

    fun onDownloadProgress(downloaded: Long, totalBytes: Long, speed: Long)

    fun onDownloadEnd()

    fun onDownloadCancel()
}