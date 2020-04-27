package com.example.watching_android.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.watching_android.R
import com.example.watching_android.model.Messages


class MessageListAdapter(private val messageList: List<Messages>) :
    RecyclerView.Adapter<MessageListAdapter.ReceivedMessageHolder>()
{
   // class MyViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): ReceivedMessageHolder {
        // create a new view
        val textView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_message_received, parent, false)
        // set the view's size, margins, paddings and layout parameters
        return ReceivedMessageHolder(textView)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: ReceivedMessageHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        val messages = messageList.get(position)
        holder.bind(messages)
    }

    override fun getItemCount() = messageList.size

    class ReceivedMessageHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var messageText: TextView = itemView.findViewById(R.id.text_message_body)
        lateinit var timeText: TextView
        var nameText: TextView = itemView.findViewById(R.id.text_message_name)
        fun bind(message: Messages) {
            messageText.setText(message.description)

            // Format the stored timestamp into a readable String using method.
            //   timeText.setText(Utils.formatDateTime(message.getCreatedAt()))
            nameText.setText(message.user.nick_name)

            // Insert the profile image from the URL into the ImageView.
            //Utils.displayRoundImageFromUrl(mContext, message.getSender().getProfileUrl(), profileImage)
        }


    }

}


