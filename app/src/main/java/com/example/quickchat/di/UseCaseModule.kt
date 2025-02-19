package com.example.quickchat.di

import com.example.quickchat.domain.usecase.RegisterNewUserUseCase
import org.koin.dsl.module

val useCaseModule = module {
    factory { RegisterNewUserUseCase(get()) }
}