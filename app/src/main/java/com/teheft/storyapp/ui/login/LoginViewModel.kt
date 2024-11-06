package com.teheft.storyapp.ui.login

import androidx.lifecycle.ViewModel
import com.teheft.storyapp.data.Repository

class LoginViewModel(private val repository: Repository): ViewModel() {

    fun login(name: String, password: String) = repository.login(name, password)
}