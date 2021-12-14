package com.bl.chatapp.ui.home.chats.chatdetails

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bl.chatapp.R
import com.bl.chatapp.common.Constants
import com.bl.chatapp.common.Constants.CHAT
import com.bl.chatapp.common.Constants.CONVERSATION_TYPE
import com.bl.chatapp.common.Constants.CURRENT_USER
import com.bl.chatapp.common.Constants.FOREIGN_USER
import com.bl.chatapp.common.Constants.IMAGE_FROM_GALLERY_CODE
import com.bl.chatapp.common.Constants.IMAGE_PATH
import com.bl.chatapp.common.Constants.STORAGE_PERMISSION_CODE
import com.bl.chatapp.common.Constants.VIEW_IMAGE_ACTIVITY_REQUEST_CODE
import com.bl.chatapp.databinding.ActivityChatDetailsBinding
import com.bl.chatapp.ui.view_mage.ViewImageActivity
import com.bl.chatapp.viewmodels.ViewModelFactory
import com.bl.chatapp.wrappers.UserDetails
import com.bumptech.glide.Glide
import kotlinx.coroutines.ExperimentalCoroutinesApi


class ChatDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatDetailsBinding
    private lateinit var chatDetailViewModel: ChatDetailViewModel
    private lateinit var currentUser: UserDetails
    private lateinit var foreignUser: UserDetails
    private lateinit var chatDetailAdapter: ChatDetailAdapter
    private lateinit var chatDetailRecyclerView: RecyclerView
    private lateinit var pleaseWaitDialog: Dialog
    private var offset = 0L
    private var isLoading = false
    private var visibleItems: Int = 0
    private var totalItems = 0
    private var firstVisibleItem = 0

    @ExperimentalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        pleaseWaitDialog = Dialog(this)
        pleaseWaitDialog.setContentView(R.layout.dialog_loading)
        currentUser = intent.getSerializableExtra(CURRENT_USER) as UserDetails
        foreignUser = intent.getSerializableExtra(FOREIGN_USER) as UserDetails
        chatDetailViewModel = ViewModelProvider(
            this,
            ViewModelFactory(
                ChatDetailViewModel(currentUser, foreignUser)
            )
        )[ChatDetailViewModel::class.java]
        chatDetailViewModel.createChannel(currentUser, foreignUser)
        initializeView()
        initializeRecyclerView()
        listeners()
        observers()
    }

    private fun initializeRecyclerView() {
        chatDetailAdapter = ChatDetailAdapter(this, chatDetailViewModel.messageList, currentUser)
        chatDetailRecyclerView = binding.chatDetailRecyclerView
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = false
        chatDetailRecyclerView.layoutManager = linearLayoutManager
        chatDetailRecyclerView.setHasFixedSize(true)
        chatDetailRecyclerView.adapter = chatDetailAdapter
        chatDetailRecyclerView.post {chatDetailRecyclerView.smoothScrollToPosition(0)}

        chatDetailRecyclerView.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                visibleItems = (chatDetailRecyclerView.layoutManager as LinearLayoutManager).childCount
                totalItems = (chatDetailRecyclerView.layoutManager as LinearLayoutManager).itemCount
                firstVisibleItem = (chatDetailRecyclerView.layoutManager as LinearLayoutManager)
                    .findFirstVisibleItemPosition()

                if(!isLoading) {
                    if((visibleItems + firstVisibleItem) >= totalItems && firstVisibleItem >= 0) {
                        isLoading = true
                        showLoader()
                        if(offset != 0L) {
                            chatDetailViewModel.getPagedMessages(currentUser, foreignUser, offset)
                        }
                    }
                }
            }
        })
    }


    private fun initializeView() {
        binding.chatDetailGroupMembersLabel.visibility = View.GONE
        binding.chatDetailReceiverTextView.text = foreignUser.userName
        if (foreignUser.profileImageUrl.isNotBlank()) {
            Glide.with(this).load(foreignUser.profileImageUrl).dontAnimate()
                .into(binding.chatDetailProfileImage)
        }
    }

    private fun listeners() {
        binding.sendMessageButton.setOnClickListener {
            var messageText = binding.chatDetailEditText.text.toString()
            if (messageText.isNotBlank()) {
                chatDetailViewModel.sendNewMessage(currentUser, foreignUser,
                    messageText, "text"
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

        binding.chatDetailProfileImage.setOnClickListener {
            gotoForeignUserScreen(foreignUser)
        }

        binding.chatDetailReceiverTextView.setOnClickListener {
            gotoForeignUserScreen(foreignUser)
        }
    }

    private fun gotoForeignUserScreen(foreignUser: UserDetails) {
        val intent = Intent(this, ViewForeignUserActivity::class.java)
        intent.putExtra(FOREIGN_USER, foreignUser)
        startActivity(intent)
    }

    private fun selectImageFromGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, IMAGE_FROM_GALLERY_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_FROM_GALLERY_CODE && data != null) {
            val imageUri = data.data
            imageUri?.let { gotoViewImageActivity(it) }
        } else if(requestCode == VIEW_IMAGE_ACTIVITY_REQUEST_CODE && data!= null) {
            val resultImageUriString = data.getStringExtra(IMAGE_PATH)
            val resultImageUri = Uri.parse(resultImageUriString)
            chatDetailViewModel.uploadChatImageToCloud(resultImageUri)
            pleaseWaitDialog.show()
        }
    }

    private fun gotoViewImageActivity(imageUri: Uri) {
        val intent = Intent(this, ViewImageActivity::class.java)
        Log.i("ChatDetailsActivity", "before sending - ${imageUri.toString()}")
        intent.putExtra("IMAGE_PATH", imageUri.toString())
        startActivityForResult(intent, VIEW_IMAGE_ACTIVITY_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE && grantResults.isNotEmpty()) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(
                    this,
                    getString(R.string.storage_access_required_toast),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    @ExperimentalCoroutinesApi
    private fun observers() {
        chatDetailViewModel.getMessageListStatus.observe(this, {
            if(chatDetailViewModel.messageList.size != 0) {
                isLoading = false
                offset = chatDetailViewModel
                    .messageList[chatDetailViewModel.messageList.size - 1].sentTime
                chatDetailAdapter.notifyDataSetChanged()
//            chatDetailAdapter.setData(it)
//            chatDetailViewModel.messageList = it
            }


        })

        chatDetailViewModel.chatImageUploadStatus.observe(this, { message ->
            if(message != null) {
                pleaseWaitDialog.dismiss()
                chatDetailViewModel.sendPushNotification(message)
            }
        })

        chatDetailViewModel.sendMessageStatus.observe(this, { message ->
            if(message != null) {
                chatDetailViewModel.sendPushNotification(message)
            }
        })

        chatDetailViewModel.getPagedMessageStatus.observe(this, { messageList ->
            isLoading = false
            for(message in messageList) {
                chatDetailViewModel.messageList.add(message)
                offset = message.sentTime
            }
            chatDetailAdapter.notifyDataSetChanged()
            showLoader()
        })
    }

    private fun showLoader() {
        if(isLoading) {
            binding.chatDetailsProgressBar.visibility = View.VISIBLE
        } else {
            binding.chatDetailsProgressBar.visibility = View.GONE
        }
    }
}