package com.teheft.storyapp.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.teheft.storyapp.data.Repository
import com.teheft.storyapp.di.Injection
import com.teheft.storyapp.ui.MainViewModel
import com.teheft.storyapp.ui.detail.DetailViewModel
import com.teheft.storyapp.ui.home.HomeViewModel
import com.teheft.storyapp.ui.login.LoginViewModel
import com.teheft.storyapp.ui.map.MapsViewModel
import com.teheft.storyapp.ui.upload.UploadViewModel

class ViewModelFactory(private val repository: Repository): ViewModelProvider.NewInstanceFactory(){
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(MainViewModel::class.java)){
            return MainViewModel(repository) as T
        }else if(modelClass.isAssignableFrom(LoginViewModel::class.java)){
            return LoginViewModel(repository) as T
        }else if(modelClass.isAssignableFrom(HomeViewModel::class.java)){
            return HomeViewModel(repository) as T
        }else if(modelClass.isAssignableFrom(UploadViewModel::class.java)){
            return UploadViewModel(repository) as T
        }else if(modelClass.isAssignableFrom(DetailViewModel::class.java)){
            return DetailViewModel(repository) as T
        }else if(modelClass.isAssignableFrom(MapsViewModel::class.java)){
            return MapsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown View Model Class ${modelClass.name}")
    }

    companion object{
        @Volatile
        private var instance: ViewModelFactory? = null
        fun getInstance(context: Context, dataStore: DataStore<Preferences>): ViewModelFactory =
            instance ?: synchronized(this){
                instance ?: ViewModelFactory(Injection.provideRepository(context, dataStore))
            }.also { instance = it }
    }

}