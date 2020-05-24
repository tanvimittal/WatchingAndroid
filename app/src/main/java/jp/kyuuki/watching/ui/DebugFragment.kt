package jp.kyuuki.watching.ui

import android.os.Bundle
import androidx.preference.PreferenceManager
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import jp.kyuuki.watching.R
import jp.kyuuki.watching.database.Preferences
import jp.kyuuki.watching.database.WatchingApi
import java.lang.StringBuilder

class DebugFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_debug, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)

        val log = StringBuilder()
        log.appendln("Shared Preference")
        log.appendln("${Preferences.KEY_USER_ID} = ${sharedPref.getInt(Preferences.KEY_USER_ID, -1)}")
        log.appendln("${Preferences.KEY_API_KEY} = ${sharedPref.getString(Preferences.KEY_API_KEY, null)}")
        log.appendln("${Preferences.KEY_NICKNAME} = ${sharedPref.getString(Preferences.KEY_NICKNAME, null)}")
        log.appendln("")
        log.appendln("WatchingApi")
        log.appendln("apiBaseUrl = ${WatchingApi.apiBaseUrl}")

        /*
         * デバッグ情報表示
         */
        val logText = view.findViewById<TextView>(R.id.text_log)
        logText.text = log

        view.findViewById<Button>(R.id.button_first).setOnClickListener {
            /*
             * SharedPreference 削除
             */
            if (sharedPref != null) {
                sharedPref.edit().apply {
                    clear()
                    commit()
                }

                Toast.makeText(activity, "Deleted SharedPreferences", Toast.LENGTH_LONG).show()

                activity?.finish()
            }
        }
    }
}
