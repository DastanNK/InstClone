package com.example.instclone.Screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.instclone.*
import com.example.instclone.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogInScreen(navController: NavController
                , viewModule: InstViewModule
    ) {
    CheckSignedIn(navController, viewModule)
    val email = remember { mutableStateOf(TextFieldValue()) }
    val password = remember { mutableStateOf(TextFieldValue()) }
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxWidth()
            .wrapContentHeight()
            .verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Image(painter = painterResource(R.drawable.ig_logo), contentDescription = "Sign Up")
            Text("LogIn", fontSize = 32.sp, modifier = Modifier.padding(16.dp))
            OutlinedTextField(value = email.value, onValueChange = {email.value=it }, modifier = Modifier.fillMaxWidth().padding(8.dp), label = { Text("Email") })
            OutlinedTextField(value = password.value, onValueChange = {password.value=it }, modifier = Modifier.fillMaxWidth().padding(8.dp), label = { Text("Password") }, visualTransformation = PasswordVisualTransformation())
            Button(onClick = {
                viewModule.Login(email.value.text, password.value.text)

            }){
                Text("Log In")
            }
            Text(text = "Don' have an account?  Go to sign up", fontSize = 16.sp, modifier = Modifier.padding(16.dp).clickable {
                navigateTo( Screen.SignUp.route, navController)
            })

        }
    }
}