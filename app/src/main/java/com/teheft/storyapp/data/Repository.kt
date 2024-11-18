package com.teheft.storyapp.data

import android.graphics.pdf.PdfDocument.Page
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.google.gson.Gson
import com.teheft.storyapp.data.pref.UserModel
import com.teheft.storyapp.data.pref.UserPreference
import com.teheft.storyapp.data.remote.response.DetailResponse
import com.teheft.storyapp.data.remote.response.ListStoriesResponse
import com.teheft.storyapp.data.remote.response.ListStoryItem
import com.teheft.storyapp.data.remote.response.RegisterResponse
import com.teheft.storyapp.data.remote.response.Story
import com.teheft.storyapp.data.remote.retrofit.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class Repository private constructor(
    private val userPreference: UserPreference,
    private val apiService: ApiService
){
    private val result = MediatorLiveData<Result<PagingData<ListStoryItem>>>()
    fun signUp(name: String, email: String, password: String)= liveData(Dispatchers.IO){
        emit(Result.Loading)
        try{
            val signUp = apiService.register(name, email, password)
            if(signUp.isSuccessful){
                val responses = signUp.body()?.message
                emit(Result.Success(responses.toString()))
            }
            else{
                val body = signUp.errorBody()?.string()
                val error = Gson().fromJson(body,RegisterResponse::class.java)
                emit(Result.Error("Login Failed: ${error.message}"))
            }
        }catch (e: SocketTimeoutException){
            emit(Result.Error(e.message.toString()))
        }catch (e: IOException){
            emit(Result.Error(e.message.toString()))
        }catch (e: HttpException){
            emit(Result.Error(e.message.toString()))
        }
    }

    fun login(email: String, password: String)= liveData(Dispatchers.IO){
            emit(Result.Loading)
            try{
                val login = apiService.login(email, password)
                if(login.isSuccessful){
                    val responses = login.body()?.loginResult

                    Log.d("user token","${responses?.token}")

                    val user = responses.let {
                        UserModel(responses?.name ?: "", responses?.userId ?: "", responses?.token ?: "")
                    }

                    userPreference.saveSession(user)

                    emit(Result.Success(login.body()?.message.toString()))
                } else{
                    val body = login.errorBody()?.string()
                    val error = Gson().fromJson(body,RegisterResponse::class.java)
                    emit(Result.Error("Login Failed: ${error.message}"))
                }
            }catch (e: SocketTimeoutException){
                emit(Result.Error(e.message.toString()))
            }catch (e: IOException){
                emit(Result.Error(e.message.toString()))
            }catch (e: HttpException){
                emit(Result.Error(e.message.toString()))
            }

    }

     fun uploadStories(description: RequestBody, file: MultipartBody.Part) = liveData(Dispatchers.IO){
         emit(Result.Loading)
         try{
             val uploadStory = apiService.uploadStories(description, file)
             if(uploadStory.isSuccessful){
                 val responses = uploadStory.body()?.message
                 emit(Result.Success(responses.toString()))
                 Log.d("repoResult", "$uploadStory")
             }else{
                 val body = uploadStory.errorBody()?.string()
                 val error = Gson().fromJson(body,RegisterResponse::class.java)
                 emit(Result.Error("Upload Error: ${error.message}"))
             }

         }catch (e: SocketTimeoutException){
             Log.d("repoResult", "${e.message}")
             emit(Result.Error(e.message.toString()))
         }catch (e: IOException){
             emit(Result.Error(e.message.toString()))
         }catch (e: HttpException){
             emit(Result.Error(e.message.toString()))
         }
    }

    fun getStories(): LiveData<PagingData<ListStoryItem>> {
         return Pager(
            config = PagingConfig(
                pageSize = 5,
                initialLoadSize = 5,
                prefetchDistance = 1
            ),
            pagingSourceFactory = {
                StoryPagingSource(apiService)
            }
        ).liveData
    }

    fun getStoriesWithLocation() = liveData(Dispatchers.IO) {
        emit(Result.Loading)

        try {
            val data = apiService.getStoriesWithLocation()

            if (data.isSuccessful){
                val responses = data.body()?.listStory
                val list = ArrayList<ListStoryItem>()

                responses?.forEach{
                    if(it != null){
                        list.add(it)
                    }
                }

                emit(Result.Success(responses))
            }else{
                val errorBody = data.errorBody()?.string()
                val error = Gson().fromJson(errorBody, RegisterResponse::class.java)
                emit(Result.Error(error.message.toString()))
            }
        }catch (e: Exception){
            Log.d("repo", "$e")
            emit(Result.Error(e.message.toString()))
        }
    }
//    Log.d("repos","Story loading")
//    emit(Result.Loading)
//    try{
//        val stories = apiService.getStories()
//        if(stories.isSuccessful){
//            Log.d("repos","Story Success")
//
//            val reponses = stories.body()?.listStory
//            val list = ArrayList<ListStoryItem>()
//
//            reponses?.forEach{
//                if (it != null) {
//                    list.add(it)
//                }
//            }
//
//            emit(Result.Success(list))
//        }else{
//            val errorBody = stories.errorBody()?.string()
//            val error = Gson().fromJson(errorBody, RegisterResponse::class.java)
//            emit(Result.Error(error.message.toString()))
//        }
//    }catch (e: UnknownHostException){
//        Log.d("repos","Story Error")
//        emit(Result.Error(e.message.toString()))
//    }catch (e: IOException){
//        Log.d("repos","Story Error")
//        emit(Result.Error(e.message.toString()))
//    }catch (e: HttpException){
//        Log.d("repos","Story Error")
//        emit(Result.Error(e.message.toString()))
//    }

    fun getDetailStories(id: String) = liveData(Dispatchers.IO){
            emit(Result.Loading)
            try{
                val detail = apiService.getDetailStories(id)
                val response = detail.body()?.story
                val story = Story(
                    response?.photoUrl,
                    response?.createdAt,
                    response?.name,
                    response?.description,
                    response?.lon,
                    response?.id,
                    response?.lat
                )
                emit(Result.Success(story))
            }catch (e: SocketTimeoutException){
                emit(Result.Error(e.message.toString()))
            }catch (e: IOException){
                emit(Result.Error(e.message.toString()))
            }catch (e: HttpException){
                emit(Result.Error(e.message.toString()))
            }
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logOut(){
        userPreference.logout()
    }

    companion object{
        @Volatile
        private var instance: Repository? = null
        fun getInstance(
            userPreference: UserPreference,
            apiService: ApiService
        ): Repository =
            instance ?: synchronized(this){
                instance ?: Repository(userPreference, apiService)
            }.also { instance = it }
    }
}