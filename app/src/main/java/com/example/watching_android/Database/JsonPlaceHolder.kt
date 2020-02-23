package com.example.watching_android.database

import com.example.watching_android.model.UserRegistration
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface JsonPlaceHolder {

    @GET("event")
    fun getPosts(): Call<List<POST>>

    @FormUrlEncoded
    @POST("users")
    fun createUser(@Field("phone_number") phone_number : String): Call<UserRegistration>


}