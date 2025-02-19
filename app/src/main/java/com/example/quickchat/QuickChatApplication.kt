package com.example.quickchat

import android.app.Application
import com.example.quickchat.di.firebaseModule
import com.example.quickchat.di.repositoryModule
import com.example.quickchat.di.useCaseModule
import com.example.quickchat.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class QuickChatApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@QuickChatApplication)
            modules(
                firebaseModule,
                repositoryModule,
                useCaseModule,
                viewModelModule
            )
        }
    }

}