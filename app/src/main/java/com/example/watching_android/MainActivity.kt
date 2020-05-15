package com.example.watching_android

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.TelephonyManager
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentTransaction
import androidx.preference.PreferenceManager
import androidx.viewpager.widget.ViewPager
import com.example.watching_android.database.Preferences
import com.example.watching_android.database.RetrofitFunctions
import com.example.watching_android.model.NickNameData
import com.example.watching_android.model.PhoneNumber
import com.example.watching_android.model.UserInfoData
import com.example.watching_android.model.UserRegistration
import com.example.watching_android.ui.NickNameFragment
import com.example.watching_android.ui.SectionsPagerAdapter
import com.google.android.material.tabs.TabLayout
import java.util.*

class MainActivity : AppCompatActivity() {
    // Declaring constant of permission READ_PHONE_STATE
    companion object{
        const val READ_PHONE_STATE = 100
        lateinit var transaction: FragmentTransaction
        lateinit var nickNameFragment: NickNameFragment
    }



    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Reading nickName from shared preferences, if it is not set then app would be called from beginning else tablayout
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        val nickName = sharedPref.getString(Preferences.KEY_NICKNAME, null)
        Preferences.apiKey = sharedPref.getString(Preferences.KEY_API_KEY, null).toString()
        Preferences.userId = sharedPref.getInt(Preferences.KEY_USER_ID, 0)

        // If nickName is not being set
        if (nickName.isNullOrBlank()){
            // READ_PHONE_STATE Permission code
            checkPermission(Manifest.permission.READ_PHONE_STATE, READ_PHONE_STATE)
            supportActionBar?.elevation = 0F
            nickNameFragment = NickNameFragment()
            transaction = supportFragmentManager.beginTransaction()
            transaction.add(R.id.mainActivity, nickNameFragment)
        }
        // Set the tab
        else{
            supportActionBar?.elevation = 0F
            Preferences.apiKey
            val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
            val viewPager: ViewPager = findViewById(R.id.view_pager)
            viewPager.adapter = sectionsPagerAdapter
            val tabs: TabLayout = findViewById(R.id.tabs)
            tabs.setupWithViewPager(viewPager)
            // This statement is setting default tab
            viewPager.setCurrentItem(2,false)

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


    private fun checkPref(setOrNot: Boolean, activity: Activity) {

        if (!setOrNot) {
            Toast.makeText(activity, "Unable to set Shared Preferences", Toast.LENGTH_LONG).show()
        } else {
            val sharedPref = activity.getPreferences(Context.MODE_PRIVATE) ?: return
            val apiKey = sharedPref.getString(Preferences.KEY_API_KEY, null)
            val id = sharedPref.getInt(Preferences.KEY_USER_ID, 0)
            val nickName = sharedPref.getString(Preferences.KEY_NICKNAME, null)
            Toast.makeText(activity, "$apiKey $id$nickName", Toast.LENGTH_LONG).show()

                if(!activity.isFinishing && !activity.isDestroyed ){
                try{

                    transaction.replace(R.id.mainActivity, nickNameFragment)
                    transaction.commit()

                } catch (e: java.lang.Exception){
                    print(e.printStackTrace())
                }

            }
        }
    }

    /**
     * 携帯番号とＩＭＥＩ番号を取る機能
     */
    fun readPhoneNumber() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED){
            try{

                val tm = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                //Getting country code
                val countryID= tm.simCountryIso.toUpperCase(Locale.getDefault())
                /*
                val IMEI = tm.getImei(0)
                if(IMEI!=null)
                    Toast.makeText(this, "IMEI number: " + IMEI, Toast.LENGTH_LONG).show() */

                val telNumber = tm.line1Number
                // 電話場番号はＮｕｌｌではない場合は次に進む
                if(telNumber!=null) {
                    val userInfo =
                        UserInfoData(phone_number = PhoneNumber(countryID, telNumber))
                    RetrofitFunctions.registerUser(userInfo, this)
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




    /**
     * This function gets the response containing id and api key
     */
    fun getResponse(userRegistration: UserRegistration?,nickNameData: NickNameData?, activity: Activity){

        if(userRegistration!=null){
            val setOrNot = Preferences.setPreferences(userRegistration, null, activity)
            this.checkPref(setOrNot, activity)

        }
        else if(nickNameData!=null){
            Preferences.setPreferences(null, nickNameData, activity)
            val startIntent: Intent? = Intent(activity, MainActivity::class.java)
            startIntent!!.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            activity.startActivity(startIntent)

        }
        else{
            //TODO: DECIDE what to do when there is server side error
        }

    }

}
