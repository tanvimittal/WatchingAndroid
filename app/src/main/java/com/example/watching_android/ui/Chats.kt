package com.example.watching_android.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.watching_android.R
import com.example.watching_android.model.Messages


class Chats : Fragment() {

    var textViewMessage : TextView? = null

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


        return parentHolder
    }

    /**
     * Everytime data is changed this function would be called
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        textViewMessage = activity?.findViewById<TextView>(R.id.txtViewMsg)

        val list: MutableList<Messages> = mutableListOf()
        viewModel.messages.observe(viewLifecycleOwner){

            var result : StringBuilder = StringBuilder("")
            it.forEach{
                result.append(it.user.nick_name + "-" + it.description)
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


        }

    }
}
