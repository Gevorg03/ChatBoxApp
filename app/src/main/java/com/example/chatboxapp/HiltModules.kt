package com.example.chatboxapp

import android.app.Activity
import android.content.Context
import androidx.fragment.app.Fragment
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Singleton

@Module
@InstallIn(ViewModelComponent::class, FragmentComponent::class, SingletonComponent::class)
object FirstModule {
    @Singleton
    @Provides
    fun getMainActivityInstance() = MainViewModel()
}
