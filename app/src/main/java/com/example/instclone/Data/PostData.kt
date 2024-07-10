package com.example.instclone.Data

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize

@Parcelize
data class PostData(
    val userId: String? = null,
    val postId: String? = null,
    val username: String? = null,
    val userImage: String? = null,
    val postImage: String? = null,
    val description: String? = null,
    val time:Long? = null,
    var likes:List<String>?=null,
    val searchTerms:List<String>?=null,
    //val comments:List<String>?=null,
):Parcelable