package com.bl.chatapp.ui.home.chats

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bl.chatapp.R
import com.bl.chatapp.common.Constants.CURRENT_USER
import com.bl.chatapp.common.Constants.FOREIGN_USER
import com.bl.chatapp.common.SharedPref
import com.bl.chatapp.databinding.ChatFragmentBinding
import com.bl.chatapp.ui.home.chats.chatdetails.ChatDetailsActivity
import com.bl.chatapp.ui.home.OnItemClickListener
import com.bl.chatapp.viewmodels.SharedViewModel
import com.bl.chatapp.viewmodels.ViewModelFactory
import com.bl.chatapp.wrappers.UserDetails

class ChatFragment : Fragment(R.layout.chat_fragment) {
    private lateinit var binding: ChatFragmentBinding
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var chatViewModel: ChatViewModel
    private lateinit var userRecyclerView: RecyclerView
    private lateinit var usersAdapter: ChatUsersAdapter
    private lateinit var currentUser: UserDetails

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = ChatFragmentBinding.bind(view)
        sharedViewModel = ViewModelProvider(this)[SharedViewModel::class.java]
        chatViewModel = ViewModelProvider(
            this,
            ViewModelFactory(ChatViewModel())
        )[ChatViewModel::class.java]
        var uid = SharedPref.getInstance(requireContext()).getUserId()
        currentUser =
            UserDetails(uid = uid, userName = "", status = "", phone = "", profileImageUrl = "")
        val userId = SharedPref.getInstance(requireContext()).getUserId()
        sharedViewModel.getUserInfoFromId(userId)
        initializeRecyclerView()
        observers()
        listeners()
    }

    private fun listeners() {
        usersAdapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(position: Int) {
                var foreignUser = chatViewModel.userList[position]
                gotoChatDetailsScreen(currentUser, foreignUser)
            }
        })
    }

    private fun gotoChatDetailsScreen(
        currentUser: UserDetails, foreignUser: UserDetails
    ) {
        var intent = Intent(requireContext(), ChatDetailsActivity::class.java)
        intent.putExtra(CURRENT_USER, currentUser)
        intent.putExtra(FOREIGN_USER, foreignUser)
        startActivity(intent)
    }

    private fun observers() {
        sharedViewModel.userInfoFromIdStatus.observe(viewLifecycleOwner, {
            currentUser = it
        })

        chatViewModel.getUserListStatus.observe(viewLifecycleOwner, {
            if (it) {
                usersAdapter.notifyDataSetChanged()
            }
        })
    }

    private fun initializeRecyclerView() {
        usersAdapter = ChatUsersAdapter(chatViewModel.userList, requireContext())
        userRecyclerView = binding.recyclerViewChatScreen
        userRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        userRecyclerView.setHasFixedSize(true)
        userRecyclerView.adapter = usersAdapter
        chatViewModel.getUserListFromDb(currentUser)
    }
}