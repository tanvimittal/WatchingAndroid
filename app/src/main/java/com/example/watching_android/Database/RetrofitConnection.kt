package com.example.watching_android.database

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.watching_android.constants.Constants

/**
 * This function is returning retrofit's object
 */

fun RetrofitConnection(): JsonPlaceHolder{
        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(JsonPlaceHolder::class.java)
}
