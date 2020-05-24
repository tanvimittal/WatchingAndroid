package jp.kyuuki.watching.ui

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import jp.kyuuki.watching.R
import jp.kyuuki.watching.database.Preferences
import jp.kyuuki.watching.database.RetrofitFunctions
import jp.kyuuki.watching.model.Event
import jp.kyuuki.watching.model.EventForRegistration
import jp.kyuuki.watching.utility.hideKeyboard
import com.google.firebase.analytics.ktx.logEvent


class Chats : Fragment() {

    private var btnOhayou : Button ?= null
    private var btnOyasumi: Button ?= null
    private var btnReadAgain : Button ?= null
    private var progressBarChats : ProgressBar ?= null
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    // https://developer.android.com/topic/libraries/architecture/viewmodel-savedstate
    // We don't need factoryProducer?!
    // We want to pass Repository to ViewModel.
   private val viewModel : MessageViewModel by viewModels(
        factoryProducer =   { SavedStateViewModelFactory(requireActivity().application, this) }
    )
    //private val viewModel : MessageViewModel by viewModels()

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
        btnOhayou = activity?.findViewById<Button>(R.id.btnOhayou)
        btnOyasumi = activity?.findViewById<Button>(R.id.btnOyasumi)
        btnReadAgain = activity?.findViewById<Button>(R.id.btnReadAgain)
        progressBarChats = activity?.findViewById<ProgressBar>(R.id.progressBarChats)
        firebaseAnalytics = Firebase.analytics

        activity?.let { hideKeyboard(it) }
        val apiKey = Preferences.apiKey
        if (apiKey == null) {
            // TODO: エラー処理
            return
        }

        progressBarChats?.visibility = View.VISIBLE
        viewModel.getRecentMessages(apiKey)
        tryFunc()
        btnOhayou?.setOnClickListener {

            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT) {
                param(FirebaseAnalytics.Param.ITEM_ID, btnOhayou!!.text.toString())
                param(FirebaseAnalytics.Param.CONTENT_TYPE, "Button")
            }

            progressBarChats?.visibility = View.VISIBLE
            buttonClick(EventForRegistration("get_up"))
        }

        btnOyasumi?.setOnClickListener {
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT) {
                param(FirebaseAnalytics.Param.ITEM_ID, btnOyasumi!!.text.toString())
                param(FirebaseAnalytics.Param.CONTENT_TYPE, "Button")
            }
            progressBarChats?.visibility = View.VISIBLE
            buttonClick(EventForRegistration("go_to_bed"))
        }

        btnReadAgain?.setOnClickListener {
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT) {
                param(FirebaseAnalytics.Param.ITEM_ID, btnReadAgain!!.text.toString())
                param(FirebaseAnalytics.Param.CONTENT_TYPE, "Button")
            }
            progressBarChats?.visibility = View.VISIBLE
            viewModel.getRecentMessages(apiKey)
        }

    }

    private fun buttonClick(eventForRegistration: EventForRegistration){
        val apiKey = Preferences.apiKey
        if (apiKey == null) {
            // TODO: エラー処理
            return
        }
        activity?.let { RetrofitFunctions.sendMessageDescription(apiKey, eventForRegistration, this, it) }
    }


    /**
     * This function is used to put observer on viewModel
     */
    private fun tryFunc() {
        val list: MutableList<Event> = mutableListOf()

        viewModel.messages.observe(viewLifecycleOwner) {
            list.clear()
            // On Error→ messages would be null
            if (it == null){
                activity?.let { it1 -> onError(it1) }
                // On Success→ messages won't be null
            } else {
                it.forEach {
                    list.add(it)
                }
                val mMessageAdapter = MessageListAdapter(requireContext(), list)
                val viewManager = LinearLayoutManager(activity)
                val mMessageRecycler =
                    activity?.findViewById<RecyclerView>(R.id.reyclerview_message_list)?.apply {
                        layoutManager = viewManager
                        adapter = mMessageAdapter
                    }
                // Scroll to last item in the list
                mMessageRecycler?.scrollToPosition((mMessageRecycler.adapter?.itemCount ?: 0) - 1)
            }
            progressBarChats?.visibility = View.GONE

        }
    }

    /**
     * This function is called when button click is successful
     */
    fun onSuccess() {
        val apiKey = Preferences.apiKey
        if (apiKey == null) {
            // TODO: エラー処理
            return
        }

        viewModel.getRecentMessages(apiKey)
    }

    /**
     * This method is called when error occurs
     */
    fun onError(activity: Activity) {
        progressBarChats?.visibility = View.GONE
        Toast.makeText(activity, activity.resources.getString(R.string.onError), Toast.LENGTH_LONG)
            .show()
    }
}
