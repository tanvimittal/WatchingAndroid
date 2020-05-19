package jp.kyuuki.watching.database

import jp.kyuuki.watching.constants.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * This function is returning retrofit's object
 */

fun RetrofitConnection(): WatchingApiService{
        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(WatchingApiService::class.java)
}
