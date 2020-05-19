package jp.kyuuki.watching.ui

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
import jp.kyuuki.watching.R
import jp.kyuuki.watching.database.Preferences
import jp.kyuuki.watching.database.RetrofitFunctions
import jp.kyuuki.watching.model.FollowRequest
import jp.kyuuki.watching.utility.hideKeyboard

class RequestRecieved : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val apiKey = Preferences.apiKey

        if (apiKey == null) {
            // TODO: エラー処理
            return null
        }
        activity?.let { hideKeyboard(it) }
        val parentHolder = inflater.inflate(R.layout.request_recieved_fragment, container, false)
        activity?.let { RetrofitFunctions.getRequest(apiKey, it, this) }

        // Getting swipeOnRefresh
        val swipeRefreshLayout = parentHolder.findViewById<SwipeRefreshLayout>(R.id.swipeRefresh)
        val swipeRefreshLayoutEmpty = parentHolder.findViewById<SwipeRefreshLayout>(R.id.swipeRefreshEmpty)
        swipeRefreshLayout!!.setOnRefreshListener {
            activity?.let { RetrofitFunctions.getRequest(apiKey, it, this) }
            swipeRefreshLayout.isRefreshing = false
        }
        swipeRefreshLayoutEmpty!!.setOnRefreshListener {
            activity?.let { RetrofitFunctions.getRequest(apiKey, it, this) }
            swipeRefreshLayoutEmpty.isRefreshing = false
        }
        return parentHolder
    }

    /**
     * This function is called for displaying requests
     */
    fun showRequests(followRequestList: List<FollowRequest>, activity: Activity){

        val mrequestAdapter = RequestListAdapter(followRequestList)
        val swipeRefreshLayout = activity.findViewById<SwipeRefreshLayout>(R.id.swipeRefresh)
        val swipeRefreshLayoutEmpty = activity.findViewById<SwipeRefreshLayout>(R.id.swipeRefreshEmpty)
        swipeRefreshLayout.visibility = View.VISIBLE
        val viewManager = LinearLayoutManager(activity)
        val textNoData = activity.findViewById<TextView>(R.id.noItemTextView)
        if (followRequestList.isEmpty()){
            swipeRefreshLayoutEmpty.visibility = View.VISIBLE
            textNoData.visibility = View.VISIBLE
            swipeRefreshLayout.visibility = View.GONE
        }
        else{
            activity?.findViewById<RecyclerView>(R.id.requestRecyclerView)?.apply {
                layoutManager = viewManager
                adapter = mrequestAdapter
            }
            textNoData.visibility = View.GONE
            swipeRefreshLayoutEmpty.visibility = View.GONE
        }

    }


    /**
     * This function would be called when Request is sent
     */
    fun onSuccess(activity: Activity){
        Toast.makeText(activity, activity.resources.getString(R.string.on_request_accepted), Toast.LENGTH_LONG).show()
    }

    /**
     * This function would be called when Request is declined
     */
    fun onRequestDeclinedSuccess(activity: Activity){
        Toast.makeText(activity, activity.resources.getString(R.string.on_request_declined), Toast.LENGTH_LONG).show()
    }

    /**
     * This function would be called when error occurs
     */
    fun onFailure(activity: Activity){
        Toast.makeText(activity, activity.resources.getString(R.string.onError), Toast.LENGTH_LONG).show()
    }

}