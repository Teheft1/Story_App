package com.teheft.storyapp.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.teheft.storyapp.data.Repository
import com.teheft.storyapp.data.Result
import com.teheft.storyapp.data.pref.UserModel
import kotlinx.coroutines.launch

class MainViewModel(private val repository: Repository): ViewModel() {

    private val _results = MediatorLiveData<Result<String>>()
    val results: LiveData<Result<String>> = _results

    fun getSession(): LiveData<UserModel>{
        return repository.getSession().asLiveData()
    }
//
    fun signUp(name: String, email: String, password: String) = repository.signUp(name, email, password)
//        viewModelScope.launch {
//            _results.removeSource(repository.signUp(name, email, password))
//            _results.addSource(repository.signUp(name, email, password)){
//                _results.postValue(it)
//            }
//        }
//    }


    fun logout(){
        viewModelScope.launch {
            repository.logOut()
        }
    }
}