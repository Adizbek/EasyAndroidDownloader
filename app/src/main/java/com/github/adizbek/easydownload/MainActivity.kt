package com.github.adizbek.easydownload

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.adizbek.easydownload.library.download.DownloadCallback
import com.github.adizbek.easydownload.library.download.DownloadManager
import com.github.adizbek.easydownload.library.download.DownloadRequest
import java.io.File

const val TAG = "DOWNLOAD MNG"

class MainActivity : AppCompatActivity(), DownloadCallback {

    val downloadManager = DownloadManager()
    lateinit var progress: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        progress = findViewById(R.id.progress)

        val download = DownloadRequest(
            "https://www.learningcontainer.com/download/sample-pdf-download-10-mb/?wpdmdl=1569&refresh=60369f72132871614192498",
            File(externalCacheDir, "remote/1file.txt"),
            this
        ).apply {
            forceDownload = true
        }

        downloadManager.queueDownload(
            download
        )

        Handler().postDelayed(
            { downloadManager.cancelByUrl(download.url) },
            2000
        )
    }

    override fun onDownloadStart() {
        Log.d(TAG, "onDownloadStart")
    }

    override fun onDownloadError(error: Throwable) {
        Log.d(TAG, "onDownloadError: ${error.localizedMessage}")
    }

    override fun onDownloadProgress(downloaded: Long, totalBytes: Long, speed: Long) {
        Log.d(TAG, "onDownloadProgress: ${downloaded}/${totalBytes}, speed: $speed")

        runOnUiThread {
            progress.text = "${downloaded}/${totalBytes}, speed: $speed kb/s"
        }
    }

    override fun onDownloadEnd() {
        Log.d(TAG, "onDownloadEnd")
    }

    override fun onDownloadCancel() {
        Log.d(TAG, "onDownloadCancel")
    }


}