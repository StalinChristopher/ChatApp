package com.bl.chatapp.ui.authentication

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bl.chatapp.R
import com.bl.chatapp.common.Constants.USER_DETAILS
import com.bl.chatapp.databinding.NewUserFragmentBinding
import com.bl.chatapp.ui.home.HomeActivity
import com.bl.chatapp.viewmodels.UserViewModel
import com.bl.chatapp.wrappers.UserDetails
import com.google.android.material.textfield.TextInputEditText

class NewUserInfoFragment : Fragment(R.layout.new_user_fragment){
    private lateinit var binding: NewUserFragmentBinding
    private lateinit var userNameEditText: TextInputEditText
    private lateinit var statusEditText: TextInputEditText
    private lateinit var sendButton: Button
    private lateinit var userDetails: UserDetails
    private lateinit var userViewModel: UserViewModel
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = NewUserFragmentBinding.bind(view)
        userViewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]
        userNameEditText = binding.signupUsername
        statusEditText = binding.newUserStatus
        sendButton = binding.newUserOkButton
        userDetails = arguments?.get(USER_DETAILS) as UserDetails
        listeners()
        observers()

    }

    private fun observers() {
        userViewModel.newUserAddStatus.observe(viewLifecycleOwner) {
            if(it != null) {
                gotoHomeActivity(it)
            }
        }
    }

    private fun gotoHomeActivity(user: UserDetails) {
        val intent = Intent(requireContext(), HomeActivity::class.java)
        intent.putExtra(USER_DETAILS, user)
        requireActivity().finish()
        startActivity(intent)
    }

    private fun listeners() {
        sendButton.setOnClickListener {
            val userName = userNameEditText.text.toString()
            val statusText = statusEditText.text.toString()
            if(userName.isEmpty() && statusText.isEmpty()) {
                userNameEditText.error = getString(R.string.please_enter_username)
                statusEditText.error = getString(R.string.please_enter_current_status)
            } else {
                userViewModel.setUserData(requireContext(), userName, statusText, userDetails)
            }
        }
    }
}