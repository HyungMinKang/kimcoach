package com.example.mobile.di

import com.example.mobile.ui.home.HomeViewModel
import com.example.mobile.ui.login.LoginViewModel
import com.example.mobile.ui.signup.SignUpViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel { LoginViewModel(get()) }
    viewModel { HomeViewModel(get()) }
    viewModel { SignUpViewModel(get())}
}