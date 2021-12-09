package com.bl.chatapp.ui.home.groups.newgroup

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bl.chatapp.R
import com.bl.chatapp.common.Constants.USER_DETAILS
import com.bl.chatapp.wrappers.UserDetails

class NewGroupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_group)
        val currentUser = intent.getSerializableExtra(USER_DETAILS) as UserDetails
        gotoUserListFragment(currentUser)
    }

    private fun gotoUserListFragment(currentUser: UserDetails) {
        val userListFragment = UserListFragment()
        val bundle = Bundle()
        bundle.putSerializable(USER_DETAILS, currentUser)
        userListFragment.arguments = bundle
        supportFragmentManager.beginTransaction()
            .replace(R.id.new_group_fragment_container_id, userListFragment).commit()
    }

    override fun onBackPressed() {
        val count = supportFragmentManager.backStackEntryCount
        if(count == 0) {
            super.onBackPressed()
        } else {
            supportFragmentManager.popBackStack()
        }


    }
}