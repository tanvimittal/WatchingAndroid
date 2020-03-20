package com.example.watching_android.database

import com.example.watching_android.model.UserRegistration
import retrofit2.Call
import retrofit2.http.*

interface JsonPlaceHolder {

    @GET("event")
    fun getPosts(): Call<List<POST>>

    @FormUrlEncoded
    @POST("users")
    fun createUser(@Field("phone_number") phone_number : String): Call<UserRegistration>

    @FormUrlEncoded
    @PUT( "users")
    fun updateNickName(@Field("id") id: String, @Field("nickName") nickName: String): Call<UserRegistration>

}