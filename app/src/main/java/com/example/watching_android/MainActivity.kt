package com.example.watching_android

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.watching_android.Database.sendJsonObject


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
                    Toast.makeText(this, "Telephone number: " + telNumber, Toast.LENGTH_LONG).show()
                    sendJsonObject()
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

}
