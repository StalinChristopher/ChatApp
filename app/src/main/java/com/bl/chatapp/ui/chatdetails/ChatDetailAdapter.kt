package com.bl.chatapp.ui.chatdetails

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bl.chatapp.R
import com.bl.chatapp.common.Constants.VIEW_TYPE_RECEIVE
import com.bl.chatapp.common.Constants.VIEW_TYPE_SENT
import com.bl.chatapp.data.models.Message
import com.bl.chatapp.ui.home.adapters.ChatUsersAdapter
import com.bl.chatapp.wrappers.UserDetails

class ChatDetailAdapter(
    private val context: Context, private val messageList: ArrayList<Message>,
    private val currentUser: UserDetails
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class SendMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val sendTextView: TextView =
            itemView.findViewById(R.id.sent_message_recycler_text_view)

        fun setUserInfo(context: Context, message: Message) {
            sendTextView.text = message.messageText
        }
    }

    class ReceiveMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val receiveTextView: TextView =
            itemView.findViewById(R.id.received_message_recycler_text_view)

        fun setUserInfo(context: Context, message: Message) {
            receiveTextView.text = message.messageText
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (messageList[position].senderId.equals(currentUser.uid))
            VIEW_TYPE_SENT
        else {
            VIEW_TYPE_RECEIVE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == VIEW_TYPE_SENT) {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.chat_detail_recycler_sent_item, parent, false)
            return SendMessageViewHolder(itemView)
        } else {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.chat_detail_recycler_receive_item, parent, false)
            return ReceiveMessageViewHolder(itemView)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentMessage = messageList[position]
        if (getItemViewType(position) == VIEW_TYPE_SENT) {
            (holder as SendMessageViewHolder).setUserInfo(context, currentMessage)
        } else {
            (holder as ReceiveMessageViewHolder).setUserInfo(context, currentMessage)
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

}