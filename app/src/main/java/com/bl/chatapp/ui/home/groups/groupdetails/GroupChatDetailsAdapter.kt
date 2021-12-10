package com.bl.chatapp.ui.home.groups.groupdetails

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bl.chatapp.R
import com.bl.chatapp.common.Constants
import com.bl.chatapp.common.Constants.VIEW_TYPE_RECEIVE
import com.bl.chatapp.common.Constants.VIEW_TYPE_SENT
import com.bl.chatapp.data.models.GroupInfo
import com.bl.chatapp.data.models.Message
import com.bl.chatapp.wrappers.UserDetails

class GroupChatDetailsAdapter(
    private val context: Context, private val messageList: ArrayList<Message>,
    private val currentUser: UserDetails, private val selectedGroup: GroupInfo,
    private val memberList: ArrayList<UserDetails>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class SendGroupMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val sendTextView: TextView =
            itemView.findViewById(R.id.sent_message_recycler_text_view)

        fun setUserInfo(context: Context, message: Message) {
            sendTextView.text = message.messageText
        }
    }

    class ReceiveGroupMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val receiveTextView: TextView =
            itemView.findViewById(R.id.received_message_recycler_text_view)
        private val receiverNameTextView: TextView =
            itemView.findViewById(R.id.received_message_recycler_name_text_view)

        fun setUserInfo(context: Context, message: Message, memberList: ArrayList<UserDetails>) {
            receiveTextView.text = message.messageText
            memberList.forEach {
                if (it.uid == message.senderId) {
                    receiverNameTextView.text = it.userName
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == Constants.VIEW_TYPE_SENT) {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.chat_detail_recycler_sent_item, parent, false)
            return SendGroupMessageViewHolder(itemView)
        } else {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.chat_detail_recycler_receive_item, parent, false)
            return ReceiveGroupMessageViewHolder(itemView)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentMessage = messageList[position]
        if (getItemViewType(position) == Constants.VIEW_TYPE_SENT) {
            holder as SendGroupMessageViewHolder
            holder.setUserInfo(context, currentMessage)
        } else {
            holder as ReceiveGroupMessageViewHolder
            holder.setUserInfo(context, currentMessage, memberList)
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (messageList[position].senderId == currentUser.uid) {
            VIEW_TYPE_SENT
        } else {
            VIEW_TYPE_RECEIVE
        }

    }

}