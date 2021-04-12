package com.github.adizbek.easydownload.library.http.progress

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.UiThread
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.github.adizbek.easydownload.library.R
import com.github.adizbek.easydownload.library.helpers.toMultiPartData
import com.github.adizbek.easydownload.library.helpers.toPrettyBytes
import kotlinx.android.synthetic.main.multi_part_progress_dialog.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody

private data class ProgressData(
    val currentFileIndex: Int = 0,
    val currentFileRead: Long = 0,
    val currentFileTotal: Long = 0,
    val currentProgress: Int = 0,
    val filesAmount: Int = 0
)

class UploadProgressDialog(
    private val onCancel: (() -> Unit)?
) : DialogFragment(), OnMultiPartUploadProgress {

    private val _progress = MultiPartUploadProgress(this)

    private val progressLiveData = MutableLiveData(ProgressData())

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
            dialog?.dismiss()
        }

        progressLiveData.observe(this) {
            view.apply {
                tv_filename.text = _progress.getPartFileName(it.currentFileIndex)
                progress.progress = it.currentProgress
                files_count.text = "${it.currentFileIndex + 1} / ${it.filesAmount}"
                bytes_count.text =
                    "${it.currentFileRead.toPrettyBytes} / ${it.currentFileTotal.toPrettyBytes}"
            }
        }
    }

    @UiThread
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
        progressLiveData.postValue(
            ProgressData(
                currentFileIndex,
                currentFileRead,
                currentFileTotal,
                currentProgress,
                filesAmount
            )
        )
    }

    override fun onFinished() {
        dialog?.dismiss()
    }

    fun trackProgress(uri: Uri, partName: String = "file"): MultipartBody.Part {
        val part = uri.toMultiPartData(partName)

        if (part.body is FileDataRequestBody) {
            _progress.attach(part.body as FileDataRequestBody)
        }

        return part
    }

}

private fun launchProgressDialog(
    lifecycleScope: LifecycleCoroutineScope,
    fragmentManager: FragmentManager,
    build: (suspend (UploadProgressDialog) -> Unit),
    onCancel: (() -> Unit)? = null,
    fragmentTag: String? = "UploadProgressDialog",
) {
    lifecycleScope.launchWhenStarted {
        withContext(Dispatchers.Main) {
            UploadProgressDialog {
                cancel()

                onCancel?.invoke()
            }.apply {
                show(fragmentManager, fragmentTag)

                withContext(Dispatchers.IO) {
                    build(this@apply)
                }
            }
        }
    }
}

fun Fragment.uploadWithProgressDialog(
    build: (suspend (UploadProgressDialog) -> Unit),
    onCancel: (() -> Unit)? = null,
    fragmentTag: String? = "UploadProgressDialog",
) = launchProgressDialog(
    lifecycleScope,
    parentFragmentManager,
    build,
    onCancel,
    fragmentTag
)

fun Fragment.uploadWithProgressDialog(build: (suspend (UploadProgressDialog) -> Unit)) =
    uploadWithProgressDialog(build, null)


fun AppCompatActivity.uploadWithProgressDialog(
    build: (suspend (UploadProgressDialog) -> Unit),
) = uploadWithProgressDialog(build, null)

fun AppCompatActivity.uploadWithProgressDialog(
    build: (suspend (UploadProgressDialog) -> Unit),
    onCancel: (() -> Unit)? = null,
    fragmentTag: String? = "UploadProgressDialog",
) = launchProgressDialog(
    lifecycleScope,
    supportFragmentManager,
    build,
    onCancel,
    fragmentTag
)