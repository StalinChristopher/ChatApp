package com.bl.chatapp.ui.chatdetails

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bl.chatapp.common.Constants
import com.bl.chatapp.common.Constants.CURRENT_USER
import com.bl.chatapp.common.Constants.FOREIGN_USER
import com.bl.chatapp.common.Constants.MESSAGE_LIST
import com.bl.chatapp.data.models.Message
import com.bl.chatapp.databinding.ActivityChatDetailsBinding
import com.bl.chatapp.ui.home.HomeActivity
import com.bl.chatapp.viewmodels.ChatDetailViewModel
import com.bl.chatapp.wrappers.UserDetails
import com.bumptech.glide.Glide


class ChatDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatDetailsBinding
    private lateinit var chatDetailViewModel: ChatDetailViewModel
    private lateinit var currentUser: UserDetails
    private lateinit var foreignUser: UserDetails
    private lateinit var messageList: ArrayList<Message>
    private lateinit var chatDetailAdapter: ChatDetailAdapter
    private lateinit var chatDetailRecyclerView: RecyclerView

    companion object {

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        chatDetailViewModel = ViewModelProvider(this)[ChatDetailViewModel::class.java]
        currentUser = intent.getSerializableExtra(CURRENT_USER) as UserDetails
        foreignUser = intent.getSerializableExtra(FOREIGN_USER) as UserDetails
        messageList = intent.getSerializableExtra(MESSAGE_LIST) as ArrayList<Message>
        chatDetailViewModel.createChannel(this, currentUser, foreignUser)
        initializeView()
        initializeRecyclerView()
        listeners()
        observers()
    }

    private fun initializeRecyclerView() {
        chatDetailAdapter = ChatDetailAdapter(this, messageList, currentUser)
        chatDetailRecyclerView = binding.chatDetailRecyclerView
        var linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.reverseLayout = true
        chatDetailRecyclerView.layoutManager = linearLayoutManager
        chatDetailRecyclerView.setHasFixedSize(true)
        chatDetailRecyclerView.adapter = chatDetailAdapter
    }

    private fun initializeView() {
        binding.chatDetailReceiverTextView.setText(foreignUser.userName)
        if(foreignUser.profileImageUrl.isNotBlank()) {
            Glide.with(this).load(foreignUser.profileImageUrl).dontAnimate().
            into(binding.chatDetailProfileImage)
        }
    }

    private fun listeners() {
        binding.sendMessageButton.setOnClickListener {
            var messageText = binding.chatDetailEditText.text.toString()
            if(messageText.isNotBlank()) {
                chatDetailViewModel.sendNewMessage(this, currentUser, foreignUser,
                    messageText, "text")
            }
            binding.chatDetailEditText.setText("")
            binding.chatDetailEditText.clearFocus()
        }

        binding.backButtonChatDetail.setOnClickListener {
            gotoHomeActivity()
        }
    }

    private fun observers() {

    }

    private fun gotoHomeActivity() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.putExtra(Constants.USER_DETAILS, currentUser)
        finish()
        startActivity(intent)
    }
}