package com.example.quickchat.di

import com.example.quickchat.data.repository.ChatRepositoryImpl
import com.example.quickchat.data.repository.FirebaseRepositoryImpl
import com.example.quickchat.domain.repository.ChatRepository
import com.example.quickchat.domain.repository.FirebaseRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.koin.dsl.module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindFirebaseRepository(
        impl: FirebaseRepositoryImpl
    ): FirebaseRepository

    @Binds
    @Singleton
    abstract fun bindChatRepository(
        impl: ChatRepositoryImpl
    ): ChatRepository
}