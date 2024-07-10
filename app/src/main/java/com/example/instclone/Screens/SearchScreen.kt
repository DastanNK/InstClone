package com.example.instclone.Screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.instclone.BottomNavigation
import com.example.instclone.InstViewModule
import com.example.instclone.Screen

@Composable
fun SearchScreen(navController: NavController, viewModule: InstViewModule) {
    val searchTerms =  viewModule.searchProgress.value
    val searchPosts = viewModule.searchedPosts.value
    val searchWord= remember { mutableStateOf("") }
    val posts= viewModule.posts.value
    Column(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.weight(1f)) {
            SearchBar(
                searchWord.value, onSearchChange = {searchWord.value = it}, {viewModule.search(searchWord.value)})
            PostList(
                isContextLoading = false,
                postLoad = searchTerms,
                posts = searchPosts
            ) { postData ->
                val screenRoute = Screen.SinglePost.createRoute(postData)
                navController.navigate(screenRoute)
            }
        }
        BottomNavigation(BottomNavigation.Search, navController)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(searchTerms:String, onSearchChange:(String)->Unit, onSearch:()->Unit){
    TextField(
        value = searchTerms,
        onValueChange = onSearchChange,
        modifier = Modifier.fillMaxWidth(),
        shape = CircleShape,
        keyboardActions = KeyboardActions(
            onSearch = {
                onSearch()
            }
        ),
        maxLines = 1,
        trailingIcon = {
            IconButton(onClick = { onSearch() }) {
                Icon(Icons.Filled.Search, contentDescription = "Search")
            }
        },
        colors = TextFieldDefaults.textFieldColors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        )
    )

}