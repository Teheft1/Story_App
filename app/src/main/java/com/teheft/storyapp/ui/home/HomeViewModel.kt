package com.teheft.storyapp.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.teheft.storyapp.data.Repository
import com.teheft.storyapp.data.Result
import com.teheft.storyapp.data.remote.response.ListStoryItem
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: Repository) : ViewModel() {
    val result : LiveData<PagingData<ListStoryItem>> = repository.getStories().cachedIn(viewModelScope)
}