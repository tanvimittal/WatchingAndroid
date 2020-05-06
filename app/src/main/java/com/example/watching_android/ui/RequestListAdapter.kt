package com.example.watching_android.ui

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.watching_android.R
import com.example.watching_android.database.RetrofitFunctions
import com.example.watching_android.model.RequestRecievedModel


var stringName : String = ""
var activity : Activity ?= null
class RequestListAdapter(private val requestList: List<RequestRecievedModel>) :
    RecyclerView.Adapter<RequestListAdapter.ReceivedRequestHolder>()
{

   // class MyViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): ReceivedRequestHolder {
        // create a new view
        val textView = LayoutInflater.from(parent.context)
            .inflate(R.layout.request_recieved, parent, false)
        // set the view's size, margins, paddings and layout parameters
        stringName = parent.context.resources.getString(R.string.messageDetail)
        activity = parent.context as Activity?
        return ReceivedRequestHolder(textView)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: ReceivedRequestHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        val request = requestList[position]
        holder.bind(request)
    }

    override fun getItemCount() = requestList.size

    class ReceivedRequestHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var messageText: TextView = itemView.findViewById(R.id.displayText)
        val btnOk: Button = itemView.findViewById(R.id.btnAcceptRequest)
        val btnDecline: Button = itemView.findViewById(R.id.btnDeclineRequet)
        fun bind(request: RequestRecievedModel) {
            val userName = request.from_user.nickname
            val userId = request.id
            val title = String.format(stringName,userName)
            messageText.text = title
            btnOk.setOnClickListener {
                activity?.let { it1 -> RetrofitFunctions.acceptRequest(it1, userId) }
            }
            //TODO: Decide what to do, right now Just deleting request
            btnDecline.setOnClickListener {

            }
        }


    }

}


