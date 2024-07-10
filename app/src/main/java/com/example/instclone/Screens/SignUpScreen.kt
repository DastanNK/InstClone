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
fun SignUpScreen(
    navController: NavController, viewModel: InstViewModule
) {
    CheckSignedIn(navController, viewModel)
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxWidth()
            .wrapContentHeight(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            val username = remember { mutableStateOf(TextFieldValue()) }
            val email = remember { mutableStateOf(TextFieldValue()) }
            val password = remember { mutableStateOf(TextFieldValue()) }
            Image(painter = painterResource(R.drawable.ig_logo), contentDescription = "Sign Up")
            Text("SignUp", fontSize = 32.sp, modifier = Modifier.padding(16.dp))
            OutlinedTextField(value = username.value, onValueChange = {username.value=it }, modifier = Modifier.padding(8.dp), label = {Text("Username")})
            OutlinedTextField(value = email.value, onValueChange = {email.value=it }, modifier = Modifier.padding(8.dp), label = {Text("Email")})
            OutlinedTextField(value = password.value, onValueChange = {password.value=it }, modifier = Modifier.padding(8.dp), label = {Text("Password")}, visualTransformation = PasswordVisualTransformation())
            Button(onClick = {
                viewModel.SigningUp(username.value.text, email.value.text, password.value.text)
            }){
                Text("Sign Up")
            }
            Text(text = "Already have an account?  Go to login", fontSize = 16.sp, modifier = Modifier.padding(16.dp).clickable {
                navigateTo( Screen.LogIn.route, navController)
            })

        }
    }
}