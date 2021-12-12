package com.bl.chatapp.ui.home.chats.chatdetails.viewholders

import android.content.Context
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bl.chatapp.R
import com.bl.chatapp.data.models.Message
import com.bumptech.glide.Glide

class SendImageMessageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.sent_image_message_image_view)

        fun setMessageInfo(context: Context, message: Message){
            Glide.with(context).load(message.content).into(imageView)
        }
    }