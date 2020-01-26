package com.example.watching_android

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST

interface JsonPlaceHolder {

    @GET("posts")
    fun getPosts(): Call<List<POST>>
}