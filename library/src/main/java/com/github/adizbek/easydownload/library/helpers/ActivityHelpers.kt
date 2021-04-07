package com.github.adizbek.easydownload.library.helpers

import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

fun AppCompatActivity.filePicker(pickCallback: (Uri?) -> Unit): ActivityResultLauncher<String> {
    return this.registerForActivityResult(ActivityResultContracts.GetContent(), pickCallback)
}