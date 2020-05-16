package com.example.watching_android.ui

import android.app.Activity
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.watching_android.R
import com.example.watching_android.database.Preferences
import com.example.watching_android.database.RetrofitFunctions
import com.example.watching_android.model.NickNameID

class Search : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val parentHolder = inflater.inflate(R.layout.search_fragment, container, false)
        val btnSearch = parentHolder?.findViewById<Button>(R.id.btnAcceptRequest)
        val phoneNumberText = parentHolder?.findViewById<EditText>(R.id.phoneNumberText)!!
        btnSearch?.setOnClickListener {
            val apiKey = Preferences.apiKey

            if (apiKey == null) {
                // TODO: エラー処理
                return@setOnClickListener
            }

            var searchPhone = phoneNumberText.text.toString()
            if (searchPhone.length == 11 && searchPhone.isNotEmpty()) {
                searchPhone = searchPhone.substring(1)
                searchPhone = "+81$searchPhone"
                activity?.let { it1 -> RetrofitFunctions.getSearchResult(apiKey, searchPhone, it1, this) }
            } else {
                // Alert Boxを表示してアプリを終了する。
                val alertDialog: android.app.AlertDialog? =
                    android.app.AlertDialog.Builder(activity).create()
                if (alertDialog != null) {
                    alertDialog.setTitle(this.resources.getString(R.string.alertNickNameTitle))
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


    fun sendRequest(nickNameInfo: NickNameID, activity: Activity) {
        val apiKey = Preferences.apiKey
        if (apiKey == null) {
            // TODO: エラー処理
            return
        }

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
        Toast.makeText(activity, activity.resources.getString(R.string.onSuccess), Toast.LENGTH_LONG).show()
        val phoneNumberText = activity.findViewById<EditText>(R.id.phoneNumberText)!!
        phoneNumberText.setText("")
    }

    /**
     * This function would be called when error occurs
     */
    fun onFailure(activity: Activity){
        Toast.makeText(activity, activity.resources.getString(R.string.onError), Toast.LENGTH_LONG).show()
    }

    fun userNotFound(activity: Activity){
        Toast.makeText(activity, activity.resources.getString(R.string.user_not_found), Toast.LENGTH_LONG).show()
    }

}