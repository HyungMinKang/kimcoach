package com.example.mobile.domain

interface LoginRepository {
    suspend fun loginSubmission():String
}