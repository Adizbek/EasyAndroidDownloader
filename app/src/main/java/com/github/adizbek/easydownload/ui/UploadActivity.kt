package com.github.adizbek.easydownload.ui

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.github.adizbek.easydownload.R
import com.github.adizbek.easydownload.api.UploadService
import com.github.adizbek.easydownload.library.helpers.getFileInfo
import com.github.adizbek.easydownload.library.helpers.filePicker
import com.github.adizbek.easydownload.library.http.progress.UploadProgressDialog
import com.github.adizbek.easydownload.library.http.progress.uploadWithProgressDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_upload.*
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class UploadActivity : AppCompatActivity() {
    var pickedFile: Uri? = null

    @Inject
    lateinit var uploadService: UploadService


    val pickFile = filePicker { uri: Uri? ->
        if (uri != null) {
            pickedFile = uri
            tv_filename.text = uri.getFileInfo().displayString()

            uploadWithProgressDialog {
                val file = it.trackProgress(uri)
                val file1 = it.trackProgress(uri)
                val file2 = it.trackProgress(uri)

                uploadService.uploadFile(file, file1, file2)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

        upload_btn.setOnClickListener {
            pickFile.launch("*/*")
        }
    }

}