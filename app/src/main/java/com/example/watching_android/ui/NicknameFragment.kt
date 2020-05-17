package com.example.watching_android.ui

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.watching_android.MainActivity
import com.example.watching_android.R
import com.example.watching_android.database.Preferences
import com.example.watching_android.database.RetrofitFunctions
import com.example.watching_android.model.UserForUpdate

/**
 * Purpose of this class to register nickname
 *
 * - 現状 MainActivity からしか使えない (MainActivity に依存)
 */
class NicknameFragment() : Fragment() {

    var mContext : Context? = null
    companion object {
        fun createInstance( mc : Context): NicknameFragment {
            // インスタンス？　MainActivityで生成時に呼ばれている関数
            val tmpDetailFragment = NicknameFragment()
            tmpDetailFragment.mContext = mc
            return tmpDetailFragment
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val nickNameView = inflater.inflate(R.layout.nickname_fragmnent, container, false)

        // Setting on click listener of button
        val registerButton = nickNameView.findViewById<Button>(R.id.btnNickName)
        val textView = nickNameView.findViewById<EditText>(R.id.editTextNickName)
        registerButton?.setOnClickListener {
            var nickname = textView.text.toString()
            val apiKey = Preferences.apiKey

            if (apiKey == null) {
                // TODO: エラー処理
            } else if (nickname.trim().length <= 15 && nickname.isNotEmpty()) {
                activity?.let {
                    RetrofitFunctions.registerNickname(apiKey, UserForUpdate(nickname), it as MainActivity)
                }
            } else {
                // Alert Boxを表示してアプリを終了する。
                val alertDialog: android.app.AlertDialog? = android.app.AlertDialog.Builder(activity).create()
                if (alertDialog != null) {
                    alertDialog.setTitle(this.resources.getString(R.string.alertNickNameTitle))
                    alertDialog.setMessage(this.resources.getString(R.string.alertNickNameMsg))
                    alertDialog.setButton(
                        AlertDialog.BUTTON_NEUTRAL, "OK",
                        DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
                    alertDialog.show()
                }
            }

        }

        return nickNameView
        //ProgressBar progressBar = (ProgressBar) getActivity().findViewById(R.id.ctrlActivityIndicator);
    }

}