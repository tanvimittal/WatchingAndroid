package jp.kyuuki.watching.ui

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import jp.kyuuki.watching.MainActivity
import jp.kyuuki.watching.R
import jp.kyuuki.watching.database.Preferences
import jp.kyuuki.watching.database.RetrofitFunctions
import jp.kyuuki.watching.model.UserForUpdate
import jp.kyuuki.watching.model.UserPublic

/**
 * Purpose of this class to register nickname
 *
 * - 現状 MainActivity からしか使えない (MainActivity に依存)
 */
class NicknameFragment() : Fragment() {

    private lateinit var firebaseAnalytics: FirebaseAnalytics

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
        val progressBar = nickNameView.findViewById<ProgressBar>(R.id.progressBarNickName);
        firebaseAnalytics = Firebase.analytics
        // Setting on click listener of button
        val registerButton = nickNameView.findViewById<Button>(R.id.btnNickName)
        val textView = nickNameView.findViewById<EditText>(R.id.editTextNickName)

        nickNameView.findViewById<TextView>(R.id.text_description).text =
            getString(R.string.description_input_nickname, UserPublic.MAX_LENGTH_NICKNAME)

        registerButton.setOnClickListener {
            var nickname = textView.text.toString()
            val apiKey = Preferences.apiKey

            if (apiKey == null) {
                // TODO: エラー処理
            } else if (nickname.trim().length <= UserPublic.MAX_LENGTH_NICKNAME && nickname.isNotEmpty()) {
                // TODO: MainActivity 依存
                activity?.let {

                    firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT) {
                        param(FirebaseAnalytics.Param.ITEM_ID,
                            registerButton!!.text.toString())
                        param(FirebaseAnalytics.Param.CONTENT_TYPE, "Button")
                    }

                    progressBar.visibility = View.VISIBLE
                    RetrofitFunctions.registerNickname(apiKey, UserForUpdate(nickname), it as MainActivity)
                }
            } else {
                // Alert Boxを表示する。
                val alertDialog: android.app.AlertDialog? = android.app.AlertDialog.Builder(activity).create()
                if (alertDialog != null) {
                    //alertDialog.setTitle(this.resources.getString(R.string.alertNickNameTitle))
                    alertDialog.setMessage(getString(R.string.alertNickNameMsg, UserPublic.MAX_LENGTH_NICKNAME))
                    alertDialog.setButton(
                        AlertDialog.BUTTON_NEUTRAL, "OK",
                        DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
                    alertDialog.show()
                }
            }

        }

        return nickNameView
    }

}