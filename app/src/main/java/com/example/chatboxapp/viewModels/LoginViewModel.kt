package com.example.chatboxapp.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    val emailErrorText: MutableStateFlow<String?> = MutableStateFlow("")
    val passErrorText: MutableStateFlow<String?> = MutableStateFlow("")

    val data: HashMap<String, MutableStateFlow<String?>> = hashMapOf("" to MutableStateFlow(""))
    init {
        viewModelScope.launch {
            emailErrorText.value = emailErrorText.value
            passErrorText.value = passErrorText.value
            data.forEach { (key, value) -> data[key] = value }
        }
    }
}