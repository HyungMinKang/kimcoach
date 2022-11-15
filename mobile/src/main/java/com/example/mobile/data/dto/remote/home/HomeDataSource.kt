package com.example.mobile.data.dto.remote.home

import okhttp3.MultipartBody

interface HomeDataSource {

    suspend fun uploadCSV(data: MultipartBody.Part): String
}