package com.example.watching_android.database

import android.app.Activity
import android.content.Context
import com.example.watching_android.MainActivity
import com.example.watching_android.model.UserRegistration
import java.lang.Exception

/**
 * This class is used to get and set shared preferences
 */
object Preferences{

    fun setPreferences(userRegistration: UserRegistration, activity: Activity){

        val mainActivity = MainActivity()
        var res = true
            try{
                val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
                with (sharedPref.edit()) {
                    putString(activity.getString(com.example.watching_android.R.string.api_key), userRegistration.api_key)
                    putInt(activity.getString(com.example.watching_android.R.string.ID), userRegistration.id)
                    commit()
                }
            } catch (e :Exception){
                res = false
            }

        mainActivity.checkPref(res, activity)
    }
}