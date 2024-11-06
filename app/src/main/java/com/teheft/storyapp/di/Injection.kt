package com.teheft.storyapp.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.teheft.storyapp.data.Repository
import com.teheft.storyapp.data.pref.UserPreference
import com.teheft.storyapp.data.remote.retrofit.ApiConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context, dataStore: DataStore<Preferences>): Repository{
        val userPreference = UserPreference.getInstance(dataStore)
//        val user = runBlocking { userPreference.getSession().first().token }
        val apiService = ApiConfig.getApiService(context)
        return Repository.getInstance(userPreference, apiService)
    }
}