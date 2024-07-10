package com.example.instclone.Screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.instclone.Data.PostData
import com.example.instclone.InstViewModule
import com.example.instclone.R
import com.example.instclone.Screen

@Composable
fun SinglePostScreen(navController: NavController, viewModule: InstViewModule, postData: PostData) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text("Back", modifier = Modifier.padding(16.dp).clickable {
            navController.popBackStack()
        })
        Divider(modifier = Modifier.padding(2.dp), thickness = 1.dp)
        SinglePostDisplay(navController, viewModule, postData)
    }

}
@Composable
fun SinglePostDisplay(navController: NavController, viewModule: InstViewModule, postData: PostData){
    Column {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Card( shape = CircleShape, modifier = Modifier.size(32.dp).padding(4.dp)){
                Image(painter=rememberImagePainter(postData.userImage),contentScale = ContentScale.Crop, contentDescription = null)
            }
            Text(postData.username.toString(), textAlign = TextAlign.Center, modifier = Modifier.padding(4.dp), fontSize = 30.sp)
            if(viewModule.userData.value?.userId!=postData.userId){
                if(viewModule.userData.value?.following?.contains(postData.userId.toString()) == false){
                    Text("Follow", color = Color.Blue, modifier = Modifier.padding(4.dp).clickable {
                        viewModule.follow(postData.userId.toString())
                    }, fontSize = 22.sp)
                }else{
                    Text("Unfollow", color = Color.Gray, modifier = Modifier.padding(4.dp).clickable {
                        viewModule.follow(postData.userId.toString())
                    }, fontSize = 22.sp)
                }
            }

        }
        Image(rememberImagePainter(postData.postImage), modifier = Modifier.padding(8.dp).fillMaxWidth(),contentScale = ContentScale.FillWidth, contentDescription = null)
        Row{
            Image(imageVector = Icons.Filled.Favorite, modifier = Modifier.padding(4.dp), contentDescription = null, colorFilter = ColorFilter.tint(Color.Black))
            Text("${postData.likes?.size?:0} likes", modifier = Modifier.padding(4.dp), fontSize = 16.sp)
        }
        Row{
            Text(postData.username.toString(), fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(4.dp), color = Color.Black)
            Text(postData.description.toString(), fontSize = 16.sp, fontWeight = FontWeight.Normal, color = Color.Black, modifier = Modifier.padding(4.dp))
        }
        Text("10 comments", modifier = Modifier.padding(4.dp).clickable {
            navController.navigate(Screen.Comments.createRoute(postData.postId.toString()))
        }, fontSize = 12.sp)


    }


}