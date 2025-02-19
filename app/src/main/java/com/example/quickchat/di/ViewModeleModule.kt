package com.example.quickchat.di

import com.example.quickchat.presentation.screens.authorization.register.RegisterViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::RegisterViewModel)
}