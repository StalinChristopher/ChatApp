package com.bl.chatapp.ui.newgroup

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.bl.chatapp.R
import com.bl.chatapp.common.Constants



class GroupNameFragment: Fragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val list = arguments?.getStringArrayList(Constants.PARTICIPANTS)
        if(list != null){
            Log.d("selected list",list.toString())
        }
    }
    
}