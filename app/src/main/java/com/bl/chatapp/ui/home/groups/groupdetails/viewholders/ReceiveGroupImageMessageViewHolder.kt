package com.bl.chatapp.ui.home.groups.groupdetails.viewholders

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bl.chatapp.R
import com.bl.chatapp.data.models.Message
import com.bl.chatapp.wrappers.UserDetails
import com.bumptech.glide.Glide

class ReceiveGroupImageMessageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.received_message_recycler_image_view)
        private val receiverNameTextView : TextView = itemView.findViewById(R.id.received_image_message_recycler_name_text_view)

        fun setMessageInfo(context: Context, message: Message, memberList: ArrayList<UserDetails>) {
            memberList.forEach {
                if (it.uid == message.senderId) {
                    receiverNameTextView.text = it.userName
                }
            }
            Glide.with(context).load(message.content).into(imageView)

        }
    }