package com.example.watching_android.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.watching_android.R
import com.example.watching_android.database.RetrofitFunctions

/**
 * Purpose of this class to register nickname
 */
class NickNameFragment() : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val nickNameView = inflater.inflate(R.layout.nickname_fragmnent, container, false)
        val btn = activity?.findViewById<Button>(R.id.btn)
        if(btn!=null){
            btn.visibility = View.INVISIBLE
        }

        // Setting on click listener of button
        val registerButton = nickNameView?.findViewById<Button>(R.id.btnNickName)
        registerButton?.setOnClickListener(View.OnClickListener {
            Toast.makeText(activity, "Working", Toast.LENGTH_LONG).show()
            RetrofitFunctions.registerNickName("1","Tanvi")
        })

        return nickNameView
        //ProgressBar progressBar = (ProgressBar) getActivity().findViewById(R.id.ctrlActivityIndicator);
    }

}