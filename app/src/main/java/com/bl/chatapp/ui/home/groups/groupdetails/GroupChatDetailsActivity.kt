package com.bl.chatapp.ui.home.groups.groupdetails

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bl.chatapp.R
import com.bl.chatapp.common.Constants.SELECTED_GROUP
import com.bl.chatapp.common.Constants.USER_DETAILS
import com.bl.chatapp.data.models.GroupInfo
import com.bl.chatapp.databinding.ActivityChatDetailsBinding
import com.bl.chatapp.viewmodels.ViewModelFactory
import com.bl.chatapp.wrappers.UserDetails

class GroupChatDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatDetailsBinding
    private lateinit var groupChatDetailViewModel: GroupChatDetailViewModel
    private lateinit var currentUser: UserDetails
    private lateinit var selectedGroup: GroupInfo
    private lateinit var groupDetailRecyclerView: RecyclerView
    private lateinit var groupChatDetailsAdapter: GroupChatDetailsAdapter
    private lateinit var pleaseWaitDialog: Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getFromIntent()
        groupChatDetailViewModel = ViewModelProvider(
            this,
            ViewModelFactory(
                GroupChatDetailViewModel(currentUser, selectedGroup)
            )
        )[GroupChatDetailViewModel::class.java]
        pleaseWaitDialog = Dialog(this)
        pleaseWaitDialog.setContentView(R.layout.dialog_loading)
        pleaseWaitDialog.show()
        initializeView()
        observers()
        listeners()

    }

    private fun observers() {
        groupChatDetailViewModel.getUserInfoFromParticipantsStatus.observe(this, {
            if (it) {
                pleaseWaitDialog.dismiss()
                initializeRecyclerView()
            }
        })

        groupChatDetailViewModel.getAllMessageOfGroupStatus.observe(this, {
            if (it) {
                if (this::groupChatDetailsAdapter.isInitialized) {
                    groupChatDetailsAdapter.notifyDataSetChanged()
                }
            }
        })
    }

    private fun initializeRecyclerView() {
        groupChatDetailsAdapter = GroupChatDetailsAdapter(
            this,
            groupChatDetailViewModel.messageList, currentUser,
            selectedGroup, groupChatDetailViewModel.memberList
        )
        groupDetailRecyclerView = binding.chatDetailRecyclerView
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.stackFromEnd = true
        groupDetailRecyclerView.layoutManager = linearLayoutManager
        groupDetailRecyclerView.setHasFixedSize(true)
        groupDetailRecyclerView.adapter = groupChatDetailsAdapter
    }

    private fun initializeView() {
        binding.chatDetailReceiverTextView.text = selectedGroup.groupName
        binding.chatDetailProfileImage.setImageResource(R.drawable.whatsapp_group_user)
    }

    private fun listeners() {
        binding.sendMessageButton.setOnClickListener {
            var messageText = binding.chatDetailEditText.text.toString()
            if (messageText.isNotBlank()) {
                groupChatDetailViewModel.sendMessageToGroup(
                    currentUser,
                    selectedGroup,
                    messageText,
                    "text"
                )
            }
            binding.chatDetailEditText.setText("")
            binding.chatDetailEditText.clearFocus()
        }

        binding.backButtonChatDetail.setOnClickListener {
            finish()
        }
    }

    private fun getFromIntent() {
        currentUser = intent.getSerializableExtra(USER_DETAILS) as UserDetails
        selectedGroup = intent.getSerializableExtra(SELECTED_GROUP) as GroupInfo
    }
}