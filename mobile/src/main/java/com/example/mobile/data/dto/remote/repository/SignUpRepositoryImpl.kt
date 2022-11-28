package com.example.mobile.data.dto.remote.repository

import com.example.mobile.data.dto.remote.signup.SignUpDataSource
import com.example.mobile.domain.SignUpRepository
import com.example.mobile.domain.model.SignUpForm

class SignUpRepositoryImpl(private val dataSource: SignUpDataSource): SignUpRepository {
    override suspend fun requestSignUp(form: SignUpForm): Boolean {
        return dataSource.requestSignUp(form)
    }
}