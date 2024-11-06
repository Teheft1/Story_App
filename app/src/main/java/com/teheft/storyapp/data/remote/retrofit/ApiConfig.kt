package com.teheft.storyapp.data.remote.retrofit

import android.content.Context
import android.util.Log
import androidx.datastore.dataStore
import com.teheft.storyapp.BuildConfig
import com.teheft.storyapp.data.pref.UserPreference
import com.teheft.storyapp.data.pref.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory



class ApiConfig {
    companion object {
        fun getApiService(context: Context): ApiService {
            val userPreference = UserPreference.getInstance(context.dataStore)
//            Log.d("tokenApiConfig", "$token")

            val authInterceptor = Interceptor{ chain ->
                val token = runBlocking {
                    userPreference.getSession().first().token
                }
                Log.d("tokenApiConfig", "$token")
                val req = chain.request()
                val requestHeader = req.newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
                chain.proceed(requestHeader)
            }

            val loggingInterceptor =
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(authInterceptor)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()

            return retrofit.create(ApiService::class.java)
        }
    }
}