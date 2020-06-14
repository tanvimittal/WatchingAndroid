package jp.kyuuki.watching

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.telephony.TelephonyManager
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import jp.kyuuki.watching.database.Preferences
import jp.kyuuki.watching.database.RetrofitFunctions
import jp.kyuuki.watching.database.WatchingApi
import jp.kyuuki.watching.model.*
import jp.kyuuki.watching.ui.NicknameFragment
import jp.kyuuki.watching.ui.SectionsPagerAdapter
import jp.kyuuki.watching.utility.MyFirebaseMessagingService


class MainActivity : AppCompatActivity() {
    // Declaring constant of permission READ_PHONE_STATE
    companion object {
        const val READ_PHONE_STATE = 100
        lateinit var progressBarMainActivity : ProgressBar
    }



    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // API 初期化 (ここだけで本当に大丈夫？)
        WatchingApi.setBaseUrl(getString(R.string.api_base_url))

        //taking constraint's layout's id
        val constraintLayout = findViewById<ConstraintLayout>(R.id.mainActivity)

        progressBarMainActivity = ProgressBar(this)
        progressBarMainActivity.visibility = View.GONE

        // Placing progress bar in centre of screen
        val params = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            ConstraintLayout.LayoutParams.MATCH_PARENT
        ).apply {
            topToTop = R.id.mainActivity
            bottomToBottom = R.id.mainActivity
            leftToLeft = R.id.mainActivity
            rightToRight = R.id.mainActivity
        }
        progressBarMainActivity.layoutParams = params

        constraintLayout.addView(progressBarMainActivity)

        // Reading nickName from shared preferences, if it is not set then app would be called from beginning else tablayout
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        val nickName = sharedPref.getString(Preferences.KEY_NICKNAME, null)
        Preferences.apiKey = sharedPref.getString(Preferences.KEY_API_KEY, null)
        Preferences.userId = sharedPref.getInt(Preferences.KEY_USER_ID, -1)
        Preferences.fcmToken = sharedPref.getString(Preferences.KEY_FCM_TOKEN, null)

        // If API key is not set
        if (Preferences.apiKey == null) {
            // READ_PHONE_STATE Permission code
            if (checkPermission(Manifest.permission.READ_PHONE_STATE, READ_PHONE_STATE)) {
                // 電話番号取得して、ユーザー登録 API 送信
                val phoneNumber = readPhoneNumber()
                if (phoneNumber == null) {
                    showFinishAlertBox()
                } else {
                    progressBarMainActivity.visibility = View.VISIBLE
                    val userInfo = UserForRegistration(phoneNumber = phoneNumber)
                    RetrofitFunctions.registerUser(userInfo, this)
                }
            }
        } else if(Preferences.fcmToken == null){
            val myFirebaseMessagingService = MyFirebaseMessagingService()
            myFirebaseMessagingService.sendInitialTokenToServer(this)
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
            viewPager.setCurrentItem(0, false)
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
        if (BuildConfig.DEBUG) {
            inflater.inflate(R.menu.debug_menu, menu)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.contact -> {
                startActivityIntentContact()
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
                // Display the alert box
                showFinishAlertBox()
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
        progressBarMainActivity.visibility = View.GONE
        Preferences.setApiIdInPreference(userWithApiKey, this)
        val myFirebaseMessagingService = MyFirebaseMessagingService()
        myFirebaseMessagingService.sendInitialTokenToServer(this)
    }

    /**
     * RetrofitFunctions.registerUser の結果受信.
     *
     * This function gets the response containing id and api key
     */
    fun onResponseFcmToken(fcmToken: UserForFcmToken, mainActivity: MainActivity) {
        // TODO: そもそも、以下のメソッドは失敗することはなさそう
        Preferences.setFcmTokenInPreference(fcmToken, mainActivity)
        transitionNickNameInputScreen(mainActivity)
    }

    /**
     * RetrofitFunctions.registerNickName の結果受信 (NickNameFragment で送信している).
     */
    fun onResponseRegisterNickname(userForUpdate: UserForUpdate) {
        progressBarMainActivity.visibility = View.GONE
        Preferences.setNickNamePreference(userForUpdate, this)
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
        progressBarMainActivity.visibility = View.GONE
        Toast.makeText(this, getString(R.string.message_server_error), Toast.LENGTH_LONG).show()
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

    /**
     * お問い合せメール.
     */
    private fun startActivityIntentContact() {
        // https://developer.android.com/guide/components/intents-common?hl=ja#Email
        // https://qiita.com/masaibar/items/4edcc9c7d2df11fe6e79
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.contact_email)))
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.contact_subject))
            putExtra(
                Intent.EXTRA_TEXT,
                getString(
                    R.string.contact_text,
                    BuildConfig.VERSION_NAME,
                    Build.VERSION.RELEASE,
                    Build.MANUFACTURER + " " + Build.MODEL
                )
            )
        }
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }
}
