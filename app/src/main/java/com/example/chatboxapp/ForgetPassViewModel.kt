package com.example.chatboxapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class ForgetPassViewModel : ViewModel() {
    val emailErrorText: MutableStateFlow<String?> = MutableStateFlow("")
    val inputEmail: MutableStateFlow<String?> = MutableStateFlow("")

    init {
        viewModelScope.launch {
            emailErrorText.value = emailErrorText.value
            inputEmail.value = inputEmail.value
        }
    }
}