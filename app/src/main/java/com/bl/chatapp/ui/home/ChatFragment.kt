package com.bl.chatapp.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bl.chatapp.R
import com.bl.chatapp.common.Constants.CURRENT_USER
import com.bl.chatapp.common.Constants.FOREIGN_USER
import com.bl.chatapp.common.Constants.MESSAGE_LIST
import com.bl.chatapp.common.SharedPref
import com.bl.chatapp.data.models.Chat
import com.bl.chatapp.data.models.Message
import com.bl.chatapp.databinding.ChatFragmentBinding
import com.bl.chatapp.ui.chatdetails.ChatDetailsActivity
import com.bl.chatapp.ui.home.adapters.ChatOnItemClickListener
import com.bl.chatapp.ui.home.adapters.ChatUsersAdapter
import com.bl.chatapp.viewmodels.HomeViewModel
import com.bl.chatapp.viewmodels.UserViewModel
import com.bl.chatapp.wrappers.UserDetails

class ChatFragment : Fragment(R.layout.chat_fragment) {
    private lateinit var binding: ChatFragmentBinding
    private lateinit var userViewModel: UserViewModel
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var userRecyclerView: RecyclerView
    private lateinit var usersAdapter: ChatUsersAdapter
    private lateinit var currentUser: UserDetails

    companion object {
        private var userList: ArrayList<UserDetails> = ArrayList()
        private var chatList: ArrayList<Chat> = ArrayList()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = ChatFragmentBinding.bind(view)
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        var uid = SharedPref.getInstance(requireContext()).getUserId()
        currentUser =
            UserDetails(uid = uid, userName = "", status = "", phone = "", profileImageUrl = "")
        userViewModel.getUserInfoFromId(requireContext())
        initializeRecyclerView()
        observers()
        listeners()

    }

    private fun listeners() {
        usersAdapter.setOnItemClickListener(object : ChatOnItemClickListener {
            override fun onItemClick(position: Int) {
                Toast.makeText(requireContext(), "item clicked", Toast.LENGTH_SHORT).show()
                var foreignUser = userList[position]
                var messageList = ArrayList<Message>()
                for (item in chatList) {
                    if (item.participants.contains(foreignUser.uid)) {
                        messageList.addAll(item.messageList)
                    }
                }
                gotoChatDetailsScreen(currentUser, foreignUser, messageList)
            }

        })
    }

    private fun gotoChatDetailsScreen(
        currentUser: UserDetails, foreignUser: UserDetails,
        messageList: ArrayList<Message>
    ) {
        var intent = Intent(requireContext(), ChatDetailsActivity::class.java)
        intent.putExtra(MESSAGE_LIST, messageList)
        intent.putExtra(CURRENT_USER, currentUser)
        intent.putExtra(FOREIGN_USER, foreignUser)
        startActivity(intent)
    }

    private fun observers() {
        userViewModel.userInfoFromIdStatus.observe(viewLifecycleOwner, {
            currentUser = it
        })

        homeViewModel.getUserListStatus.observe(viewLifecycleOwner, {
            userList.clear()
            userList.addAll(it)
            usersAdapter.notifyDataSetChanged()
        })

        homeViewModel.getAllChatsOfUserStatus.observe(viewLifecycleOwner, {
            chatList.clear()
            chatList.addAll(it)
            usersAdapter.notifyDataSetChanged()
        })
    }

    private fun initializeRecyclerView() {
        usersAdapter = ChatUsersAdapter(userList, chatList, requireContext())
        userRecyclerView = binding.recyclerViewChatScreen
        userRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        userRecyclerView.setHasFixedSize(true)
        userRecyclerView.adapter = usersAdapter
        homeViewModel.getUserListFromDb(requireContext(), currentUser)
        homeViewModel.getAllChatsOfUser(requireContext())

    }
}