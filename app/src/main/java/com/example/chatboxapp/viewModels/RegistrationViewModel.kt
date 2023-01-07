package com.example.chatboxapp.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class RegistrationViewModel : ViewModel() {
    val fullnameHelperText: MutableStateFlow<String?> = MutableStateFlow("")
    val fullnameErrorText: MutableStateFlow<String?> = MutableStateFlow("")
    val emailHelperText: MutableStateFlow<String?> = MutableStateFlow("")
    val emailErrorText: MutableStateFlow<String?> = MutableStateFlow("")
    val phoneHelperText: MutableStateFlow<String?> = MutableStateFlow("")
    val phoneErrorText: MutableStateFlow<String?> = MutableStateFlow("")
    val passHelperText: MutableStateFlow<String?> = MutableStateFlow("")
    val passErrorText: MutableStateFlow<String?> = MutableStateFlow("")

    val data: HashMap<String, MutableStateFlow<String?>> = hashMapOf("" to MutableStateFlow(""))

    init {
        viewModelScope.launch {
            fullnameHelperText.value = fullnameHelperText.value
            fullnameErrorText.value = fullnameErrorText.value
            emailHelperText.value = emailHelperText.value
            emailErrorText.value = emailErrorText.value
            phoneHelperText.value = phoneHelperText.value
            phoneErrorText.value = phoneErrorText.value
            passHelperText.value = passHelperText.value
            passErrorText.value = passErrorText.value
            data.forEach { (key, value) -> data[key] = value }
        }
    }
}