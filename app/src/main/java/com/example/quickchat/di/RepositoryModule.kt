package com.example.quickchat.di

import com.example.quickchat.data.repository.FirebaseRepositoryImpl
import com.example.quickchat.domain.repository.FirebaseRepository
import org.koin.dsl.module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind

val repositoryModule = module {
    singleOf(::FirebaseRepositoryImpl) bind FirebaseRepository::class
}