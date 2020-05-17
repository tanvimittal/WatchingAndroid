package com.example.watching_android.database

import android.app.Activity
import androidx.preference.PreferenceManager
import com.example.watching_android.model.UserForUpdate
import com.example.watching_android.model.UserWithApiKey

/**
 * This class is used to get and set shared preferences
 */
object Preferences {
    // https://qiita.com/ryo_mm2d/items/b90dbbd726183c20c14c#%E3%82%AD%E3%83%BC%E3%82%92%E3%81%A9%E3%81%AE%E3%82%88%E3%81%86%E3%81%AB%E7%AE%A1%E7%90%86%E3%81%99%E3%81%B9%E3%81%8D%E3%81%8B
    const val KEY_USER_ID = "user_id"
    const val KEY_API_KEY = "api_key"
    const val KEY_NICKNAME = "nickname"

    // 取得済みの場合は 0 以上
    var userId = -1

    // 取得済みの場合は null 以外
    var apiKey: String? = null

    /**
     * This function is called to set ID and Api key in shared preferences
     * @param : userWithApiKey - Contains the API key and user id to be written in shared preferences
     *        : activity - Object of Activity
     */
    fun setApiIdInPreference(userWithApiKey: UserWithApiKey, activity: Activity){

        // If userRegistration object is not null then write API key and user id in shared preferences
            val sharedPref = PreferenceManager.getDefaultSharedPreferences(activity)
            with (sharedPref.edit()) {
                putString(KEY_API_KEY, userWithApiKey.apiKey)
                putInt(KEY_USER_ID, userWithApiKey.id)
                commit()
                userId = userWithApiKey.id
                apiKey = userWithApiKey.apiKey
            }
            apiKey = sharedPref.getString(KEY_API_KEY, "").toString()
        }


    /**
     * This function is used to set Nickname in preference
     * @param: userForUpdate - Contains nickname to be updated
     *       : activity - Object of Activity
     */
    fun setNickNamePreference(userForUpdate: UserForUpdate, activity: Activity) {
        // If Nickname is not null set nickname
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(activity)
        with (sharedPref.edit()) {
        putString(KEY_NICKNAME, userForUpdate.nickname)
        commit()
        }
    }

}

