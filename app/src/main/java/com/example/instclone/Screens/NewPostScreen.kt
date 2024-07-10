package com.example.instclone.Screens

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.instclone.CommonProgressSpiner
import com.example.instclone.InstViewModule
import com.example.instclone.Screen
import com.example.instclone.navigateTo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewPostScreen(navController: NavController, viewModule: InstViewModule, encodedUri: String?) {
    val scrollState = rememberScrollState()
    val image by remember { mutableStateOf(encodedUri) }
    val description = remember { mutableStateOf("") }
    Column(modifier = Modifier.fillMaxSize().verticalScroll(scrollState)) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Button(onClick = {
                navigateTo(Screen.MyPosts.route, navController)
            }) {
                Text("Back")
            }
            Button(onClick = {
               viewModule.onNewPost(Uri.parse(image), description.value){

               }
                navigateTo(Screen.MyPosts.route, navController)
            }) {
                Text("Save")
            }
        }
        Image(
            painter = rememberImagePainter(image),
            contentDescription = null,
            modifier = Modifier.fillMaxWidth().padding(16.dp).defaultMinSize(minHeight = 150.dp)
        )
        OutlinedTextField(
            value = description.value,
            onValueChange = { description.value = it },
            modifier = Modifier.fillMaxWidth().height(150.dp).padding(16.dp),
            singleLine = false,
            label = { Text("Description") }
        )
        val inProgress = viewModule.isProgress.value
        if (inProgress) {
            CommonProgressSpiner()
        }
    }
}