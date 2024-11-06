package com.teheft.storyapp.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teheft.storyapp.data.Repository
import com.teheft.storyapp.data.Result
import com.teheft.storyapp.data.remote.response.ListStoryItem
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: Repository) : ViewModel() {
    private val _result = MediatorLiveData<Result<List<ListStoryItem>>>()
    val result : LiveData<Result<List<ListStoryItem>>> = _result
    private val _text = MutableLiveData<Result<String>>()
    val text: LiveData<Result<String>> = _text

    fun getStories() {
        viewModelScope.launch {
            val results = repository.getStories()
           _result.addSource(results){
               _result.postValue(it)
           }
        }
    }

}