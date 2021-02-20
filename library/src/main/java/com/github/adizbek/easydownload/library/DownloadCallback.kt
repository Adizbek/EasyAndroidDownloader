package com.github.adizbek.easydownload.library

interface DownloadCallback {
    fun onDownloadStart()

    fun onDownloadError(error: Throwable)

    fun onDownloadProgress(downloaded: Long, totalBytes: Long)

    fun onDownloadEnd()
}