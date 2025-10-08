package com.exam.simplecustomerregistrationapp

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.exam.simplecustomerregistrationapp.ui.customer.CustomerListScreen
import com.exam.simplecustomerregistrationapp.ui.customer.CustomerRegistrationScreen
import com.exam.simplecustomerregistrationapp.ui.model.Screen
import com.exam.simplecustomerregistrationapp.ui.theme.SimpleCustomerRegistrationAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SimpleCustomerRegistrationAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController, startDestination = Screen.CustomerList.route
    ) {
        composable(Screen.CustomerList.route) {
            CustomerListScreen(navController = navController)
        }

        composable(
            Screen.CustomerRegistration.route + "/customerId={customerId}",
            arguments = listOf(navArgument("customerId") { nullable = true})
        ) { backStackEntry ->
            CustomerRegistrationScreen(
                navController = navController,
                customerId = backStackEntry.arguments?.getString("customerId")
            )
        }
    }
}