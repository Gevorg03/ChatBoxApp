package com.example.chatboxapp

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class MainViewModel : ViewModel() {
    val currentFragment: MutableStateFlow<Fragment> = MutableStateFlow(SplashFragment())

    init {
        currentFragment.value = currentFragment.value
    }
}