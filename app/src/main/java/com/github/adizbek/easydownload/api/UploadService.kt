package com.github.adizbek.easydownload.api

import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface UploadService {

    @POST("upload")
    @Multipart
    suspend fun uploadFile(
        @Part file: MultipartBody.Part,
        @Part file1: MultipartBody.Part,
        @Part file2: MultipartBody.Part,
    )

}