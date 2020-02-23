package com.example.watching_android.Database

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.POST

/**
 * This function is used to connect to retrofit.
 */
fun sendJsonObject(){
    // Creating Retrofit's instance
    val retrofit =  Retrofit.Builder()
        .baseUrl(" http://rensou.akoba.xyz/")//
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // Creating JsonPlaceHolder's object
    val service = retrofit.create(JsonPlaceHolder::class.java)
    val call = service.getPosts()


    call.enqueue(object : Callback<List<POST>> {

        override fun onResponse(call: Call<List<POST>>, response: Response<List<POST>>) {

            //var str = "Success"

        }

        override  fun onFailure(call: Call<List<POST>>, throwable: Throwable){

            //var str = "failure"
        }
    })
    object {

        var BaseUrl = "http://rensou.akoba.xyz/"
    }

}