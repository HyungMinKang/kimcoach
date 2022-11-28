package com.example.mobile.domain

import com.example.mobile.domain.model.SignUpForm

interface SignUpRepository {
    suspend fun requestSignUp(form: SignUpForm):Boolean
}