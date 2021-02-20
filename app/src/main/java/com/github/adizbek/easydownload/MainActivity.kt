package com.github.adizbek.easydownload

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.adizbek.easydownload.library.DownloadCallback
import com.github.adizbek.easydownload.library.DownloadManager
import com.github.adizbek.easydownload.library.DownloadRequest
import java.io.File

const val TAG = "DOWNLOAD MNG"

class MainActivity : AppCompatActivity(), DownloadCallback {

    val downloadManager = DownloadManager()
    lateinit var progress: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        progress = findViewById(R.id.progress)

        downloadManager.queueDownload(
            DownloadRequest(
                "https://gist.githubusercontent.com/khaykov/a6105154becce4c0530da38e723c2330/raw/41ab415ac41c93a198f7da5b47d604956157c5c3/gistfile1.txt",
                File(externalCacheDir, "remote/1file.txt"),
                this
            ).apply {
                forceDownload = true
            }
        )
    }

    override fun onDownloadStart() {
        Log.d(TAG, "onDownloadStart")
    }

    override fun onDownloadError(error: Throwable) {
        Log.d(TAG, "onDownloadError: ${error.localizedMessage}")
    }

    override fun onDownloadProgress(downloaded: Long, totalBytes: Long) {
        Log.d(TAG, "onDownloadProgress: ${downloaded}/${totalBytes}")

        runOnUiThread {
            progress.text = "${downloaded}/${totalBytes}"
        }
    }

    override fun onDownloadEnd() {
        Log.d(TAG, "onDownloadEnd")
    }


}