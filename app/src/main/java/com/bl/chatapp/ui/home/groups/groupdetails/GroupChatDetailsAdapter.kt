package com.bl.chatapp.ui.home.groups.groupdetails

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bl.chatapp.R
import com.bl.chatapp.common.Constants.IMAGE
import com.bl.chatapp.common.Constants.VIEW_TYPE_IMAGE_RECEIVE
import com.bl.chatapp.common.Constants.VIEW_TYPE_IMAGE_SENT
import com.bl.chatapp.common.Constants.VIEW_TYPE_RECEIVE
import com.bl.chatapp.common.Constants.VIEW_TYPE_SENT
import com.bl.chatapp.data.models.GroupInfo
import com.bl.chatapp.data.models.Message
import com.bl.chatapp.ui.home.groups.groupdetails.viewholders.ReceiveGroupImageMessageViewHolder
import com.bl.chatapp.ui.home.groups.groupdetails.viewholders.ReceiveGroupMessageViewHolder
import com.bl.chatapp.ui.home.groups.groupdetails.viewholders.SendGroupImageMessageViewHolder
import com.bl.chatapp.ui.home.groups.groupdetails.viewholders.SendGroupMessageViewHolder
import com.bl.chatapp.wrappers.UserDetails

class GroupChatDetailsAdapter(
    private val context: Context, private val messageList: ArrayList<Message>,
    private val currentUser: UserDetails, private val selectedGroup: GroupInfo,
    private val memberList: ArrayList<UserDetails>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            VIEW_TYPE_SENT -> {
                val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.chat_detail_recycler_sent_item, parent, false)
                return SendGroupMessageViewHolder(itemView)
            }
            VIEW_TYPE_RECEIVE -> {
                val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.chat_detail_recycler_receive_item, parent, false)
                return ReceiveGroupMessageViewHolder(itemView)
            }
            VIEW_TYPE_IMAGE_SENT -> {
                val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.chat_detail_recycler_sent_image_item, parent, false)
                return SendGroupImageMessageViewHolder(itemView)
            }
            else -> {
                val itemView = LayoutInflater.from(parent.context).inflate(R.layout.chat_detail_recycler_receive_image_item, parent, false)
                return ReceiveGroupImageMessageViewHolder(itemView)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentMessage = messageList[position]
        when(getItemViewType(position)) {
            VIEW_TYPE_SENT -> {
                (holder as SendGroupMessageViewHolder).setMessageInfo(context, currentMessage)
            }
            VIEW_TYPE_RECEIVE -> {
                (holder as ReceiveGroupMessageViewHolder).setMessageInfo(context, currentMessage, memberList)
            }
            VIEW_TYPE_IMAGE_SENT -> {
                (holder as SendGroupImageMessageViewHolder).setMessageInfo(context, currentMessage)
            }
            else -> {
                (holder as ReceiveGroupImageMessageViewHolder).setMessageInfo(context, currentMessage, memberList)
            }
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (messageList[position].senderId == currentUser.uid) {
            if(messageList[position].messageType == IMAGE) {
                VIEW_TYPE_IMAGE_SENT
            } else {
                VIEW_TYPE_SENT
            }
        } else {
            if(messageList[position].messageType == IMAGE) {
                VIEW_TYPE_IMAGE_RECEIVE
            } else {
                VIEW_TYPE_RECEIVE
            }
        }

    }

}