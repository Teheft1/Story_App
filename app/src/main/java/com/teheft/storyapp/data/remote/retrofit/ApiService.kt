package com.teheft.storyapp.data.remote.retrofit

import com.teheft.storyapp.data.remote.response.DetailResponse
import com.teheft.storyapp.data.remote.response.ListStoriesResponse
import com.teheft.storyapp.data.remote.response.LoginResponse
import com.teheft.storyapp.data.remote.response.RegisterResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {

    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<RegisterResponse>

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<LoginResponse>

    @Multipart
    @POST("stories")
    suspend fun uploadStories(
        @Part ("description") description: RequestBody,
        @Part file: MultipartBody.Part
    ): Response<RegisterResponse>

    @GET("stories")
    suspend fun getStories(): Response<ListStoriesResponse>

    @GET("stories/{id}")
    suspend fun getDetailStories(
        @Path ("id") id: String
    ): Response<DetailResponse>
}