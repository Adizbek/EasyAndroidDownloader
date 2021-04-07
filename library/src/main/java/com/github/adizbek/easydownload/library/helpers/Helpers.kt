package com.github.adizbek.easydownload.library.helpers

import java.io.InputStream
import java.io.OutputStream

fun InputStream.copyToWithProgress(
    out: OutputStream,
    onCopy: (totalBytesCopied: Long, bytesJustCopied: Int) -> Unit
): Long {
    var bytesCopied: Long = 0
    val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
    var bytes = read(buffer)
    while (bytes >= 0) {
        out.write(buffer, 0, bytes)
        bytesCopied += bytes
        onCopy(bytesCopied, bytes)
        bytes = read(buffer)
    }
    return bytesCopied
}

class RunWithInterval(private val intervalMs: Int) {
    private var lastRun = 0L
    var current = 0L

    fun process(runnable: Runnable) {
        current = System.currentTimeMillis()

        if (current - lastRun >= intervalMs) {
            runnable.run()

            lastRun = current
        }
    }
}
