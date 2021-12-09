package com.bl.chatapp.ui.newgroup

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bl.chatapp.R
import com.bl.chatapp.common.Constants.USER_DETAILS
import com.bl.chatapp.databinding.UserListFragmentBinding
import com.bl.chatapp.wrappers.UserDetails

class UserListFragment : Fragment(R.layout.user_list_fragment) {
    private lateinit var binding: UserListFragmentBinding
    private lateinit var currentUser: UserDetails
    private lateinit var newGroupViewModel: NewGroupViewModel
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = UserListFragmentBinding.bind(view)
        currentUser = arguments?.getSerializable(USER_DETAILS) as UserDetails
        newGroupViewModel = ViewModelProvider(requireActivity())[NewGroupViewModel::class.java]

        newGroupViewModel.getUserListFromDb(currentUser)
    }
}