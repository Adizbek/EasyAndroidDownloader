package com.github.adizbek.easydownload.library.helpers

import android.net.Uri
import android.provider.OpenableColumns
import com.blankj.utilcode.util.Utils
import com.github.adizbek.easydownload.library.http.progress.FileDataRequestBody
import com.github.adizbek.easydownload.library.http.progress.MultiPartUploadProgress
import okhttp3.MultipartBody
import java.io.FileNotFoundException
import java.io.InputStream

fun Uri.openInputStream(): InputStream {
    val contentResolver = Utils.getApp().contentResolver

    return contentResolver.openInputStream(this)
        ?: throw FileNotFoundException("Failed to open input stream")
}

fun Uri.getFileInfo(): UriFileData {
    val contentResolver = Utils.getApp().contentResolver
    var name = "file"
    var size = 0L
    val mime = contentResolver.getType(this).orEmpty()

    contentResolver.query(this, null, null, null, null)?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
        cursor.moveToFirst()

        name = cursor.getString(nameIndex)
        size = cursor.getLong(sizeIndex)
    }

    return UriFileData(this, name, size, mime)
}

fun Uri.toMultiPartData(
    partName: String = "file",
    progressListener: MultiPartUploadProgress? = null,
): MultipartBody.Part {
    val fileData = getFileInfo()
    val requestFile = FileDataRequestBody(fileData)

    progressListener?.attach(requestFile)

    return MultipartBody.Part.createFormData(partName, fileData.fileName, requestFile)
}

data class UriFileData(
    val uri: Uri,
    val fileName: String,
    val fileSize: Long = 0,
    val mime: String = "",
) {
    fun displayString(): String {
        return "${this.fileName}, ${this.fileSize.toInt().toKb}Кб"
    }
}