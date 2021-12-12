package com.bl.chatapp.ui.home.chats.chatdetails.viewholders

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bl.chatapp.R
import com.bl.chatapp.data.models.Message

class ReceiveMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val receiveTextView: TextView =
            itemView.findViewById(R.id.received_message_recycler_text_view)
        private val receiverNameLabel: TextView = itemView.findViewById(R.id.received_message_recycler_name_text_view)

        fun setMessageInfo(context: Context, message: Message) {
            receiveTextView.text = message.content
            receiverNameLabel.visibility = View.GONE
        }
    }