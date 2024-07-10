package com.example.instclone.Screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import com.example.instclone.*
import com.example.instclone.Data.PostData
import com.example.instclone.R
import com.google.gson.Gson

@Composable
fun MyPostScreen(navController: NavController, viewModule: InstViewModule) {
    val newPostImageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val encodedUri = Uri.encode(it.toString())
            val destination = Screen.NewPosts.createRoute(encodedUri)
            navController.navigate(destination)
        }

    }
    val posts by remember{ mutableStateOf(viewModule.posts.value)}
    val isContextLoading by remember{ mutableStateOf(viewModule.isProgress.value)}
    val postLoad by remember{ mutableStateOf(viewModule.refreshPostsProgress.value) }

    val name = remember { mutableStateOf(viewModule.userData.value?.name) }
    val username = remember { mutableStateOf(viewModule.userData.value?.username) }
    val bio = remember { mutableStateOf(viewModule.userData.value?.bio) }
    Column(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.weight(1f)) {
            Row(modifier = Modifier.fillMaxWidth().wrapContentHeight(), horizontalArrangement = Arrangement.Center) {
                ProfileImage(imageUrl = viewModule.userData.value?.imageUrl) {
                    newPostImageLauncher.launch("image/*")
                }
                Text("15\nposts", modifier = Modifier.padding(15.dp))
                Text("15\nfollowers", modifier = Modifier.padding(15.dp))
                Text("15\nfollowing ", modifier = Modifier.padding(15.dp))
            }
            Text(text = name.value ?: "")
            Text(text = "@${username.value ?: ""}")
            Text(text = bio.value ?: "", maxLines = 3)
            Card(
                border = BorderStroke(4.dp, MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.clickable {
                    navigateTo(Screen.Profile.route, navController)
                }.fillMaxWidth().padding(all = 4.dp).padding(top = 8.dp, bottom = 8.dp)
            ) {
                Text("Edit profile", modifier = Modifier.padding(4.dp).align(Alignment.CenterHorizontally))
            }

            PostList(
                isContextLoading = isContextLoading,
                postLoad = postLoad,
                posts = posts
            ) { post ->
                val postData= Uri.encode(Gson().toJson(post))
                navController.navigate("posts/$postData")
            }
        }


        BottomNavigation(BottomNavigation.MyPost, navController)
    }
    if (viewModule.refreshPostsProgress.value) {
        CommonProgressSpiner()
    }
}

@Composable
fun PostList(
    isContextLoading: Boolean,
    postLoad: Boolean,
    posts: List<PostData>,
    onPostClick: (PostData) -> Unit,
) {
    Column() {
        if (postLoad) {
            CommonProgressSpiner()
        } else if (posts.isEmpty()) {
            if (!isContextLoading) {
                Text("No posts available", modifier = Modifier.padding(15.dp))
            }
        } else {
            //Image(rememberImagePainter(posts[0].postImage), contentDescription = null)
            LazyVerticalGrid(columns = GridCells.Fixed(3)) {
                items(posts.size) { size ->
                    //Image(rememberImagePainter(posts[1].postImage), contentDescription = null, modifier = Modifier.defaultMinSize(100.dp))
                    PostImage(posts[size].postImage.toString(), onPostClick, posts[size])
                    //posts[size].postImage?.let { Post(it) }
                }
            }
        }
    }
}

/*@Composable
fun Post(imageUrl: String){
    //Text(imageUrl)
    Image(rememberImagePainter(imageUrl), contentDescription = null, modifier = Modifier.heightIn(max=100.dp).widthIn(max=100.dp))

}*/
@Composable
fun PostImage(imageUrl: String, onPostClick: (PostData) -> Unit, posts: PostData) {
    Card(modifier = Modifier.padding(all = 1.dp).defaultMinSize(100.dp).clickable {
        onPostClick(posts)
    }) {
        Image(rememberImagePainter(imageUrl), contentDescription = null, modifier = Modifier.heightIn(max=200.dp).widthIn(max=150.dp))
    }
}

@Composable
fun ProfileImage(imageUrl: String?, onClick: () -> Unit) {
    Box(modifier = Modifier.padding(top = 16.dp).clickable { onClick.invoke() }) {
        UserImageCard(imageUrl)
        Card(
            shape = CircleShape,
            border = BorderStroke(width = 2.dp, color = Color.White),
            modifier = Modifier.size(32.dp).align(Alignment.BottomEnd).padding(bottom = 8.dp, end = 8.dp)
        ) {
            Image(
                painterResource(R.drawable.baseline_add_24),
                contentDescription = null,
                modifier = Modifier.background(Color.Blue)
            )
        }
    }
}