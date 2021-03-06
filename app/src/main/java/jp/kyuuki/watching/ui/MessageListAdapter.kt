package jp.kyuuki.watching.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import jp.kyuuki.watching.R
import jp.kyuuki.watching.database.Preferences
import jp.kyuuki.watching.model.Event
import java.text.SimpleDateFormat
import java.util.*

class MessageListAdapter(val context: Context, private val messageList: List<Event>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>()
{
    private val VIEW_TYPE_MESSAGE_SENT = 1
    private val VIEW_TYPE_MESSAGE_RECEIVED = 2
   // class MyViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): RecyclerView.ViewHolder {

        lateinit var retMessageHolder : RecyclerView.ViewHolder
        if (viewType == VIEW_TYPE_MESSAGE_RECEIVED){
            // create a new view
            val textView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_message_received, parent, false)
            // set the view's size, margins, paddings and layout parameters
            retMessageHolder = ReceivedMessageHolder(context, textView)
        }
        else if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            // create a new view
            val textView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_message_sent, parent, false)
            // set the view's size, margins, paddings and layout parameters
            retMessageHolder = SentMessageHolder(context, textView)
        }

        return retMessageHolder
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        val message = messageList.get(position)
        when (holder.itemViewType) {
            VIEW_TYPE_MESSAGE_SENT -> (holder as SentMessageHolder).bind(message)
            VIEW_TYPE_MESSAGE_RECEIVED -> (holder as ReceivedMessageHolder).bind(message)
        }
    }

    /**
     * This returns the type of message
     */
    override fun getItemViewType(position: Int): Int {
        val message = messageList.get(position)
        if(message.user.id == Preferences.userId)
            return VIEW_TYPE_MESSAGE_SENT
        else
            return VIEW_TYPE_MESSAGE_RECEIVED
    }

    override fun getItemCount() = messageList.size

    private class ReceivedMessageHolder(val context: Context, itemView: View) : RecyclerView.ViewHolder(itemView) {
        var messageText: TextView = itemView.findViewById(R.id.text_message_body)
        var timeText: TextView = itemView.findViewById(R.id.text_message_time)
        //var nameText: TextView = itemView.findViewById(R.id.text_message_name)
        fun bind(event: Event) {
            messageText.text = when (event.name) {
                Event.NAME_GET_UP -> {
                    context.getString(R.string.message_received_event_get_up, event.user.nickname)
                }
                Event.NAME_GO_TO_BED -> {
                    context.getString(R.string.message_received_event_go_to_bed, event.user.nickname)
                }
                else -> {
                    context.getString(R.string.message_event_invalid)
                    // TODO: バグを検出
                }
            }

            // Format the stored timestamp into a readable String using method.
            val setDate = formatDate(event.createdAt)
            timeText.text = setDate

            //Setting nick name
            //nameText.text = event.user.nickname

            // Insert the profile image from the URL into the ImageView.
            //Utils.displayRoundImageFromUrl(mContext, message.getSender().getProfileUrl(), profileImage)
        }


        /**
         * Converting date into desired format
         */
        fun getDate(date: Date): String {

            // output format: hour:minute AM/PM
            val outputFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
            val outDateFormat = SimpleDateFormat("MM-dd", Locale.getDefault())

            outputFormat.timeZone = TimeZone.getDefault()
            outDateFormat.timeZone = TimeZone.getDefault()
            val time = outputFormat.format(date)
            val msgDate = outDateFormat.format(date)
            return ("$msgDate $time")
        }
    }


    private class SentMessageHolder internal constructor(val context: Context, itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var messageText: TextView = itemView.findViewById(R.id.text_message_body)
        var timeText: TextView = itemView.findViewById(R.id.text_message_time)
        fun bind(event: Event) {
            messageText.text = when (event.name) {
                Event.NAME_GET_UP -> {
                    context.getString(R.string.message_sent_event_get_up)
                }
                Event.NAME_GO_TO_BED -> {
                    context.getString(R.string.message_sent_event_go_to_bed)
                }
                else -> {
                    context.getString(R.string.message_event_invalid)
                    // TODO: バグを検出
                }
            }

            // Format the stored timestamp into a readable String using method.
            val setDate = formatDate(event.createdAt)
            timeText.text = setDate
            // Insert the profile image from the URL into the ImageView.
            //Utils.displayRoundImageFromUrl(mContext, message.getSender().getProfileUrl(), profileImage)
        }
    }

}

// 日本仕様
private fun formatDate(date: Date): String {
    // output format: hour:minute AM/PM
    val outputFormat = SimpleDateFormat("a hh:mm", Locale.getDefault())
    val outDateFormat = SimpleDateFormat("MM/dd", Locale.getDefault())

    outputFormat.timeZone = TimeZone.getDefault()
    outDateFormat.timeZone = TimeZone.getDefault()
    val time = outputFormat.format(date)
    val msgDate = outDateFormat.format(date)
    return ("$msgDate   $time")
}
