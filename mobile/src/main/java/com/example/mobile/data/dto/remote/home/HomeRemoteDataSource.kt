package com.example.mobile.data.dto.remote.home

import okhttp3.MultipartBody

class HomeRemoteDataSource(private val api: HomeApi):HomeDataSource {
    override suspend fun uploadCSV(data: MultipartBody.Part): String {
        return api.loadImage(data).string()
    }
}