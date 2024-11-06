package com.teheft.storyapp.ui.detail

import androidx.lifecycle.ViewModel
import com.teheft.storyapp.data.Repository

class DetailViewModel(private val repository: Repository): ViewModel() {
    fun detailStory(id: String) = repository.getDetailStories(id)
}