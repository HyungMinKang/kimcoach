package com.example.mobile.data.dto.remote.signup

import com.example.mobile.domain.model.SignUpForm
import retrofit2.http.POST

interface SignUpApi {

    @POST
     suspend fun requestSignUp(form: SignUpForm):Boolean
}