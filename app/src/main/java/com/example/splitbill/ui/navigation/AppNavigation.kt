package com.example.splitbill.ui.navigation


import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.splitbill.ui.screens.welcome.WelcomeScreen

object AppRoutes {
    const val WELCOME = "welcome"
    const val UPLOAD_RECEIPT = "upload_receipt"
    const val EDIT_RECEIPT = "edit_receipt"
    const val ASSIGN_ITEMS = "assign_items"
    const val BILL_SUMMARY = "bill_summary"
}

@Composable
fun AppNavigation(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = AppRoutes.WELCOME
    ) {
        composable(AppRoutes.WELCOME) {
            WelcomeScreen(
                onGetStartedClick = {
                    navController.navigate(AppRoutes.UPLOAD_RECEIPT)
                }
            )
        }

        composable(AppRoutes.UPLOAD_RECEIPT) {
        }

        composable(AppRoutes.EDIT_RECEIPT) {
        }

        composable(AppRoutes.ASSIGN_ITEMS) {
        }

        composable(AppRoutes.BILL_SUMMARY) {
        }
    }
}