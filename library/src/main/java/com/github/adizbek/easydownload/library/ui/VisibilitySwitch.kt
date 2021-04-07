package com.github.adizbek.easydownload.library.ui

import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData


fun LiveData<Boolean>.visibilitySwitch(
    lifecycleOwner: LifecycleOwner,
    onLoading: Array<View>,
    afterLoading: Array<View>
) {
    this.observe(lifecycleOwner) { isLoading ->
        onLoading.forEach {
            it.isVisible = isLoading
        }

        afterLoading.forEach {
            it.isVisible = !isLoading
        }
    }
}

fun LiveData<Boolean>.visibilitySwitch(
    lifecycleOwner: LifecycleOwner, onLoading: View, afterLoading: View
) {
    this.visibilitySwitch(lifecycleOwner, arrayOf(onLoading), arrayOf(afterLoading))
}