package com.example.mobile.data.dto.remote.signup

import com.example.mobile.domain.model.SignUpForm

interface SignUpDataSource {

    suspend fun requestSignUp(form: SignUpForm):Boolean
}