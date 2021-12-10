package com.bl.chatapp.ui.home.groups

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bl.chatapp.R
import com.bl.chatapp.data.models.GroupInfo
import com.bl.chatapp.ui.home.OnItemClickListener
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView

class GroupListAdapter(
    private val groupList: ArrayList<GroupInfo>,
    private val context: Context

    ): RecyclerView.Adapter<GroupListAdapter.GroupItemViewHolder>() {

    private lateinit var groupItemListener: OnItemClickListener

    fun setOnItemClickListener(listener: OnItemClickListener) {
        groupItemListener = listener
    }

    class GroupItemViewHolder(itemView: View, listener: OnItemClickListener) :
        RecyclerView.ViewHolder(itemView) {
        private val groupName = itemView.findViewById<TextView>(R.id.recycler_item_userName)
        private val profileImage =
            itemView.findViewById<CircleImageView>(R.id.imageView_recycler_item)
        private val messageTextView =
            itemView.findViewById<TextView>(R.id.recycler_item_message_text_view)

        fun setUserInfo(context: Context, group: GroupInfo) {
            groupName.text = group.groupName
            profileImage.setImageResource(R.drawable.whatsapp_group_user)
        }

        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.chat_recycler_view_item,
            parent, false
        )

        return GroupItemViewHolder(itemView, groupItemListener)
    }

    override fun onBindViewHolder(holder: GroupItemViewHolder, position: Int) {
        val currentItem = groupList[position]
        holder.setUserInfo(context, currentItem)
    }

    override fun getItemCount(): Int {
        return groupList.size
    }

}