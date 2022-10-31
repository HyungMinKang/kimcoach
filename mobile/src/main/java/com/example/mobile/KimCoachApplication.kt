package com.example.mobile

import android.app.Application
import com.example.mobile.di.NetWorkModule
import com.example.mobile.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class KimCoachApplication:Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin{
            androidContext(this@KimCoachApplication)
            modules(appModule, NetWorkModule)
        }
    }
}