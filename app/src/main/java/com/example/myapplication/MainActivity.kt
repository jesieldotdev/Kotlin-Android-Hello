package com.example.myapplication

import DataStoreManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.screens.LoginScreen
import com.example.myapplication.screens.TodoScreen

class MainActivity : ComponentActivity() {
    private lateinit var dataStoreManager: DataStoreManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dataStoreManager = DataStoreManager(applicationContext)
        setContent {
            MaterialTheme {
                AppNav(dataStoreManager=dataStoreManager)
            }
        }
    }
}

@Composable
fun AppNav(dataStoreManager: DataStoreManager) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "todo") {
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("todo") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }
        composable("todo") {
            TodoScreen(dataStoreManager)
        }
    }
}

