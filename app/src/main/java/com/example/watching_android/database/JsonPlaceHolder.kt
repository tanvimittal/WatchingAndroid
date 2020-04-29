package com.example.watching_android.database

import com.example.watching_android.model.*
import retrofit2.Call
import retrofit2.http.*
import java.util.*

interface JsonPlaceHolder {

    @GET("event")
    fun getPosts(): Call<List<POST>>

    @POST("users")
    fun createUser(@Body userInfoData: UserInfoData): Call<UserRegistration>

    @PUT( "users")
    fun updateNickName(@Body nickName: NickNameData): Call<UserRegistration>

    @GET("events")
    fun getMessgaes(): Call<List<Messages>>
    //fun getMessgaes(@Header("x-api-key") xApiKey: String): Call<List<Messages>>

}