package com.github.adizbek.easydownload.library

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