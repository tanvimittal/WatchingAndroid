package com.example.watching_android

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.PathPermission
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.TelephonyManager
import android.util.Log
import android.view.accessibility.AccessibilityManager
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import java.lang.Exception
import java.lang.System.out
//import java.util.jar.Manifest
import android.Manifest.permission;
import okhttp3.MediaType
import okhttp3.MediaType.Companion.parse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.watching_android.JsonPlaceHolder


class MainActivity : AppCompatActivity() {

    // Declaring constant of permission READ_PHONE_STATE
    companion object{
        const val READ_PHONE_STATE = 100
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // READ_PHONE_STATE Permission code
        checkPermission(Manifest.permission.READ_PHONE_STATE, MainActivity.READ_PHONE_STATE)

        //If the app has permission then extract IMEI and phone number
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED){
            try{

                val tm = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                val IMEI = tm.getImei(0)
                if(IMEI!=null)
                    Toast.makeText(this, "IMEI number: " + IMEI, Toast.LENGTH_LONG).show()

                val telNumber = tm.line1Number
                if (telNumber != null)
                    Toast.makeText(this, "Phone number: " + telNumber,
                        Toast.LENGTH_LONG).show()

            } catch (ex: Exception){
                Log.e("", ex.message)
            }
        }
    }

    /**
     * Function to check if the permission is granted by user or not, if the permission is not granted then ask for permission
     */
    fun checkPermission(permission: String, requestCode: Int){
        if(ContextCompat.checkSelfPermission(this,
                                   permission) == PackageManager.PERMISSION_DENIED) {

            //Requesting the permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(permission),
                requestCode
            )
        }else{
            //Permission granted
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

        if(requestCode == MainActivity.READ_PHONE_STATE){
            if(grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,
                          "Phone Permission Granted",
                           Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }

    }

    fun sendJsonObject(){
        // Creating Retrofit's instance
         val retrofit =  Retrofit.Builder()
            .baseUrl(" http://rensou.akoba.xyz/rensou.json")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

        // Creating JsonPlaceHolder's object
       // jsonPlaceHolder Json

    }

}
