package com.example.watching_android.database

import com.example.watching_android.model.Messages
import com.example.watching_android.model.NickNameData
import com.example.watching_android.model.PhoneClass
import com.example.watching_android.model.UserRegistration
import retrofit2.Call
import retrofit2.http.*
import java.util.*

interface JsonPlaceHolder {

    @GET("event")
    fun getPosts(): Call<List<POST>>

    @POST("users")
    fun createUser(@Body phone_number : PhoneClass): Call<UserRegistration>

    @PUT( "users")
    fun updateNickName(@Body nickName: NickNameData): Call<UserRegistration>

    @GET("events")
    fun getMessgaes():Call<List<Messages>>

}