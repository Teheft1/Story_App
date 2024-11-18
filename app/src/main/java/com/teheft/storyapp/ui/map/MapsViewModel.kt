package com.teheft.storyapp.ui.map

import androidx.lifecycle.ViewModel
import com.teheft.storyapp.data.Repository

class MapsViewModel(private val repository: Repository): ViewModel() {
    fun getStories() = repository.getStoriesWithLocation()
}