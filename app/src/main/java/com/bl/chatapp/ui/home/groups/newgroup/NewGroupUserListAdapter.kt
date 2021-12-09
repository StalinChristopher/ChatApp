package com.bl.chatapp.ui.home.groups.newgroup

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bl.chatapp.R
import com.bl.chatapp.wrappers.UserDetails
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView

class NewGroupUserListAdapter(
    private val userList: ArrayList<UserDetails>,
    private val context: Context
) : RecyclerView.Adapter<NewGroupUserListAdapter.GroupChatUserViewHolder>() {

    private var selectedUser = mutableListOf<String>()

    class GroupChatUserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName: TextView = itemView.findViewById(R.id.recycler_item_userName)
        val profileImage: CircleImageView = itemView.findViewById(R.id.imageView_recycler_item)
        val checkBox: CheckBox = itemView.findViewById(R.id.select_user_cb)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupChatUserViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.select_group_participants_rv_item,
            parent, false
        )
        return GroupChatUserViewHolder(itemView)

    }

    override fun onBindViewHolder(holder: GroupChatUserViewHolder, position: Int) {
        val userName = holder.userName
        val profileImage = holder.profileImage
        val checkBox = holder.checkBox

        checkBox.setOnClickListener {
            if (checkBox.isChecked) {
                selectedUser.add(userList[position].uid)
            } else if (!checkBox.isChecked) {
                selectedUser.remove(userList[position].uid)
            }
        }
        holder.itemView.apply {
            userName.text = userList[position].userName
            if (userList[position].profileImageUrl.isNotBlank()) {
                Glide.with(context).load(userList[position].profileImageUrl).dontAnimate()
                    .into(profileImage)
            }
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    fun getSelectedList(): MutableList<String> {
        return selectedUser
    }
}