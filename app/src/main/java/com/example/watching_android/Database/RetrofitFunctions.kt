package com.example.watching_android.database

import com.example.watching_android.model.UserRegistration
import com.example.watching_android.model.UserInfoData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 *  This class has functions related to retrofit
 */
object RetrofitFunctions{

    /**
     * This function is used to register users on database
     */
    fun registerUser(userInfoData: UserInfoData){
        val retrofitConnection = RetrofitConnection()
        retrofitConnection.createUser(userInfoData.phone_number)
            .enqueue(object : Callback<UserRegistration>{
                override fun onFailure(call: Call<UserRegistration>, t: Throwable) {
                    var str = "Failure"
                }

                override fun onResponse(call: Call<UserRegistration>, response: Response<UserRegistration>) {

                    //TODO; onSuccess set the id and api_key as app data
                    var str = "Success"
                }

            })
    }
}
