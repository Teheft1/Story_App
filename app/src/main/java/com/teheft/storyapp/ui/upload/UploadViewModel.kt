package com.teheft.storyapp.ui.upload


import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.teheft.storyapp.data.Repository
import com.teheft.storyapp.data.Result
import okhttp3.MultipartBody
import okhttp3.RequestBody

class UploadViewModel(private val repository: Repository): ViewModel() {
    fun uploadStory(description: RequestBody, file: MultipartBody.Part): LiveData<Result<String>> {
        return repository.uploadStories(description, file)
    }
}