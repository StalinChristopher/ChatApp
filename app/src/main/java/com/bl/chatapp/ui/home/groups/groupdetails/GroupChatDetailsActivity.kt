package com.bl.chatapp.ui.home.groups.groupdetails

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bl.chatapp.R
import com.bl.chatapp.common.Constants
import com.bl.chatapp.common.Constants.SELECTED_GROUP
import com.bl.chatapp.common.Constants.USER_DETAILS
import com.bl.chatapp.data.models.GroupInfo
import com.bl.chatapp.databinding.ActivityChatDetailsBinding
import com.bl.chatapp.ui.view_mage.ViewImageActivity
import com.bl.chatapp.viewmodels.ViewModelFactory
import com.bl.chatapp.wrappers.UserDetails
import com.bumptech.glide.Glide

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
//                    if(groupChatDetailsAdapter.itemCount != 0) {
//                        groupDetailRecyclerView.smoothScrollToPosition(0)
//                    }
                }
            }
        })

        groupChatDetailViewModel.groupChatImageUploadStatus.observe(this, { message ->
            if(message != null) {
                pleaseWaitDialog.dismiss()
                groupChatDetailViewModel.sendGroupNotifications(message)
            }
        })

        groupChatDetailViewModel.sendMessageToGroupStatus.observe(this, { message ->
            if(message != null) {
                groupChatDetailViewModel.sendGroupNotifications(message)
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
//        linearLayoutManager.stackFromEnd = true
        linearLayoutManager.reverseLayout = true
        groupDetailRecyclerView.layoutManager = linearLayoutManager
        groupDetailRecyclerView.setHasFixedSize(true)
        groupDetailRecyclerView.adapter = groupChatDetailsAdapter
    }

    private fun initializeView() {
        binding.chatDetailReceiverTextView.text = selectedGroup.groupName
        if(selectedGroup.groupImageUrl.isNotBlank()) {
            Glide.with(this).load(selectedGroup.groupImageUrl).into(binding.chatDetailProfileImage)
        } else {
            binding.chatDetailProfileImage.setImageResource(R.drawable.whatsapp_group_user)
        }
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

        binding.chatDetailCameraIcon.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                selectImageFromGallery()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.STORAGE_PERMISSION_CODE
                )
            }
        }
    }

    private fun selectImageFromGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, Constants.IMAGE_FROM_GALLERY_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.IMAGE_FROM_GALLERY_CODE && data != null) {
            val imageUri = data.data
            imageUri?.let { gotoViewImageActivity(it) }
        } else if(requestCode == Constants.VIEW_IMAGE_ACTIVITY_REQUEST_CODE && data!= null) {
            val resultImageUriString = data.getStringExtra(Constants.IMAGE_PATH)
            val resultImageUri = Uri.parse(resultImageUriString)
            groupChatDetailViewModel.uploadGroupChatImage(resultImageUri)
            pleaseWaitDialog.show()
        }
    }

    private fun gotoViewImageActivity(imageUri: Uri) {
        val intent = Intent(this, ViewImageActivity::class.java)
        intent.putExtra("IMAGE_PATH", imageUri.toString())
        startActivityForResult(intent, Constants.VIEW_IMAGE_ACTIVITY_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.STORAGE_PERMISSION_CODE && grantResults.isNotEmpty()) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(
                    this,
                    getString(R.string.storage_access_required_toast),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun getFromIntent() {
        currentUser = intent.getSerializableExtra(USER_DETAILS) as UserDetails
        selectedGroup = intent.getSerializableExtra(SELECTED_GROUP) as GroupInfo
    }
}