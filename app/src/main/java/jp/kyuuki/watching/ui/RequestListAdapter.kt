package jp.kyuuki.watching.ui

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import jp.kyuuki.watching.R
import jp.kyuuki.watching.database.Preferences
import jp.kyuuki.watching.database.RetrofitFunctions
import jp.kyuuki.watching.model.FollowRequest


var stringName : String = ""
var activity : Activity ?= null
var fragmentObject : RecievedRequestsFragment ?= null
class RequestListAdapter(private val followRequestList: List<FollowRequest>) :
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
        fragmentObject = RecievedRequestsFragment()
        return ReceivedRequestHolder(textView)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: ReceivedRequestHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        val request = followRequestList[position]
        holder.bind(request)
    }

    override fun getItemCount() = followRequestList.size

    class ReceivedRequestHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private var messageText: TextView = itemView.findViewById(R.id.displayText)
        private val btnOk: Button = itemView.findViewById(R.id.btnAcceptRequest)
        private val btnDecline: Button = itemView.findViewById(R.id.btnDeclineRequet)
        fun bind(followRequest: FollowRequest) {
            val apiKey = Preferences.apiKey
            if (apiKey == null) {
                // TODO: エラー処理
                return
            }

            val nickname = followRequest.fromUser.nickname
            val followRequestId = followRequest.id
            val title = String.format(stringName, nickname)
            messageText.text = title

            btnOk.setOnClickListener {
                activity?.let { it1 -> RetrofitFunctions.acceptRequest(apiKey, it1, followRequestId, fragmentObject) }
            }
            //TODO: Decide what to do, right now Just deleting request
            btnDecline.setOnClickListener {
                activity?.let { it1 -> RetrofitFunctions.declineRequest(apiKey, it1, followRequestId, fragmentObject) }
            }
        }


    }

}


