package com.example.watching_android.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.watching_android.R
import com.example.watching_android.database.RetrofitFunctions
import com.example.watching_android.model.MessageDescription
import com.example.watching_android.model.Messages
import kotlinx.android.synthetic.main.fragment_debug.*


class Chats : Fragment() {

    var textViewMessage : TextView? = null
    var btnOhayou : Button ?= null
    var btnOyasumi: Button ?= null

    // https://developer.android.com/topic/libraries/architecture/viewmodel-savedstate
    // We don't need factoryProducer?!
    // We want to pass Repository to ViewModel.
//   private val viewModel : MessageViewModel by viewModels(
//        factoryProducer =   { SavedStateViewModelFactory(requireActivity().application, this)}
//    )
    private val viewModel : MessageViewModel by viewModels()

    // We have to explain this "by viewModels"? Why can't we construct by simple way like below?
    //private val viewModel : MessageViewModel = MessageViewModel()
    // https://qiita.com/mangano-ito/items/9b067916d1374d66b750
    // https://developer.android.com/reference/kotlin/androidx/fragment/app/package-summary?hl=ja#viewmodels

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val parentHolder = inflater.inflate(R.layout.chat_tab, container, false)
        btnOhayou = activity?.findViewById<Button>(R.id.btnOhayou)
        btnOyasumi = activity?.findViewById<Button>(R.id.btnOyasumi)

        return parentHolder
        btnOhayou?.setOnClickListener(View.OnClickListener {
            buttonClick(MessageDescription("おはよう"))
            viewModel.getRecentMessages()
        })
        btnOyasumi?.setOnClickListener(View.OnClickListener {
            buttonClick(MessageDescription("おやすみ"))
            viewModel.getRecentMessages()
        })

    }

    /**
     * Everytime data is changed this function would be called
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnOhayou = activity?.findViewById<Button>(R.id.btnOhayou)
        btnOyasumi = activity?.findViewById<Button>(R.id.btnOyasumi)
        btnOhayou?.setOnClickListener(View.OnClickListener {
            buttonClick(MessageDescription("おはよう"))
            viewModel.getRecentMessages()
        })
        btnOyasumi?.setOnClickListener(View.OnClickListener {
            buttonClick(MessageDescription("おやすみ"))
            viewModel.getRecentMessages()
        })
        val list: MutableList<Messages> = mutableListOf()

        viewModel.messages.observe (viewLifecycleOwner){

            var result : StringBuilder = StringBuilder("")
            it!!.forEach{
                result.append(it.user.nickname + "-" + it.description)
                result.append(System.getProperty("line.separator"))
                list.add(it)
            }
            val mMessageAdapter = MessageListAdapter(list)
            //mMessageRecycler?.setLayoutManager(LinearLayoutManager(activity))
            val viewManager = LinearLayoutManager(activity)
            val mMessageRecycler =
                activity?.findViewById<RecyclerView>(R.id.reyclerview_message_list)?.apply{
                    layoutManager = viewManager
                    adapter = mMessageAdapter
                }
            // Scroll to last item in the list
            mMessageRecycler?.scrollToPosition((mMessageRecycler.adapter?.itemCount ?: 0) - 1)

        }
        fun checkData(){
            viewModel.getRecentMessages()
        }

    }

    fun buttonClick(messageDescription: MessageDescription){
        RetrofitFunctions.sendMessageDescription(messageDescription)

    }


    override fun onDestroy() {
        super.onDestroy()
    }
}
