package com.bl.chatapp.ui.home.chats.chatdetails.viewholders

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bl.chatapp.R
import com.bl.chatapp.data.models.Message

class SendMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val sendTextView: TextView =
            itemView.findViewById(R.id.sent_message_recycler_text_view)

        fun setMessageInfo(context: Context, message: Message) {
            sendTextView.text = message.content
        }
    }