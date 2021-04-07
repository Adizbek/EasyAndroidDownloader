package com.github.adizbek.easydownload.library.http.progress

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.github.adizbek.easydownload.library.R
import com.github.adizbek.easydownload.library.helpers.toMultiPartData
import com.github.adizbek.easydownload.library.helpers.toPrettyBytes
import kotlinx.android.synthetic.main.multi_part_progress_dialog.view.*
import okhttp3.MultipartBody


class MultiPartProgressDialog(
    private val onCancel: (() -> Unit)?
) : DialogFragment(), OnMultiPartUploadProgress {

    private val _progress = MultiPartUploadProgress(this)

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.multi_part_progress_dialog, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.cancel_action.setOnClickListener {
            onCancel?.invoke()
        }
    }

    override fun progress(
        currentFileIndex: Int,
        filesAmount: Int,
        currentFileRead: Long,
        currentFileTotal: Long,
        allFileRead: Long,
        allFilesTotal: Long,
        currentProgress: Int,
        totalProgress: Int,
    ) {
        view?.apply {
            tv_filename.text = _progress.getPartFileName(currentFileIndex)
            progress.progress = currentProgress
            files_count.text = "${currentFileIndex + 1} / $filesAmount"
            bytes_count.text =
                "${currentFileRead.toPrettyBytes} / ${currentFileTotal.toPrettyBytes}"
        }
    }

    override fun onFinished() {
        dismiss()
    }

    fun uploadWithProgress(uri: Uri, partName: String = "file"): MultipartBody.Part {
        return uri.toMultiPartData(partName, _progress)
    }

}