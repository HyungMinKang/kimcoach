package com.example.mobile.di

import com.example.mobile.data.dto.remote.home.HomeApi
import com.example.mobile.data.dto.remote.home.HomeDataSource
import com.example.mobile.data.dto.remote.home.HomeRemoteDataSource
import com.example.mobile.data.dto.remote.login.LoginApi
import com.example.mobile.data.dto.remote.login.LoginDataSource
import com.example.mobile.data.dto.remote.login.LoginRemoteDataSource
import com.example.mobile.data.dto.remote.repository.HomeRepositoryImpl
import com.example.mobile.data.dto.remote.repository.LoginRepositoryImpl
import com.example.mobile.data.dto.remote.repository.SignUpRepositoryImpl
import com.example.mobile.data.dto.remote.signup.SignUpApi
import com.example.mobile.data.dto.remote.signup.SignUpDataSource
import com.example.mobile.data.dto.remote.signup.SignUpRemoteDataSource
import com.example.mobile.domain.HomeRepository
import com.example.mobile.domain.LoginRepository
import com.example.mobile.domain.SignUpRepository
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

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

    single<Retrofit>() {
        Retrofit.Builder()
            .baseUrl("http://163.239.223.177:5006/")
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .client(get(named("Normal")))
            .build()
    }

    single<LoginApi> { get<Retrofit>().create(LoginApi::class.java) }
    single<LoginDataSource> { LoginRemoteDataSource(get()) }
    single<LoginRepository> { LoginRepositoryImpl(get()) }

    single<HomeApi> { get<Retrofit>().create(HomeApi::class.java) }
    single<HomeDataSource> { HomeRemoteDataSource(get()) }
    single<HomeRepository> { HomeRepositoryImpl(get()) }

    single<SignUpApi> {get<Retrofit>().create(SignUpApi::class.java)}
    single<SignUpDataSource> { SignUpRemoteDataSource(get())}
    single<SignUpRepository> { SignUpRepositoryImpl(get())}
}