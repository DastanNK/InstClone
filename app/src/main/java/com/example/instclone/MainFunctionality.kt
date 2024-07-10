package com.example.instclone

import android.widget.Toast
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter

fun navigateTo(destination: String, navController: NavController) {
    navController.navigate(destination) {
        popUpTo(destination)
        launchSingleTop = true
    }
}

@Composable
fun CommonProgressSpiner() {
    Row(
        modifier = Modifier.fillMaxSize().clickable(enabled = false) {},
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun NotificationMessage(viewModule: InstViewModule) {
    val msg = viewModule.popNotification.value
    Toast.makeText(LocalContext.current, "$msg", Toast.LENGTH_SHORT).show()
}

@Composable
fun CheckSignedIn(navController: NavController, viewModule: InstViewModule) {
    val alreadyLoggedIn = remember { mutableStateOf(false) }
    if (viewModule.isLoginSuccess.value && !alreadyLoggedIn.value) {
        alreadyLoggedIn.value = true
        navController.navigate(Screen.Feed.route) {
            popUpTo(0)
        }
    }
}

@Composable
fun CommonImage(data: String?) {
    val painter = rememberImagePainter(data = data)
    Image(
        painter = painter,
        contentDescription = null, contentScale = ContentScale.Crop
    )
    if (painter.state is ImagePainter.State.Loading) {
        CommonProgressSpiner()
    }
}

@Composable
fun UserImageCard(data: String?) {
    Card(modifier = Modifier.padding(8.dp).size(64.dp), shape = CircleShape) {
        if (data.isNullOrEmpty()) {
            Image(painter = painterResource(id = R.drawable.baseline_account_circle_24), contentDescription = null)
        } else {
            CommonImage(data = data)
        }
    }
}

private enum class LikeIconSize {
    SMALL,
    LARGE
}

@Composable
fun LikeAnimation(like: Boolean = true) {
    var sizeState by remember { mutableStateOf(LikeIconSize.SMALL) }
    val transition = updateTransition(targetState = sizeState, label = "")
    val size by transition.animateDp(
        label = "",
        transitionSpec = {
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        }
    ) { state ->
        when (state) {
            LikeIconSize.SMALL -> 25.dp
            LikeIconSize.LARGE -> 50.dp
        }
    }
    Image(
        painterResource(if (like) R.drawable.favorite else R.drawable.unfavorite),
        contentDescription = null,
        modifier = Modifier.size(size),
        colorFilter = ColorFilter.tint(
            if (like) Color.Red else Color.Gray
        )
    )
    sizeState=LikeIconSize.LARGE
}

