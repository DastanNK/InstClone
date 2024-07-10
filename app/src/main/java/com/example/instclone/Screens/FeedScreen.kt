package com.example.instclone.Screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.example.instclone.*
import com.example.instclone.Data.PostData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoilApi::class)
@Composable
fun FeedScreen(navController: NavController, viewModule: InstViewModule) {
    val feedList = viewModule.PostsFeed.value
    val load=viewModule.postFeedLoading.value

    Column(modifier = Modifier.fillMaxSize()) {
        if(load){
            CommonProgressSpiner()
        }
        Column(modifier = Modifier.weight(1f)) {
            LazyColumn {
                items(feedList) { feedList ->
                    EachItem(feedList, navController, viewModule)
                }
            }
        }
        BottomNavigation(BottomNavigation.Feed, navController)
    }

}

@Composable
fun EachItem(feedList:PostData, navController: NavController, viewModule: InstViewModule) {
    val likeAnimation = remember { mutableStateOf(false) }
    val dislikeAnimation = remember { mutableStateOf(false) }
    Column(modifier = Modifier.padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Card(
                shape = CircleShape,
                border = BorderStroke(width = 2.dp, color = Color.White),
                modifier = Modifier.size(32.dp)
            ) {
                Image(
                    rememberImagePainter(feedList.userImage),
                    contentDescription = null
                )
            }
            Text(feedList.username.toString())
        }
        Box(
            modifier = Modifier.fillMaxWidth().pointerInput(Unit){
                detectTapGestures(
                    onDoubleTap = {
                        viewModule.onLikePost(feedList)
                        if(feedList.likes?.contains(viewModule.userData.value?.userId)==true){
                            dislikeAnimation.value = true
                        }else{
                            likeAnimation.value = true
                        }
                    },
                    onTap = {
                        navController.navigate(Screen.SinglePost.createRoute(feedList))
                    }
                )
            }, contentAlignment = Alignment.Center
        ){
            Image(
                rememberImagePainter(feedList.postImage),
                contentDescription = null,
                modifier = Modifier.heightIn(max = 700.dp).fillMaxWidth(),
                contentScale = ContentScale.Fit
            )
            if(likeAnimation.value) {
                CoroutineScope(Dispatchers.Main).launch {
                    delay(1000)
                    likeAnimation.value = false
                }
                LikeAnimation()
            }
            if(dislikeAnimation.value) {
                CoroutineScope(Dispatchers.Main).launch {
                    delay(1000)
                    dislikeAnimation.value = false
                }
                LikeAnimation(false)
            }
        }

    }
}
