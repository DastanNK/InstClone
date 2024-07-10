package com.example.instclone.Screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.instclone.CommonImage
import com.example.instclone.InstViewModule
import com.example.instclone.Screen
import com.example.instclone.navigateTo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController, viewModule: InstViewModule) {
    val imageUrl = viewModule.userData.value?.imageUrl
    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { viewModule.uploadProfileImage(it) }
    }
    val name = remember { mutableStateOf(viewModule.userData.value?.name) }
    val username = remember { mutableStateOf(viewModule.userData.value?.username) }
    val bio = remember { mutableStateOf(viewModule.userData.value?.bio) }
    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Button(onClick = {
                navigateTo(Screen.MyPosts.route, navController)
            }) {
                Text("Back")
            }
            Button(onClick = {
                viewModule.updateInfo(name = name.value ?: "", username = username.value ?: "", bio = bio.value ?: "")
                navigateTo(Screen.MyPosts.route, navController)
            }) {
                Text("Save")
            }
        }

        Box(modifier = Modifier.height(IntrinsicSize.Min)) {
            Column(
                modifier = Modifier.padding(8.dp).fillMaxWidth().clickable {
                    launcher.launch("image/*")
                }, horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(shape = CircleShape, modifier = Modifier.padding(8.dp).size(100.dp)) {
                    CommonImage(data = imageUrl)
                }
                Text("Change profile picture")
            }
        }

        Row(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Text("Name", modifier = Modifier.width(100.dp))
            Spacer(modifier = Modifier.width(16.dp))
            TextField(value = name.value ?: "", onValueChange = { name.value = it }, modifier = Modifier.fillMaxWidth())
        }
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Text("Username", modifier = Modifier.width(100.dp))
            Spacer(modifier = Modifier.width(16.dp))
            TextField(
                value = username.value ?: "",
                onValueChange = { username.value = it },
                modifier = Modifier.fillMaxWidth()
            )
        }
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Text("Bio", modifier = Modifier.width(100.dp))
            Spacer(modifier = Modifier.width(16.dp))
            TextField(
                value = bio.value ?: "",
                onValueChange = { bio.value = it },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Button(modifier = Modifier.padding(16.dp), onClick = {
                viewModule.onLogout()
                navigateTo(Screen.LogIn.route, navController)
            }) {
                Text("Logout")

            }
        }


    }
}