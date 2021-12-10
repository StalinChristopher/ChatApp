package com.bl.chatapp.ui.home.chats

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bl.chatapp.R
import com.bl.chatapp.ui.home.OnItemClickListener
import com.bl.chatapp.wrappers.UserDetails
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView

class ChatUsersAdapter(
    private val userList: ArrayList<UserDetails>,
    private val context: Context
) :
    RecyclerView.Adapter<ChatUsersAdapter.UserViewHolder>() {

    private lateinit var chatItemListener: OnItemClickListener

    fun setOnItemClickListener(listener: OnItemClickListener) {
        chatItemListener = listener
    }

    class UserViewHolder(itemView: View, listener: OnItemClickListener) :
        RecyclerView.ViewHolder(itemView) {
        private val userName = itemView.findViewById<TextView>(R.id.recycler_item_userName)
        private val profileImage =
            itemView.findViewById<CircleImageView>(R.id.imageView_recycler_item)
        private val messageTextView =
            itemView.findViewById<TextView>(R.id.recycler_item_message_text_view)

        fun setUserInfo(context: Context, user: UserDetails) {
            userName.text = user.userName
            if (user.profileImageUrl.isNotBlank()) {
                Glide.with(context).load(user.profileImageUrl).dontAnimate().into(profileImage)
            }
        }

        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.chat_recycler_view_item,
            parent, false
        )

        return UserViewHolder(itemView, chatItemListener)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentItem = userList[position]
        holder.setUserInfo(context, currentItem)
    }

    override fun getItemCount(): Int {
        return userList.size
    }
}