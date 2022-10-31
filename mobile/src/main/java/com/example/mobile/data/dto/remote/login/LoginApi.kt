package com.example.mobile.data.dto.remote.login

import retrofit2.http.GET

interface LoginApi {
    @GET
    suspend fun getLoginAccess(): String {
        return "OK"
    }

}