package com.example.watching_android.database

import com.example.watching_android.constants.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Watching API.
 *
 * - Singleton にする
 * - https://www.youtube.com/watch?v=w6MvFXz5ecA
 */
object WatchingApi {
    val client: JsonPlaceHolder by lazy {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(Constants.baseUrl)
            .build()

        retrofit.create(JsonPlaceHolder::class.java)
    }
}