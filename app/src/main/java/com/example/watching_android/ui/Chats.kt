package com.example.watching_android.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
import com.example.watching_android.R

class Chats : Fragment() {

    var textViewMessage : TextView?=null

  /*  private val viewModel : MessageViewModel by viewModels(
        factoryProducer = {SavedStateViewModelFactory(activity!!.application, this,null)}
    )*/
 /*private val viewModel by viewModels<MessageViewModel> () {
      SavedStateViewModelFactory(activity!!.application, this,null)
  }*/

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val parentHolder = inflater.inflate(R.layout.chat_tab, container, false)
        textViewMessage = activity?.findViewById<TextView>(R.id.txtViewMsg)
        return parentHolder
    }

    /**
     * Everytime data is changed this function would be called
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewModel: MessageViewModel by viewModels(
            factoryProducer = {SavedStateViewModelFactory(activity!!.application, this,null)}
        )
        viewModel.messages.observe(viewLifecycleOwner, Observer {
            //TODO: Update UI
            textViewMessage?.text ?:viewModel.messages.toString()

        })

    }
}
