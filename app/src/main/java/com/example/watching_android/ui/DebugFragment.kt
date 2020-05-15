package com.example.watching_android.ui

import android.content.Context
import android.os.Bundle
import androidx.preference.PreferenceManager
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.example.watching_android.R
import com.example.watching_android.database.Preferences

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

        view.findViewById<Button>(R.id.button_first).setOnClickListener {
            /*
             * SharedPreference 削除
             */
            val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)

            if (sharedPref != null) {
                val nickName = sharedPref.getString(Preferences.KEY_NICKNAME, null)
                Log.d(this::class.java.simpleName, "nickname = $nickName")

                sharedPref.edit().apply {
                    clear()
                    commit()
                }

                Toast.makeText(activity, "Deleted SharedPreferences", Toast.LENGTH_LONG).show()
            }
        }
    }
}
