package com.example.mobile.data.dto.remote.repository

import com.example.mobile.data.dto.remote.home.HomeDataSource
import com.example.mobile.domain.HomeRepository
import okhttp3.MultipartBody

class HomeRepositoryImpl(private val dataSource: HomeDataSource) :HomeRepository{
    override suspend fun uploadFile(part: MultipartBody.Part): String {
        return dataSource.uploadCSV(part)
    }
}