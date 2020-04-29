package com.example.watching_android.database

import android.app.Activity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.watching_android.MainActivity
import com.example.watching_android.model.*

/**
 *  This class has functions related to retrofit
 */
object RetrofitFunctions{

    /**
     * This function is used to register users on database
     */
    fun registerUser(userInfoData: UserInfoData, activity: Activity){

        val mainActivity = MainActivity()
        val retrofitConnection = RetrofitConnection()
        var resUserInfoData = UserRegistration(0, "")
        retrofitConnection.createUser(userInfoData)
            .enqueue(object : Callback<UserRegistration>{
                override fun onFailure(call: Call<UserRegistration>, t: Throwable) {
                    mainActivity.getResponse(null, null, activity)
                }

                override fun onResponse(call: Call<UserRegistration>, response: Response<UserRegistration>) {

                    val api_key = response.body()?.api_key ?:""
                    val id : Int = response.body()?.id ?:0
                    resUserInfoData.id = id
                    resUserInfoData.api_key = api_key
                   mainActivity.getResponse(resUserInfoData, null, activity)
                }

            })

    }

    /**
     * This function is calling retrofit API and saving data as user shared preference
     */
    fun registerNickName(nickName: NickNameData, activity: Activity){
        val mainActivity = MainActivity()
        val retrofitConnection = RetrofitConnection()
        retrofitConnection.updateNickName(nickName)
            .enqueue(object : Callback<UserRegistration>{
                override fun onFailure(call: Call<UserRegistration>, t: Throwable) {
                    mainActivity.getResponse(null, null, activity)
                }

                override fun onResponse(call: Call<UserRegistration>, response: Response<UserRegistration>) {
                    mainActivity.getResponse(null, nickName, activity)
                }

            })

    }

}
