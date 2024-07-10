package com.example.instclone

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.toLowerCase
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.instclone.Data.CommentData
import com.example.instclone.Data.Event
import com.example.instclone.Data.PostData
import com.example.instclone.Data.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.*
import javax.inject.Inject

const val USERS_COLLECTION = "users"
const val POST_COLLECTION = "posts"
const val COMMENTS_COLLECTION = "comments"

@HiltViewModel
class InstViewModule @Inject constructor(
    val auth: FirebaseAuth,
    val db: FirebaseFirestore,
    val storage: FirebaseStorage
) : ViewModel() {

    var isProgress = mutableStateOf(false)
    var isLoginSuccess = mutableStateOf(false)
    val userData = mutableStateOf<User?>(null)
    var popNotification = mutableStateOf<Event<String>?>(null)

    val refreshPostsProgress = mutableStateOf(false)
    val posts = mutableStateOf<List<PostData>>(listOf())

    val searchProgress = mutableStateOf(false)
    val searchedPosts = mutableStateOf<List<PostData>>(listOf())

    val postFeedLoading = mutableStateOf(false)
    val PostsFeed = mutableStateOf<List<PostData>>(listOf())

    val commentData = mutableStateOf<List<CommentData>>(listOf())


    init {
        //auth.signOut()
        val currentUser = auth.currentUser
        isLoginSuccess.value = currentUser != null
        currentUser?.uid?.let { uid ->
            getUserData(uid)
        }
    }

    fun Login(email: String, password: String) {
        isProgress.value = true
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener() { task ->
            if (task.isSuccessful) {
                isLoginSuccess.value = true
                isProgress.value = false
                auth.currentUser?.uid?.let { uid ->
                    getUserData(uid)
                }
            } else {
                isProgress.value = false
                isLoginSuccess.value = false
                handleException(task.exception)
            }
        }.addOnFailureListener {
            isLoginSuccess.value = false
            handleException(it, "Can not log in")
            isProgress.value = false
        }
    }

    fun SigningUp(username: String, email: String, password: String) {
        isProgress.value = true
        if (username.isEmpty() && email.isEmpty() && password.isEmpty()) {
            handleException(cause = "Please fill all fields")
            return
        }
        db.collection(USERS_COLLECTION).whereEqualTo("username", username).get().addOnSuccessListener { documents ->
            if (documents.size() > 0) {
                handleException(cause = "Already exists")
                isProgress.value = false
            } else {
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        isLoginSuccess.value = true
                        createOrUpdateUser(username = username)
                    } else {
                        handleException(it.exception, cause = "Failed to create user")
                    }
                    isProgress.value = false
                }
            }
        }.addOnFailureListener {
            handleException(it, cause = "Failed to create user")
        }
    }

    private fun createOrUpdateUser(
        name: String? = null,
        username: String? = null,
        bio: String? = null,
        imageUrl: String? = null,
        //following:List<String>?=null
    ) {
        val uid = auth.currentUser?.uid
        val userData = User(
            userId = uid,
            name = name ?: userData.value?.name,
            username = username ?: userData.value?.username,
            bio = bio ?: userData.value?.bio,
            imageUrl = imageUrl ?: userData.value?.imageUrl,
            following = userData.value?.following
        )

        uid?.let { uid ->
            isProgress.value = true
            db.collection(USERS_COLLECTION).document(uid).get()
                .addOnSuccessListener {
                    if (it.exists()) {
                        it.reference.update(userData.toMap())
                            .addOnSuccessListener {
                                this.userData.value = userData
                                isProgress.value = false
                            }
                            .addOnFailureListener {
                                handleException(it, "Cannot update user")
                                isProgress.value = false
                            }
                    } else {
                        db.collection(USERS_COLLECTION).document(uid).set(userData)
                        getUserData(uid)
                        isProgress.value = false
                    }
                }
                .addOnFailureListener { exc ->
                    handleException(exc, "Cannot create user")
                    isProgress.value = false
                }
        }
    }

    fun handleException(exception: Exception? = null, cause: String? = null) {
        exception?.printStackTrace()
        val errorMsg = exception?.localizedMessage ?: ""
        val message = if (errorMsg.isEmpty()) {
            "$cause"
        } else {
            "$cause:$errorMsg"
        }
        popNotification.value = Event(message)
    }

    private fun getUserData(uid: String) {
        isProgress.value = true
        db.collection(USERS_COLLECTION).document(uid).get().addOnSuccessListener { document ->
            val user = document.toObject<User>()
            userData.value = user
            isProgress.value = false
            refreshPosts()
            getFeed()
            popNotification.value = Event("User data retrieved")
        }.addOnFailureListener {
            handleException(it, cause = "Failed to get user")
            isProgress.value = false
        }
    }

    fun updateInfo(name: String, username: String, bio: String) {
        createOrUpdateUser(name, username, bio)
    }

    fun onLogout() {
        auth.signOut()
        userData.value = null
        isProgress.value = false
        isLoginSuccess.value = false
        searchedPosts.value = listOf()
        PostsFeed.value = listOf()
        commentData.value = listOf()
    }

    private fun uploadImage(uri: Uri, onSuccess: (Uri) -> Unit) {
        isProgress.value = true
        val storageRef = storage.reference
        val uuid = UUID.randomUUID()
        val imageRef = storageRef.child("image/$uuid")
        imageRef.putFile(uri).addOnSuccessListener {
            val result = it.metadata?.reference?.downloadUrl
            result?.addOnSuccessListener(onSuccess)
        }.addOnFailureListener {
            handleException(it, "Image upload has been failed")
            isProgress.value = false
        }
    }

    fun uploadProfileImage(uri: Uri) {
        uploadImage(uri) { uri ->
            createOrUpdateUser(imageUrl = uri.toString())
        }
    }

    fun onNewPost(uri: Uri, description: String, onPostSuccess: () -> Unit) {
        uploadImage(uri) { uri ->
            onCreatePost(uri, description, onPostSuccess)
        }
    }

    private fun onCreatePost(uri: Uri, description: String, onPostSuccess: () -> Unit) {
        isProgress.value = true
        val currentUid = auth.currentUser?.uid
        val currentUsername = userData.value?.username
        val currentUserImage = userData.value?.imageUrl
        if (currentUid != null) {
            val postUuid = UUID.randomUUID().toString()
            val fillerWords = listOf("the", "in", "be", "is", "of", "and", "or", "a", "in", "it")
            val searchTerms = description.split(" ", ".", "?", ",", "!", ";", "-", "#").map {
                it.lowercase()
            }.filter { it.isNotEmpty() && !fillerWords.contains(it) }
            val post = PostData(
                userId = currentUid,
                postId = postUuid,
                username = currentUsername,
                userImage = currentUserImage,
                postImage = uri.toString(),
                description = description,
                time = System.currentTimeMillis(),
                searchTerms = searchTerms
            )
            db.collection(POST_COLLECTION).document(postUuid).set(post).addOnSuccessListener {
                isProgress.value = false
                refreshPosts()
                onPostSuccess.invoke()
            }.addOnFailureListener {
                handleException(it, "Failed to create post")
                isProgress.value = false
            }
        } else {
            handleException(cause = "Failed to create post")
            onLogout()
        }
    }

    fun refreshPosts() {
        val currentUid = auth.currentUser?.uid
        if (currentUid != null) {
            refreshPostsProgress.value = true
            db.collection(POST_COLLECTION).whereEqualTo("userId", currentUid).get().addOnSuccessListener { documents ->
                convertPosts(documents, posts)
                refreshPostsProgress.value = false
            }.addOnFailureListener {
                handleException(it, "Can not refresh posts")
                refreshPostsProgress.value = false
            }
        } else {
            handleException(cause = "refresh unavailable")
            onLogout()
        }
    }

    private fun convertPosts(documents: QuerySnapshot, outState: MutableState<List<PostData>>) {
        val newPosts = mutableListOf<PostData>()
        documents.forEach { document ->
            val post = document.toObject<PostData>()
            newPosts.add(post)
        }
        val sortedPosts = newPosts.sortedByDescending { it.time }
        outState.value = sortedPosts
    }

    fun search(searchTerm: String) {
        searchProgress.value = true
        if (searchTerm.isNotEmpty()) {
            db.collection(POST_COLLECTION).whereArrayContains("searchTerms", searchTerm.trim().lowercase()).get()
                .addOnSuccessListener { documents ->
                    convertPosts(documents, searchedPosts)
                    searchProgress.value = false
                }.addOnFailureListener {
                    searchProgress.value = false
                    handleException(it, "Can not search posts")
                }
        }
    }

    fun follow(userId: String) {
        auth.currentUser?.uid?.let { currentUser ->
            val following = arrayListOf<String>()
            userData.value?.following?.let {
                following.addAll(it)
            }
            if (following.contains(userId)) {
                following.remove(userId)
            } else {
                following.add(userId)
            }
            db.collection(USERS_COLLECTION).document(currentUser).update("following", following).addOnSuccessListener {
                getUserData(currentUser)
            }
        }
    }

    private fun getFeed() {
        val following = userData.value?.following
        postFeedLoading.value = true
        if (following.isNullOrEmpty()) {
            getGeneralFeed()
        } else {
            db.collection(POST_COLLECTION).whereIn("following", following).get().addOnSuccessListener { documets ->
                convertPosts(documets, PostsFeed)
                if (PostsFeed.value.isEmpty()) {
                    getGeneralFeed()
                } else {
                    postFeedLoading.value = false
                }
            }.addOnFailureListener {
                handleException(it, "Can not get posts")
                postFeedLoading.value = false
            }
        }

    }

    private fun getGeneralFeed() {
        val currentTime = System.currentTimeMillis()
        val day = 24 * 60 * 60 * 1000
        db.collection(POST_COLLECTION).whereGreaterThan("time", currentTime - day).get()
            .addOnSuccessListener { documets ->
                convertPosts(documets, PostsFeed)
                postFeedLoading.value = false
            }.addOnFailureListener {
            handleException(it, "Can not get posts")
        }
    }

    fun onLikePost(postData: PostData) {
        auth.currentUser?.uid?.let { currentUser ->
            postData.likes?.let { like ->
                val newLikes = arrayListOf<String>()
                if (like.contains(currentUser)) {
                    newLikes.addAll(like.filter { it != currentUser })
                } else {
                    newLikes.addAll(like)
                    newLikes.add(currentUser)
                }
                postData.postId?.let { id ->
                    db.collection(POST_COLLECTION).document(id).update("likes", newLikes).addOnSuccessListener {
                        postData.likes = newLikes
                    }.addOnFailureListener {
                        handleException(it, "Can not like post")
                    }
                }
            }
        }
    }

    fun writeComment(comment: String, postId: String) {
        userData.value?.username.let { username ->
            val time = System.currentTimeMillis()
            val commentId = UUID.randomUUID().toString()
            val commentData = CommentData(commentId, postId, username, comment, time)
            db.collection(COMMENTS_COLLECTION).document(commentId).set(commentData).addOnSuccessListener {
                retrieveComment(postId)
            }.addOnFailureListener {
                handleException(it, "Can not write comment")
            }
        }
    }

    fun retrieveComment(postId: String) {
        db.collection(COMMENTS_COLLECTION).whereEqualTo("postId", postId).get().addOnSuccessListener { documents->
            val newCommentData= mutableListOf<CommentData>()
            documents.forEach { document ->
                val comData=document.toObject<CommentData>()
                newCommentData.add(comData)
            }
            val CommentDataSorted=newCommentData.sortedByDescending { it.time }
            commentData.value=CommentDataSorted
        }
    }
}