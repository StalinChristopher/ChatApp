package com.bl.chatapp.ui.home.groups.newgroup

import android.os.Bundle
import android.view.View
import android.widget.Toast
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
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = GroupNameFragmentBinding.bind(view)
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
        })
    }

    private fun gotoHomeActivity() {
        activity?.finish()
    }

    private fun listeners() {
        binding.groupNameFloatingButton.setOnClickListener {
            val groupName = binding.groupNameEditText.text.toString().trim()
            if(groupName.isBlank()) {
                binding.groupNameEditText.error = getString(R.string.group_name_empty_error)
            } else {
                newGroupViewModel.createGroup(participantsList, groupName)
            }
        }
    }

}