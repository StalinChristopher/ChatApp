package com.bl.chatapp.ui.home.groups

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bl.chatapp.R
import com.bl.chatapp.common.Constants.SELECTED_GROUP
import com.bl.chatapp.common.Constants.USER_DETAILS
import com.bl.chatapp.common.SharedPref
import com.bl.chatapp.data.models.GroupInfo
import com.bl.chatapp.databinding.GroupFragmentBinding
import com.bl.chatapp.ui.home.OnItemClickListener
import com.bl.chatapp.ui.home.chats.ChatUsersAdapter
import com.bl.chatapp.ui.home.groups.groupdetails.GroupChatDetailsActivity
import com.bl.chatapp.ui.home.groups.newgroup.NewGroupActivity
import com.bl.chatapp.viewmodels.GroupViewModel
import com.bl.chatapp.viewmodels.UserViewModel
import com.bl.chatapp.viewmodels.ViewModelFactory
import com.bl.chatapp.wrappers.UserDetails

class GroupsFragment : Fragment(R.layout.group_fragment) {
    private lateinit var groupViewModel: GroupViewModel
    private lateinit var binding: GroupFragmentBinding
    private lateinit var currentUser: UserDetails
    private lateinit var userViewModel: UserViewModel
    private lateinit var groupRecyclerView: RecyclerView
    private lateinit var groupListAdapter: GroupListAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = GroupFragmentBinding.bind(view)
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        groupViewModel = ViewModelProvider(this, ViewModelFactory(GroupViewModel()))[GroupViewModel::class.java]
        var uid = SharedPref.getInstance(requireContext()).getUserId()
        currentUser = UserDetails(uid = uid, userName = "", status = "", phone = "", profileImageUrl = "")
        userViewModel.getUserInfoFromId(uid)
        initializeRecyclerView()
        observers()
        listeners()
    }

    private fun initializeRecyclerView() {
        groupListAdapter = GroupListAdapter(groupViewModel.participants, requireContext())
        groupRecyclerView = binding.groupFragmentRecyclerView
        groupRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        groupRecyclerView.setHasFixedSize(true)
        groupRecyclerView.adapter = groupListAdapter
        groupViewModel.getAllGroupsOfUser(currentUser)
    }

    private fun observers() {
        userViewModel.userInfoFromIdStatus.observe(viewLifecycleOwner, {
            currentUser = it
        })

        groupViewModel.getAllGroupsStatus.observe(viewLifecycleOwner, {
            if(it) {
                groupListAdapter.notifyDataSetChanged()
            }
        })
    }

    private fun listeners() {
        binding.groupFragmentFloatingButton.setOnClickListener {
            gotoCreateGroupActivity(currentUser)
        }

        groupListAdapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(position: Int) {
                var selectedGroup = groupViewModel.participants[position]
                gotoGroupChatDetailsScreen(selectedGroup, currentUser)
            }
        })

    }

    private fun gotoGroupChatDetailsScreen(selectedGroup: GroupInfo, currentUser: UserDetails) {
        val intent = Intent(requireActivity(),GroupChatDetailsActivity::class.java)
        intent.putExtra(SELECTED_GROUP,selectedGroup)
        intent.putExtra(USER_DETAILS, currentUser)
        startActivity(intent)
    }

    private fun gotoCreateGroupActivity(currentUser: UserDetails) {
        val intent = Intent(requireActivity(), NewGroupActivity::class.java)
        intent.putExtra(USER_DETAILS, currentUser)
        startActivity(intent)
    }
}