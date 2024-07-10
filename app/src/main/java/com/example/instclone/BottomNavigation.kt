package com.example.instclone

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

enum class BottomNavigation(val Icon:Int, val route:String) {
    Feed(R.drawable.baseline_account_box_24, Screen.Feed.route),
    Search(R.drawable.baseline_content_paste_search_24, Screen.Search.route),
    MyPost(R.drawable.baseline_add_home_24, Screen.MyPosts.route)
}

@Composable
fun BottomNavigation(item: BottomNavigation, navController: NavController) {
    Row(modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(top = 4.dp)) {
        for(i in BottomNavigation.values()) {
            Image(painter = painterResource(i.Icon), contentDescription = i.route, modifier = Modifier.size(40.dp).padding(4.dp).weight(1f).clickable {
                navigateTo(i.route, navController)
            }, colorFilter = if (item.route == i.route) {
                ColorFilter.tint(Color.Black)
            }else{
                ColorFilter.tint(Color.Gray)
            })

        }
    }
}