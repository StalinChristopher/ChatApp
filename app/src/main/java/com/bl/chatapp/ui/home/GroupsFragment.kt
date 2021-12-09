package com.bl.chatapp.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bl.chatapp.R
import com.bl.chatapp.common.Constants.USER_DETAILS
import com.bl.chatapp.common.SharedPref
import com.bl.chatapp.databinding.GroupFragmentBinding
import com.bl.chatapp.ui.newgroup.NewGroupActivity
import com.bl.chatapp.viewmodels.GroupViewModel
import com.bl.chatapp.viewmodels.UserViewModel
import com.bl.chatapp.viewmodels.ViewModelFactory
import com.bl.chatapp.wrappers.UserDetails

class GroupsFragment : Fragment(R.layout.group_fragment) {
    private lateinit var groupViewModel: GroupViewModel
    private lateinit var binding: GroupFragmentBinding
    private lateinit var currentUser: UserDetails
    private lateinit var userViewModel: UserViewModel
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = GroupFragmentBinding.bind(view)
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        groupViewModel = ViewModelProvider(this, ViewModelFactory(GroupViewModel()))[GroupViewModel::class.java]
        var uid = SharedPref.getInstance(requireContext()).getUserId()
        currentUser = UserDetails(uid = uid, userName = "", status = "", phone = "", profileImageUrl = "")
        userViewModel.getUserInfoFromId(uid)
        observers()
        listeners()


    }

    private fun observers() {
        userViewModel.userInfoFromIdStatus.observe(viewLifecycleOwner, {
            currentUser = it
        })
    }

    private fun listeners() {
        binding.groupFragmentFloatingButton.setOnClickListener {
            gotoCreateGroupActivity(currentUser)
        }
    }

    private fun gotoCreateGroupActivity(currentUser: UserDetails) {
        val intent = Intent(requireActivity(), NewGroupActivity::class.java)
        intent.putExtra(USER_DETAILS, currentUser)
        startActivity(intent)
    }
}