package jp.kyuuki.watching.ui

import android.app.Activity
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import jp.kyuuki.watching.R
import jp.kyuuki.watching.database.Preferences
import jp.kyuuki.watching.database.RetrofitFunctions
import jp.kyuuki.watching.model.UserPublic

class Search : Fragment() {

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val parentHolder = inflater.inflate(R.layout.search_fragment, container, false)
        val btnSearch = parentHolder?.findViewById<Button>(R.id.btnAcceptRequest)
        val phoneNumberText = parentHolder?.findViewById<EditText>(R.id.phoneNumberText)!!
        val progressBarSearch  = parentHolder?.findViewById<ProgressBar>(R.id.progressBarSearch)
        firebaseAnalytics = Firebase.analytics
        btnSearch?.setOnClickListener {
            val apiKey = Preferences.apiKey

            if (apiKey == null) {
                // TODO: エラー処理
                return@setOnClickListener
            }

            var searchPhone = phoneNumberText.text.toString()
            if (searchPhone.length == 11 && searchPhone.isNotEmpty()) {

                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT) {
                    param(FirebaseAnalytics.Param.ITEM_ID, btnSearch!!.text.toString())
                    param(FirebaseAnalytics.Param.CONTENT_TYPE, "Button")
                }

                progressBarSearch.visibility = View.VISIBLE
                searchPhone = searchPhone.substring(1)
                searchPhone = "+81$searchPhone"
                activity?.let { it1 -> RetrofitFunctions.getSearchResult(apiKey, searchPhone, it1, this) }
            } else {
                // Alert Boxを表示してアプリを終了する。
                val alertDialog: android.app.AlertDialog? =
                    android.app.AlertDialog.Builder(activity).create()
                if (alertDialog != null) {
                    //alertDialog.setTitle(this.resources.getString(R.string.alertNickNameTitle))
                    alertDialog.setMessage(this.resources.getString(R.string.alertSearchMsg))
                    alertDialog.setButton(
                        AlertDialog.BUTTON_NEUTRAL, "OK",
                        DialogInterface.OnClickListener { dialog, _ -> dialog.dismiss() })
                    alertDialog.show()
                }
            }
        }

        return parentHolder
    }


    fun sendRequest(nickNameInfo: UserPublic, activity: Activity) {
        val apiKey = Preferences.apiKey
        if (apiKey == null) {
            // TODO: エラー処理
            return
        }

        // Progress Barを非表示する
        val progressBarSearch  = activity?.findViewById<ProgressBar>(R.id.progressBarSearch)
        progressBarSearch.visibility = View.GONE

        val nickName = nickNameInfo.nickname
        val userId = nickNameInfo.id
        val alertDialog: android.app.AlertDialog? =
            android.app.AlertDialog.Builder(activity).create()
        if (alertDialog != null) {
            alertDialog.setTitle(activity.resources.getString(R.string.requestTitle))
            val title = String.format(activity.resources.getString(R.string.sendRequestMsg),nickName)
            alertDialog.setMessage(title)
            alertDialog.setButton(
                AlertDialog.BUTTON_NEUTRAL, "OK",
                DialogInterface.OnClickListener {
                        dialog, _ -> dialog.dismiss()
                        progressBarSearch.visibility = View.VISIBLE
                        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT) {
                        param(FirebaseAnalytics.Param.ITEM_ID, "SendRequest")
                        param(FirebaseAnalytics.Param.CONTENT_TYPE, "Button")
                    }
                        RetrofitFunctions.sendRequest(apiKey, userId, activity, this)
                         })
            alertDialog.setButton(
                AlertDialog.BUTTON_NEGATIVE, "CANCEL",
                DialogInterface.OnClickListener { dialog, _ -> dialog.dismiss() })
            alertDialog.show()
        }

    }

    /**
     * This function would be called when Request is sent
     */
    fun onSuccess(activity: Activity){
        val progressBarSearch  = activity?.findViewById<ProgressBar>(R.id.progressBarSearch)
        progressBarSearch.visibility = View.GONE
        Toast.makeText(activity, activity.resources.getString(R.string.onSuccess), Toast.LENGTH_LONG).show()
        val phoneNumberText = activity.findViewById<EditText>(R.id.phoneNumberText)!!
        phoneNumberText.setText("")
    }

    /**
     * This function would be called when error occurs
     */
    fun onFailure(activity: Activity){
        val progressBarSearch  = activity?.findViewById<ProgressBar>(R.id.progressBarSearch)
        progressBarSearch.visibility = View.GONE
        Toast.makeText(activity, activity.resources.getString(R.string.onError), Toast.LENGTH_LONG).show()
    }

    fun userNotFound(activity: Activity){
        val progressBarSearch  = activity?.findViewById<ProgressBar>(R.id.progressBarSearch)
        progressBarSearch.visibility = View.GONE
        Toast.makeText(activity, activity.resources.getString(R.string.user_not_found), Toast.LENGTH_LONG).show()
    }

}