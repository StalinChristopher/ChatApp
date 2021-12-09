package com.bl.chatapp.ui.newgroup

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bl.chatapp.R
import com.bl.chatapp.common.Constants.PARTICIPANTS
import com.bl.chatapp.common.Constants.USER_DETAILS
import com.bl.chatapp.databinding.UserListFragmentBinding
import com.bl.chatapp.ui.home.adapters.ChatUsersAdapter
import com.bl.chatapp.ui.home.adapters.GroupChatUserAdapter
import com.bl.chatapp.wrappers.UserDetails
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.ArrayList

class UserListFragment : Fragment(R.layout.user_list_fragment) {
    private lateinit var binding: UserListFragmentBinding
    private lateinit var currentUser: UserDetails
    private lateinit var newGroupViewModel: NewGroupViewModel
    private lateinit var userRecyclerView: RecyclerView
    private lateinit var adapter: GroupChatUserAdapter
    private lateinit var addFab: FloatingActionButton
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = UserListFragmentBinding.bind(view)
        addFab = binding.userListFloatingButton
        currentUser = arguments?.getSerializable(USER_DETAILS) as UserDetails
        newGroupViewModel = ViewModelProvider(requireActivity())[NewGroupViewModel::class.java]
        newGroupViewModel.getUserListFromDb(currentUser)
        addFab.setOnClickListener {
            gotoSetGroupNamePage()
        }
        initialiseRecycleView()
        observe()
    }

    private fun gotoSetGroupNamePage() {
        val selectedList = adapter.getSelectedList()
        if (selectedList.size != 0) {
            val bundle = Bundle()
            bundle.putStringArrayList(PARTICIPANTS, selectedList as ArrayList<String>)
            val groupNameFragment = GroupNameFragment()
            groupNameFragment.arguments = bundle
            activity?.run {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.new_group_fragment_container_id, groupNameFragment)
                    .commit()
            }
        }
    }


private fun observe() {
    newGroupViewModel.getUserListStatus.observe(viewLifecycleOwner, {
        if (it) {
            adapter.notifyDataSetChanged()
        }
    })
}

private fun initialiseRecycleView() {
    adapter = GroupChatUserAdapter(newGroupViewModel.userList, requireContext())
    userRecyclerView = binding.userListRecyclerView
    userRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    userRecyclerView.setHasFixedSize(true)
    userRecyclerView.adapter = adapter
    newGroupViewModel.getUserListFromDb(currentUser)
}
}