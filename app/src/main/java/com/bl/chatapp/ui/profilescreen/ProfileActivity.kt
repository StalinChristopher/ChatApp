package com.bl.chatapp.ui.profilescreen

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.bl.chatapp.R
import com.bl.chatapp.common.Constants.IMAGE_FROM_GALLERY_CODE
import com.bl.chatapp.common.Constants.STORAGE_PERMISSION_CODE
import com.bl.chatapp.common.Constants.USER_DETAILS
import com.bl.chatapp.common.SharedPref
import com.bl.chatapp.common.Validator
import com.bl.chatapp.databinding.ActivityProfilescreenBinding
import com.bl.chatapp.ui.home.HomeActivity
import com.bl.chatapp.viewmodels.SharedViewModel
import com.bl.chatapp.wrappers.UserDetails
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView

class ProfileActivity : AppCompatActivity(){
    private lateinit var binding: ActivityProfilescreenBinding
    private lateinit var currentUser: UserDetails
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var profileImageButton: CircleImageView
    private lateinit var updateButton: Button
    private lateinit var pleaseWaitDialog: Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedViewModel = ViewModelProvider(this)[SharedViewModel::class.java]
        val userId = SharedPref.getInstance(this).getUserId()
        sharedViewModel.getUserInfoFromId(userId)
        binding = ActivityProfilescreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        observers()
        pleaseWaitDialog = Dialog(this)
        pleaseWaitDialog.setContentView(R.layout.dialog_loading)
        profileImageButton = binding.profileImageButton
        updateButton = binding.profileOkButton
        listeners()
    }

    private fun listeners() {
        profileImageButton.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                selectImageFromGallery()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE
                )
            }
        }

        updateButton.setOnClickListener {
            if(Validator.verifyUserName(this, binding.profileUsername)) {
                var latestUserName = binding.profileUsername.text.toString()
                var latestStatus = binding.profileStatus.text.toString()
                var updateUser = UserDetails(currentUser.uid, userName = latestUserName,
                    status = latestStatus, phone = currentUser.phone,
                    profileImageUrl = currentUser.profileImageUrl )
                currentUser = updateUser
                sharedViewModel.updateUserProfileDetails(updateUser)
            }

        }
    }

    private fun selectImageFromGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, IMAGE_FROM_GALLERY_CODE)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_FROM_GALLERY_CODE && data != null) {
            var imageUri = data.data
            pleaseWaitDialog.show()
            profileImageButton.setImageURI(imageUri)
            sharedViewModel.setProfileImage(imageUri!!, currentUser)
        }
    }

    private fun observers() {
        sharedViewModel.getUserInfoStatus.observe(this, {
            if(it != null) {
                SharedPref.getInstance(this).addUserId(it.uid)
                currentUser = it
                initializeProfile(it)
            }
        })

        sharedViewModel.setUserProfileImageStatus.observe(this, {
            if(it != null) {
                pleaseWaitDialog.hide()
                val url = it.toString()
                currentUser.profileImageUrl = url
            }
        })

        sharedViewModel.updateUserInfoStatus.observe(this, {
            if(it) {
                pleaseWaitDialog.hide()
                gotoHomeActivity()

            }
        })

        sharedViewModel.userInfoFromIdStatus.observe(this, {
            if(it != null) {
                currentUser = it
                initializeProfile(it)
            }
        })
    }

    private fun initializeProfile(user: UserDetails) {
        pleaseWaitDialog.show()
        if(user.profileImageUrl.isNotEmpty()) {
            Glide.with(this).load(user.profileImageUrl).dontAnimate().into(profileImageButton)
        }
        binding.profileUsername.setText(user.userName)
        binding.profileStatus.setText(user.status)
        pleaseWaitDialog.hide()
    }

    private fun gotoHomeActivity() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.putExtra(USER_DETAILS, currentUser)
        this.finish()
        startActivity(intent)
    }
}