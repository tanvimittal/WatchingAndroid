package com.example.watching_android.database

import android.app.Activity
import androidx.preference.PreferenceManager
import com.example.watching_android.MainActivity
import com.example.watching_android.model.NickNameData
import com.example.watching_android.model.UserRegistration
import java.lang.Exception

/**
 * This class is used to get and set shared preferences
 */
object Preferences{
    // https://qiita.com/ryo_mm2d/items/b90dbbd726183c20c14c#%E3%82%AD%E3%83%BC%E3%82%92%E3%81%A9%E3%81%AE%E3%82%88%E3%81%86%E3%81%AB%E7%AE%A1%E7%90%86%E3%81%99%E3%81%B9%E3%81%8D%E3%81%8B
    const val KEY_USER_ID = "user_id"
    const val KEY_API_KEY = "api_key"
    const val KEY_NICKNAME = "nickname"

    var userId = -1
    var apiKey = ""  // 取得できていないときに "" (空文字) で x-api-key を送ってしまう (検出しにくいバグを埋め込む)

    /*
     * 何を set するメソッド？
     *
     * - IN と OUT を明確に！
     * - set と get の対応関係がとれていない
     *   set は nickname をあつかっているけど get は nickname をあつかっていない etc.
     * - 例外処理必要か？ return を見てないところもある
     */
    fun setPreferences(userRegistration: UserRegistration?,nickNameData: NickNameData?, activity: Activity) : Boolean{

        val mainActivity = MainActivity()
        var res = true
        // If userRegistration object is not null then write API key and user id in shared preferences
        if (userRegistration!=null){
            try{
                val sharedPref = PreferenceManager.getDefaultSharedPreferences(activity)
                with (sharedPref.edit()) {
                    putString(KEY_API_KEY, userRegistration.api_key)
                    putInt(KEY_USER_ID, userRegistration.id)
                    commit()
                    if(userRegistration!=null){
                        userId = userRegistration.id
                        apiKey = userRegistration.api_key
                    }
                }
                apiKey = sharedPref.getString(KEY_API_KEY, "").toString()
            } catch (e :Exception){
                res = false
            }
        }

        // If userRegistration object is not null then write API key and user id in shared preferences
        if (nickNameData!=null){
            try{
                val sharedPref = PreferenceManager.getDefaultSharedPreferences(activity)
                with (sharedPref.edit()) {
                    putString(KEY_NICKNAME, nickNameData.nickname)
                    commit()
                }
            } catch (e :Exception){
                res = false
            }
        }


            return res
    }

    /*
     * このメソッドは必要か？
     */
    /**
     * This function returns api key and id
     */
    fun getPreferences(activity: Activity) : UserRegistration{
        var userRegistration = UserRegistration(0,"")
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(activity)
        userRegistration.id = sharedPref.getInt(KEY_USER_ID, 0)
        userRegistration.api_key = sharedPref.getString(KEY_API_KEY, "").toString()
        return userRegistration
    }
}

