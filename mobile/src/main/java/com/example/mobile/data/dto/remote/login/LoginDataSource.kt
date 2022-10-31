package com.example.mobile.data.dto.remote.login

interface LoginDataSource {
    suspend fun loginSubmission(): String
}