package com.example.instclone.Screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.instclone.Data.CommentData
import com.example.instclone.InstViewModule
import org.w3c.dom.Comment

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentScreen(navController: NavController, viewModule: InstViewModule, postId:String) {
    var comment by remember { mutableStateOf("") }
    val commentList = viewModule.commentData.value
    LaunchedEffect(Unit) {
        viewModule.retrieveComment(postId)
    }
    Column (modifier = Modifier.fillMaxSize()) {
        Text("Back", modifier = Modifier.padding(8.dp).clickable { navController.navigateUp() })
        Column(modifier = Modifier.weight(1f)) {
            LazyColumn(modifier = Modifier.padding(8.dp)) {
                items(commentList) {comment->
                    EachItem(comment)
                }
            }

        }
        TextField(value =comment , onValueChange = { comment=it }, modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = {
                    viewModule.writeComment(comment, postId)
                }) {
                    Icon(Icons.Filled.Send, contentDescription = "")
                }
            }
        )

    }
}

@Composable
fun EachItem(comment:CommentData){
    Row(modifier = Modifier.padding(8.dp).fillMaxWidth().heightIn(max=32.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(comment.username.toString(), modifier = Modifier.padding(end=8.dp))
        Text(comment.comment.toString())
    }
}