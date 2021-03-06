package com.bl.chatapp.ui.home

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bl.chatapp.ui.home.chats.ChatFragment
import com.bl.chatapp.ui.home.groups.GroupsFragment

class FragmentAdapter(fragmentManager: FragmentManager, lifeCycle: Lifecycle) :
    FragmentStateAdapter(
        fragmentManager, lifeCycle
    ) {
    override fun getItemCount(): Int {
        return 2;
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ChatFragment()
            1 -> GroupsFragment()
            else -> ChatFragment()
        }
    }
}