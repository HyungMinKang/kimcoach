package com.example.mobile.data.dto.remote.signup

import com.example.mobile.domain.model.SignUpForm

class SignUpRemoteDataSource(private val api: SignUpApi):SignUpDataSource {
    override suspend fun requestSignUp(form: SignUpForm): Boolean {
        return api.requestSignUp(form)
    }
}