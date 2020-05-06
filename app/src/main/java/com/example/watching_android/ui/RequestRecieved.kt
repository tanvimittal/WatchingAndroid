package com.example.watching_android.ui

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.watching_android.R
import com.example.watching_android.database.RetrofitFunctions
import com.example.watching_android.model.RequestRecievedModel
import kotlinx.android.synthetic.main.request_recieved_fragment.*

class RequestRecieved : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val parentHolder = inflater.inflate(R.layout.request_recieved_fragment, container, false)
        activity?.let { RetrofitFunctions.getRequest(it) }

        // Getting swipeOnRefresh
        val swipeRefreshLayout = parentHolder.findViewById<SwipeRefreshLayout>(R.id.swipeRefresh)
        swipeRefreshLayout!!.setOnRefreshListener {
            activity?.let { RetrofitFunctions.getRequest(it) }
            swipeRefreshLayout.isRefreshing = false
        }
        return parentHolder
    }

    fun showRequests(requestList: List<RequestRecievedModel>, activity: Activity){

        val mrequestAdapter = RequestListAdapter(requestList)
        val viewManager = LinearLayoutManager(activity)
        val textNoData = activity.findViewById<TextView>(R.id.noItemTextView)
        if (requestList.isEmpty()){
            textNoData.visibility = View.VISIBLE
        }
        else{
            activity?.findViewById<RecyclerView>(R.id.requestRecyclerView)?.apply {
                layoutManager = viewManager
                adapter = mrequestAdapter
            }
            textNoData.visibility = View.GONE
        }

    }


    /**
     * This function would be called when Request is sent
     */
    fun onSuccess(activity: Activity){
        Toast.makeText(activity, activity.resources.getString(R.string.on_request_accepted), Toast.LENGTH_LONG).show()
    }

    /**
     * This function would be called when error occurs
     */
    fun onFailure(activity: Activity){
        Toast.makeText(activity, activity.resources.getString(R.string.onError), Toast.LENGTH_LONG).show()
    }

}