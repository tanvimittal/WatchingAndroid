package com.example.watching_android.Database

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST

interface JsonPlaceHolder {

    @GET("rensou.json")
    fun getPosts(): Call<List<POST>>
}