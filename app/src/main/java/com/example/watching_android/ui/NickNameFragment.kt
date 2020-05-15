package com.example.watching_android.ui

import android.content.Context
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
import com.example.watching_android.model.NickNameData

/**
 * Purpose of this class to register nickname
 */
class NickNameFragment() : Fragment() {

    var mContext : Context? = null
    companion object {
        fun createInstance( mc : Context): NickNameFragment {
            // インスタンス？　MainActivityで生成時に呼ばれている関数
            val tmpDetailFragment = NickNameFragment()
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
        val registerButton = nickNameView?.findViewById<Button>(R.id.btnNickName)
        val textView = nickNameView?.findViewById<EditText>(R.id.editTextNickName)
        registerButton?.setOnClickListener {
            var nickName = textView!!.text.toString()
            if(nickName.trim().length<=15 && nickName.isNotEmpty()){
                // TODO: そもそも、この処理に意味があるのか？
                val userRegistration = activity?.let { it1 -> Preferences.getPreferences(it1) }
                // TODO: userRegistration が null の時 (= avtivity が null の時?!) はどうする？
                if (userRegistration != null) {
                    activity?.let { it1 ->
                        RetrofitFunctions.registerNickName(NickNameData(nickName),
                            it1
                        )
                    }
                }
            } else{
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