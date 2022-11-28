package com.example.mobile.data.dto.remote.repository

import com.example.mobile.data.dto.remote.login.LoginDataSource
import com.example.mobile.domain.LoginRepository

class LoginRepositoryImpl(private val dataSource: LoginDataSource) : LoginRepository {
    override suspend fun loginSubmission(): Boolean {
        return dataSource.loginSubmission()
    }
}