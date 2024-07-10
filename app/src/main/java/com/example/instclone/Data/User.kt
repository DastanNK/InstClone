package com.example.instclone.Data

data class User(
    val userId: String? = null,
    val name: String? = null,
    val username: String? = null,
    val email: String? = null,
    val imageUrl: String? = null,
    val bio: String? = null,
    val following: List<String>? = null
) {
    fun toMap() = mapOf(
        "userId" to userId,
        "name" to name,
        "username" to username,
        "imageUrl" to imageUrl,
        "bio" to bio,
        "following" to following
    )
}
