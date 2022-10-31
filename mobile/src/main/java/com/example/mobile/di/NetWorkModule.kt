package com.example.mobile.di

import com.example.mobile.data.dto.remote.login.LoginApi
import com.example.mobile.data.dto.remote.login.LoginDataSource
import com.example.mobile.data.dto.remote.login.LoginRemoteDataSource
import com.example.mobile.data.dto.remote.repository.LoginRepositoryImpl
import com.example.mobile.domain.LoginRepository
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val NetWorkModule = module {

    single<OkHttpClient>(named("Normal")) {
        OkHttpClient.Builder()
            .addInterceptor(get<Interceptor>(named("Interceptor")))
            .build()
    }

    single<Interceptor>(named("Interceptor")) {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    single<Retrofit>(named("LoginRetrofit")) {
        Retrofit.Builder()
            .baseUrl("http://54.180.127.134/home/")
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .client(get(named("Normal")))
            .build()
    }

    single<LoginApi> {
        get<Retrofit>(named("LoginRetrofit")).create(LoginApi::class.java)
    }
    single<LoginDataSource> { LoginRemoteDataSource(get()) }
    single<LoginRepository> { LoginRepositoryImpl(get()) }
}