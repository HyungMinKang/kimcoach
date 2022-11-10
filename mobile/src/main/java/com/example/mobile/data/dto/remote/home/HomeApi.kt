package com.example.mobile.data.dto.remote.home

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface HomeApi {

    @Multipart
    @POST("upload-csv/")
    suspend fun loadImage(@Part file: MultipartBody.Part): ResponseBody
}