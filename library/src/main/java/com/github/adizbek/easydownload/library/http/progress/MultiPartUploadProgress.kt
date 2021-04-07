package com.github.adizbek.easydownload.library.http.progress

interface OnMultiPartUploadProgress {
    fun progress(
        currentFileIndex: Int, filesAmount: Int,
        currentFileRead: Long, currentFileTotal: Long,
        allFileRead: Long, allFilesTotal: Long,
        currentProgress: Int, totalProgress: Int,
    )

    fun onFinished()
}

class MultiPartUploadProgress(
    private val listener: OnMultiPartUploadProgress,
) : FileDataUploadProgress {
    private val parts = arrayListOf<FileDataRequestBody>()

    private var totalFiles = 0
    private var allFilesRead = 0L
    private var allFilesTotalRead = 0L

    private var finishedFiles = 0

    fun attach(part: FileDataRequestBody) {
        part.progressListener = this

        parts.add(part)

        totalFiles++
        allFilesTotalRead += part.contentLength()
    }

    override fun onProgress(part: FileDataRequestBody, now: Int, written: Long, total: Long) {
        allFilesRead += now

        listener.progress(
            parts.indexOf(part), totalFiles,
            written, total,
            allFilesRead, allFilesTotalRead,
            kotlin.math.max((written * 100 / total).toInt(), 100),
            kotlin.math.max((allFilesRead * 100 / allFilesTotalRead).toInt(), 100),
        )
    }

    override fun onFinished(part: FileDataRequestBody) {
        finishedFiles++

        if (totalFiles == finishedFiles) {
            listener.onFinished()
        }
    }

    fun getPartFileName(index: Int): String {
        return parts[index].filename()
    }
}