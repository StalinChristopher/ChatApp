package com.bl.chatapp.ui.home.groups.newgroup

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bl.chatapp.R
import com.bl.chatapp.common.Constants
import com.bl.chatapp.common.Constants.USER_DETAILS
import com.bl.chatapp.databinding.GroupNameFragmentBinding
import com.bl.chatapp.wrappers.UserDetails

class GroupNameFragment: Fragment(R.layout.group_name_fragment) {
    private lateinit var binding: GroupNameFragmentBinding
    private lateinit var newGroupViewModel: NewGroupViewModel
    private lateinit var participantsList: ArrayList<String>
    private lateinit var currentUser: UserDetails
    private lateinit var pleaseWaitDialog: Dialog
    private var groupImageUri: Uri? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = GroupNameFragmentBinding.bind(view)
        pleaseWaitDialog = Dialog(requireContext())
        pleaseWaitDialog.setContentView(R.layout.dialog_loading)
        newGroupViewModel = ViewModelProvider(requireActivity())[NewGroupViewModel::class.java]
        participantsList = arguments?.getStringArrayList(Constants.PARTICIPANTS) as ArrayList<String>
        currentUser = arguments?.getSerializable(USER_DETAILS) as UserDetails
        listeners()
        observers()
    }

    private fun observers() {
        newGroupViewModel.groupCreatedStatus.observe(viewLifecycleOwner, {
            if(it) {
                gotoHomeActivity()
            } else {
                Toast.makeText(requireContext(),
                    "Group could not be created. Please try again", Toast.LENGTH_SHORT).show()
            }
            pleaseWaitDialog.dismiss()
        })
    }

    private fun gotoHomeActivity() {
        activity?.finish()
    }

    private fun listeners() {

        binding.groupImageView.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                selectImageFromGallery()
            } else {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.STORAGE_PERMISSION_CODE
                )
            }
        }
        binding.groupNameFloatingButton.setOnClickListener {
            val groupName = binding.groupNameEditText.text.toString().trim()
            if(groupName.isBlank()) {
                binding.groupNameEditText.error = getString(R.string.group_name_empty_error)
            } else {
                newGroupViewModel.createGroup(participantsList, groupName, groupImageUri)
                pleaseWaitDialog.show()
            }
        }

        binding.groupNameBackButton.setOnClickListener {
            activity?.run {
                supportFragmentManager.popBackStack()
            }
        }
    }

    private fun selectImageFromGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, Constants.IMAGE_FROM_GALLERY_CODE)
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
                    requireContext(),
                    getString(R.string.storage_access_required_toast),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.IMAGE_FROM_GALLERY_CODE && data != null) {
            var imageUri = data.data
            pleaseWaitDialog.show()
            binding.groupImageView.setImageURI(imageUri)
            groupImageUri = imageUri
            pleaseWaitDialog.dismiss()
        }
    }

}