package com.example.quickchat

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class QuickChatApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@QuickChatApplication)
            modules(

            )
        }
    }

}