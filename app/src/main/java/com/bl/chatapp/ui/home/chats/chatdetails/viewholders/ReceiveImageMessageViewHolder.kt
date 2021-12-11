package com.bl.chatapp.ui.home.chats.chatdetails.viewholders

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bl.chatapp.R
import com.bl.chatapp.data.models.Message
import com.bumptech.glide.Glide

class ReceiveImageMessageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.received_message_recycler_image_view)
        private val receiverNameTextView : TextView = itemView.findViewById(R.id.received_image_message_recycler_name_text_view)

        fun setMessageInfo(context: Context, message: Message) {
            receiverNameTextView.visibility = View.GONE
            Glide.with(context).load(message.content).into(imageView)
        }
    }