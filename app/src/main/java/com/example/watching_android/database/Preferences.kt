package com.example.watching_android.database

import android.app.Activity
import android.content.Context
import com.example.watching_android.MainActivity
import com.example.watching_android.R
import com.example.watching_android.model.NickNameData
import com.example.watching_android.model.UserRegistration
import java.lang.Exception

/**
 * This class is used to get and set shared preferences
 */
object Preferences{

    var USERID = -1
    var APIKEY = ""
    fun setPreferences(userRegistration: UserRegistration?,nickNameData: NickNameData?, activity: Activity) : Boolean{

        val mainActivity = MainActivity()
        var res = true
        // If userRegistration object is not null then write API key and user id in shared preferences
        if (userRegistration!=null){
            try{
                val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
                with (sharedPref.edit()) {
                    putString(activity.getString(com.example.watching_android.R.string.api_key), userRegistration.api_key)
                    putInt(activity.getString(com.example.watching_android.R.string.ID), userRegistration.id)
                    commit()
                    if(userRegistration!=null){
                        USERID = userRegistration.id
                        APIKEY = userRegistration.api_key
                    }
                }
                APIKEY = sharedPref.getString(activity.getString(R.string.api_key), "").toString()
            } catch (e :Exception){
                res = false
            }
        }

        // If userRegistration object is not null then write API key and user id in shared preferences
        if (nickNameData!=null){
            try{
                val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
                with (sharedPref.edit()) {
                    putString(activity.getString(com.example.watching_android.R.string.nickname), nickNameData.nickname)
                    commit()
                }
            } catch (e :Exception){
                res = false
            }
        }


            return res
    }


    /**
     * This function returns api key and id
     */
    fun getPreferences(activity: Activity) : UserRegistration{
        var userRegistration = UserRegistration(0,"")
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        userRegistration.id = sharedPref.getInt(activity.getString(R.string.ID), 0)
        userRegistration.api_key = sharedPref.getString(activity.getString(R.string.api_key), "").toString()
        return userRegistration
    }


}