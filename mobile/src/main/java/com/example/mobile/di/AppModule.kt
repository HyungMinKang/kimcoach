package com.example.mobile.di

import com.example.mobile.ui.login.LoginViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel{ LoginViewModel(get())}
}