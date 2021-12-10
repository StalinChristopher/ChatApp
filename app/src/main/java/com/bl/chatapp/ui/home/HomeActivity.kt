package com.bl.chatapp.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.bl.chatapp.R
import com.bl.chatapp.common.Constants.SPLASH_SCREEN_VISIBILITY
import com.bl.chatapp.common.Constants.USER_DETAILS
import com.bl.chatapp.common.SharedPref
import com.bl.chatapp.databinding.ActivityHomeBinding
import com.bl.chatapp.ui.authentication.AuthenticationActivity
import com.bl.chatapp.ui.profilescreen.ProfileActivity
import com.bl.chatapp.ui.authentication.LoginViewModel
import com.bl.chatapp.viewmodels.SharedViewModel
import com.bl.chatapp.viewmodels.ViewModelFactory
import com.bl.chatapp.wrappers.UserDetails
import com.google.android.material.tabs.TabLayout

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var homeToolbar : androidx.appcompat.widget.Toolbar
    private lateinit var currentUser : UserDetails
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var fragmentAdapter: FragmentAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedViewModel = ViewModelProvider(this)[SharedViewModel::class.java]
        homeToolbar = binding.homeToolbar
        tabLayout = binding.homeScreenTabLayout
        viewPager = binding.homeScreenViewPager
        setSupportActionBar(homeToolbar)
        loginViewModel = ViewModelProvider(
            this,
            ViewModelFactory(LoginViewModel())
        )[LoginViewModel::class.java]
        listeners()
        currentUser = intent.getSerializableExtra(USER_DETAILS) as UserDetails
        sharedViewModel.getUserData(currentUser)
        initializeTabLayout()
        observers()
    }

    private fun observers() {
        sharedViewModel.getUserInfoStatus.observe(this, {
            if(it != null) {
                SharedPref.getInstance(this).addUserId(it.uid)
                currentUser = it
            }
        })
    }

    private fun initializeTabLayout() {
        var fragmentManager = supportFragmentManager
        fragmentAdapter = FragmentAdapter(fragmentManager, lifecycle)
        viewPager.adapter = fragmentAdapter
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.chats)))
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.groups)))

        tabLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    viewPager.currentItem = tab.position
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                tabLayout.selectTab(tabLayout.getTabAt(position))
            }
        })
    }

    private fun listeners() {
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        var itemView = item.itemId
        when (itemView) {
            R.id.profile_menu_item -> gotoProfileScreen()
            R.id.logout_menu_item -> {
                SharedPref.getInstance(this).clearAll()
                loginViewModel.logOut()
                gotoAuthenticationActivity()
            }
        }
        return true
    }

    private fun gotoProfileScreen() {
        val intent = Intent(this, ProfileActivity::class.java)
        intent.putExtra(USER_DETAILS,currentUser)
        startActivity(intent)
    }

    private fun gotoAuthenticationActivity() {
        val intent = Intent(this, AuthenticationActivity::class.java)
        intent.putExtra(SPLASH_SCREEN_VISIBILITY,false)
        finish()
        startActivity(intent)
    }
}