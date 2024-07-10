package com.example.instclone

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.instclone.Data.PostData
import com.example.instclone.Screens.*
import com.example.instclone.ui.theme.InstCloneTheme
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            InstCloneTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    InstCloneApp()
                }
            }
        }
    }
}

sealed class Screen(val route: String) {
    object SignUp : Screen("signup")
    object LogIn : Screen("login")
    object Feed : Screen("feed")
    object Search : Screen("search")
    object MyPosts : Screen("myposts")
    object Profile : Screen("profile")
    object NewPosts : Screen("newposts/{encodedUri}") {
        fun createRoute(encodedUri: String) = "newposts/$encodedUri"
    }
    object SinglePost : Screen("posts/{postData}") {
        fun createRoute(postData: PostData) :String {
            return "posts/${Uri.encode(Gson().toJson(postData))}"
        }
    }
    object Comments : Screen("comments/{postId}") {
        fun createRoute(postId: String) = "comments/$postId"
    }
}

@Composable
fun InstCloneApp() {
    val navController = rememberNavController()
    val viewModel = hiltViewModel<InstViewModule>()
    if (viewModel.isProgress.value) {
        CommonProgressSpiner()
    }
    NotificationMessage(viewModel)
    NavHost(navController = navController, startDestination = Screen.SignUp.route) {
        composable(Screen.SignUp.route) {
            SignUpScreen(navController, viewModel)
        }
        composable(Screen.LogIn.route) {
            LogInScreen(navController, viewModel)
        }
        composable(Screen.Feed.route) {
            FeedScreen(navController, viewModel)
        }
        composable(Screen.Search.route) {
            SearchScreen(navController, viewModel)
        }
        composable(Screen.MyPosts.route) {
            MyPostScreen(navController, viewModel)
        }
        composable(Screen.Profile.route) {
            ProfileScreen(navController, viewModel)
        }
        composable(Screen.NewPosts.route) { navBackStackEntry ->
            NewPostScreen(navController, viewModel, navBackStackEntry.arguments?.getString("encodedUri"))
        }
        composable(Screen.SinglePost.route, arguments = listOf(navArgument("postData"){
            type= NavType.StringType
        })) { navBackStackEntry ->
            val post = navBackStackEntry.arguments?.getString("postData")
            val postData = Gson().fromJson(post, PostData::class.java)
            postData?.let {
                SinglePostScreen(navController, viewModel, it)
            }
        }
        composable(Screen.Comments.route) { navBackStackEntry ->
            val postId=navBackStackEntry.arguments?.getString("postId")
            postId?.let {
                CommentScreen(navController, viewModel, it)
            }
        }
    }
}

