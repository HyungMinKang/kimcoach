package com.example.mobile.data.dto.remote.login

class LoginRemoteDataSource(private val api: LoginApi): LoginDataSource {
    override suspend fun loginSubmission(): String {
        return api.getLoginAccess()
    }
}