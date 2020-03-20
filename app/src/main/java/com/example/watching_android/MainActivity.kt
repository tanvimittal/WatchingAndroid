package com.example.watching_android

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.TelephonyManager
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.watching_android.database.RetrofitFunctions
import com.example.watching_android.model.UserInfoData
import android.content.SharedPreferences
import androidx.annotation.CheckResult
import com.example.watching_android.database.Preferences
import com.example.watching_android.model.UserRegistration
import com.example.watching_android.ui.NickNameFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    // Declaring constant of permission READ_PHONE_STATE
    companion object{
        const val READ_PHONE_STATE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // READ_PHONE_STATE Permission code
        checkPermission(Manifest.permission.READ_PHONE_STATE, READ_PHONE_STATE)
        val btn = findViewById<Button>(R.id.btn)
        btn.setOnClickListener(View.OnClickListener {
            Toast.makeText(this, "Hello", Toast.LENGTH_LONG).show()
            //RetrofitFunctions.registerUser(UserInfoData(), this)
            checkPref(true, this)

        })

    }

    /**
     * 携帯番号とＩＭＥＩ番号を取る機能
     */
    fun readPhoneNumber() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED){
            try{

                val tm = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                /*
                val IMEI = tm.getImei(0)
                if(IMEI!=null)
                    Toast.makeText(this, "IMEI number: " + IMEI, Toast.LENGTH_LONG).show() */

                val telNumber = tm.line1Number
                // 電話場番号はＮｕｌｌではない場合は次に進む
                if(telNumber!=null) {
                    var userInfo =
                        UserInfoData(phone_number = telNumber)
                    val userRegistration = RetrofitFunctions.registerUser(userInfo, this)
                    this.getResponse(userRegistration, this)
                } else{
                    // Alert Boxを表示してアプリを終了する。
                    val alertDialog: android.app.AlertDialog? = android.app.AlertDialog.Builder(this@MainActivity).create()
                    if (alertDialog != null) {
                        alertDialog.setTitle(this.resources.getString(R.string.alertTelTitle))
                        alertDialog.setMessage(this.resources.getString(R.string.alertTelMsg))
                        alertDialog.setButton(
                            AlertDialog.BUTTON_NEUTRAL, "OK",
                            DialogInterface.OnClickListener { dialog, which -> finish() })
                        alertDialog.show()
                    }
                }

            } catch (ex: Exception){
                Log.e("", ex.message)
            }
        }
        // If the permission is not there
        else{

            // requestPhonePermission
            checkPermission(Manifest.permission.READ_PHONE_STATE, READ_PHONE_STATE)
        }
    }

    /**
     * Function to check if the permission is granted by user or not,
     * if the permission is not granted then ask for permission
     */
    fun checkPermission(permission: String, requestCode: Int){
        if (ContextCompat.checkSelfPermission(this,
                                   permission) == PackageManager.PERMISSION_DENIED) {

            //Requesting the permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(permission),
                requestCode
            )
        } else{
            readPhoneNumber()
        }
    }

    /**
     * This function is called when user accepts or declines the permission
     * Param: requestCode -> Used to check which permission called this function
     * requestCode is provided when user is prompt for permission
     */
    override fun onRequestPermissionsResult(requestCode: Int,
                                   @NonNull permissions: Array<String>,
                                   @NonNull grantResults: IntArray){

        super.onRequestPermissionsResult(requestCode,
                                         permissions,
                                         grantResults)

        if (requestCode == READ_PHONE_STATE) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                readPhoneNumber()
            } else {
                // Close the app
                finish()
            }
        }

    }

    fun checkPref(setOrNot: Boolean, activity: Activity) {

        if (setOrNot == false) {
            Toast.makeText(activity, "Unable to set Shared Preferences", Toast.LENGTH_LONG).show()
        } else {
            val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
            val apiKey = sharedPref.getString(activity.getString(R.string.api_key), "")
            val id = sharedPref.getInt(activity.getString(R.string.ID), 0)
            Toast.makeText(activity, apiKey + " " + id, Toast.LENGTH_LONG).show()

            if(!activity.isFinishing){
                val transaction = supportFragmentManager.beginTransaction()
                val nickNameFragment = NickNameFragment()
                transaction.add(R.id.mainActivity, nickNameFragment)
                transaction.commit()
            }



        }
    }

    /**
     * This function gets the response containing id and api key
     */
    fun getResponse(userRegistration: UserRegistration?, activity: Activity){

        if(userRegistration!=null){
            val setOrNot = Preferences.setPreferences(userRegistration, activity)
            this.checkPref(setOrNot, activity)

        } else{
            //TODO: DECIDE what to do when there is server side error
        }

    }

}
