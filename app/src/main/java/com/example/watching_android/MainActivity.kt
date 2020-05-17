package com.example.watching_android

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.TelephonyManager
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import androidx.viewpager.widget.ViewPager
import com.example.watching_android.database.Preferences
import com.example.watching_android.database.RetrofitFunctions
import com.example.watching_android.model.UserForUpdate
import com.example.watching_android.model.PhoneNumber
import com.example.watching_android.model.UserForRegistration
import com.example.watching_android.model.UserWithApiKey
import com.example.watching_android.ui.NicknameFragment
import com.example.watching_android.ui.SectionsPagerAdapter
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity() {
    // Declaring constant of permission READ_PHONE_STATE
    companion object {
        const val READ_PHONE_STATE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Reading nickName from shared preferences, if it is not set then app would be called from beginning else tablayout
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        val nickName = sharedPref.getString(Preferences.KEY_NICKNAME, null)
        Preferences.apiKey = sharedPref.getString(Preferences.KEY_API_KEY, null)
        Preferences.userId = sharedPref.getInt(Preferences.KEY_USER_ID, -1)

        // If API key is not set
        if (Preferences.apiKey == null) {
            // READ_PHONE_STATE Permission code
            if (checkPermission(Manifest.permission.READ_PHONE_STATE, READ_PHONE_STATE)) {
                // 電話番号取得して、ユーザー登録 API 送信
                val phoneNumber = readPhoneNumber()
                if (phoneNumber == null) {
                    showFinishAlertBox()
                } else {
                    val userInfo = UserForRegistration(phoneNumber = phoneNumber)
                    RetrofitFunctions.registerUser(userInfo, this)
                }
            }
        } else if (Preferences.apiKey != null && nickName == null) {
            supportActionBar?.elevation = 0F
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.mainActivity, NicknameFragment())
            transaction.commit()
        } else {
            // Set the tab
            supportActionBar?.elevation = 0F
            val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
            val viewPager = findViewById<ViewPager>(R.id.view_pager)
            viewPager.adapter = sectionsPagerAdapter
            val tabs: TabLayout = findViewById(R.id.tabs)
            tabs.setupWithViewPager(viewPager)
            // This statement is setting default tab
            viewPager.setCurrentItem(2, false)
        }
    }


    /*
     * Option Menu.
     *
     * https://developer.android.com/guide/topics/ui/menus?hl=ja#options-menu
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.contact -> {
                Toast.makeText(this, "TODO: Contact", Toast.LENGTH_LONG).show()
                true
            }
            R.id.debug -> {
                val intent = Intent(this, DebugActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    /**
     * This function is called when user accepts or declines the permission
     * Param: requestCode -> Used to check which permission called this function
     * requestCode is provided when user is prompt for permission
     */
    override fun onRequestPermissionsResult(
        requestCode: Int, @NonNull permissions: Array<String>, @NonNull grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == READ_PHONE_STATE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 電話番号取得して、ユーザー登録 API 送信
                val phoneNumber = readPhoneNumber()
                if (phoneNumber == null) {
                    showFinishAlertBox()
                } else {
                    val userInfo = UserForRegistration(phoneNumber = phoneNumber)
                    RetrofitFunctions.registerUser(userInfo, this)
                }
            } else {
                // Close the app
                showFinishAlertBox()
                //finish()
            }
        }
    }


    /**
     * RetrofitFunctions.registerUser の結果受信.
     *
     * This function gets the response containing id and api key
     */
    fun onResponseRegisterUser(userWithApiKey: UserWithApiKey) {
        // TODO: そもそも、以下のメソッドは失敗することはなさそう
        val setOrNot = Preferences.setPreferences(userWithApiKey, null, this)

        if (setOrNot) {
            transitionNickNameInputScreen(this)
        } else {
            Toast.makeText(this, "Unable to set Shared Preferences", Toast.LENGTH_LONG).show()
        }
    }

    /**
     * RetrofitFunctions.registerNickName の結果受信 (NickNameFragment で送信している).
     */
    fun onResponseRegisterNickname(userForUpdate: UserForUpdate) {
        Preferences.setPreferences(null, userForUpdate, this)

        val startIntent = Intent(this, MainActivity::class.java)
        startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        this.startActivity(startIntent)
    }

    /**
     * RetrofitFunctions.registerUser
     * RetrofitFunctions.registerNickName
     * のエラー処理.
     */
    fun onErrorRegister() {
        //TODO: DECIDE what to do when there is server side error
    }


    /**
     * Function to check if the permission is granted by user or not,
     * if the permission is not granted then ask for permission
     *
     * @return true: Permission OK / false: Permission NG
     */
    private fun checkPermission(permission: String, requestCode: Int): Boolean {
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
            //Requesting the permission
            ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)

            return false
        } else {
            return true
        }
    }

    /**
     * 携帯番号とＩＭＥＩ番号を取る機能
     *
     * - パーミッションが許可されているときに呼ぶこと
     *
     * @return  null 以外: PhoneNumber / null: 取得失敗
     */
    private fun readPhoneNumber(): PhoneNumber? {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            throw IllegalStateException("readPhoneNumber() must not be called without permission")
        }

        val tm = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        //Getting country code
        val countryID = tm.simCountryIso.toUpperCase()
        /*
        val IMEI = tm.getImei(0)
        if(IMEI!=null)
            Toast.makeText(this, "IMEI number: " + IMEI, Toast.LENGTH_LONG).show() */

        val telNumber = tm.line1Number

        return if (telNumber != null) {
            PhoneNumber(countryID, telNumber)
        } else {
            null
        }
    }

    /**
     * Activity が有効ならニックネーム入力画面に遷移.
     *
     * - Activity が有効じゃないときは黙って何もしない
     */
    private fun transitionNickNameInputScreen(activity: Activity) {
        // デバッグ用
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(activity)
        val apiKey = sharedPref.getString(Preferences.KEY_API_KEY, null)
        val userId = sharedPref.getInt(Preferences.KEY_USER_ID, -1)
        Toast.makeText(activity, "$apiKey $userId", Toast.LENGTH_LONG).show()

        if (!activity.isFinishing && !activity.isDestroyed) {
            supportActionBar?.elevation = 0F
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.mainActivity, NicknameFragment())
            transaction.commit()
        }
    }

    private fun showFinishAlertBox() {
        // Alert Boxを表示してアプリを終了する。
        val alertDialog = android.app.AlertDialog.Builder(this).create()

        alertDialog.setTitle(this.resources.getString(R.string.alertTelTitle))
        alertDialog.setMessage(this.resources.getString(R.string.alertTelMsg))
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK") { _, _ -> finish() }

        alertDialog.show()
    }

}
