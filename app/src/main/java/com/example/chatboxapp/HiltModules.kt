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
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Singleton

//@Module
//@InstallIn(ViewModelComponent::class, FragmentComponent::class)
//object FirstModule {
//    @Singleton
//    @Provides
//    fun getMainActivityInstance() = MainViewModel()
//}


@Module
@InstallIn(SingletonComponent::class)
object FirstModule {
    @Singleton
    @Provides
    fun getMainActivityInstance() = MainViewModel()
}

//@Module
//@InstallIn(ViewModelComponent::class, FragmentComponent::class)
//object SecondModule {
//    @Singleton
//    @Provides
//    fun getMainActivityInstance() = MainViewModel()
//}