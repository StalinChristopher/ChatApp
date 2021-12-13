package com.bl.chatapp.data.models

import java.io.Serializable

data class GroupInfo(
     val groupId: String,
     val groupName: String,
     val groupImageUrl: String,
     val participants: ArrayList<String>
): Serializable
