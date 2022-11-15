package com.example.mobile.domain

import okhttp3.MultipartBody

interface HomeRepository {

    suspend fun uploadFile(part: MultipartBody.Part): String
}