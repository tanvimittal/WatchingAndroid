package com.example.watching_android.database

import com.example.watching_android.constants.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Watching API.
 *
 * - Singleton にする
 * - https://www.youtube.com/watch?v=w6MvFXz5ecA
 */
object WatchingApi {
    val service: WatchingApiService by lazy {
        // 通信ログ出力
        // https://tech.mti.co.jp/entry/2020/03/31/163321
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY

        // 通信ログ出力のためにデフォルト OkHttpClient 以外を利用する
        val httpClient = OkHttpClient.Builder().addInterceptor(interceptor).build()

        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(Constants.baseUrl)
            .client(httpClient)
            .build()

        retrofit.create(WatchingApiService::class.java)
    }
}