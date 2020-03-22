package com.example.watching_android.database

import android.app.Activity
import android.content.Context
import com.example.watching_android.R
import com.example.watching_android.model.UserRegistration
import com.example.watching_android.model.UserInfoData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.watching_android.MainActivity
import com.example.watching_android.model.NickNameData
import com.example.watching_android.model.PhoneClass
import retrofit2.http.POST

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
        retrofitConnection.createUser(PhoneClass(userInfoData.phone_number))
            .enqueue(object : Callback<UserRegistration>{
                override fun onFailure(call: Call<UserRegistration>, t: Throwable) {
                    mainActivity.getResponse(null, activity)
                }

                override fun onResponse(call: Call<UserRegistration>, response: Response<UserRegistration>) {

                    val api_key = response.body()?.api_key ?:""
                    val id : Int = response.body()?.id ?:0
                    resUserInfoData.id = id
                    resUserInfoData.api_key = api_key
                   mainActivity.getResponse(resUserInfoData, activity)
                }

            })

    }

    fun registerNickName(nickName: NickNameData){
        val retrofitConnection = RetrofitConnection()
        retrofitConnection.updateNickName(nickName)
            .enqueue(object : Callback<UserRegistration>{
                override fun onFailure(call: Call<UserRegistration>, t: Throwable) {
                    val str = "failed"
                }

                override fun onResponse(call: Call<UserRegistration>, response: Response<UserRegistration>) {
                   val str = "Worked"
                }

            })

    }

}
