package com.github.adizbek.easydownload.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.github.adizbek.easydownload.R
import com.github.adizbek.easydownload.api.UploadService
import com.github.adizbek.easydownload.library.helpers.filePicker
import com.github.adizbek.easydownload.library.http.progress.uploadWithProgressDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_test_upload.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class TestUploadFragment : DialogFragment(R.layout.fragment_test_upload) {

    @Inject
    lateinit var uploadService: UploadService


    val pickFile = filePicker { uri ->
        if (uri != null) {
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    uploadWithProgressDialog {
                        val file = it.trackProgress(uri)
                        val file1 = it.trackProgress(uri)
                        val file2 = it.trackProgress(uri)

                        uploadService.uploadFile(file, file1, file2)
                    }
                }
            }
        }

        Toast.makeText(context, "Picked", Toast.LENGTH_SHORT).show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.pickFile.setOnClickListener {
            pickFile.launch("*/*")
        }
    }
}