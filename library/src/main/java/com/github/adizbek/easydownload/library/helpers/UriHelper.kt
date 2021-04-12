package com.github.adizbek.easydownload.library.helpers

import android.net.Uri
import android.provider.OpenableColumns
import com.blankj.utilcode.util.Utils
import com.github.adizbek.easydownload.library.http.progress.FileDataRequestBody
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

fun Uri.toMultiPartRequest(): FileDataRequestBody {
    return FileDataRequestBody(getFileInfo())
}

fun Uri.toMultiPartData(
    partName: String = "file",
): MultipartBody.Part {
    val requestFile = this.toMultiPartRequest()

    return MultipartBody.Part.createFormData(partName, requestFile.filename(), requestFile)
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