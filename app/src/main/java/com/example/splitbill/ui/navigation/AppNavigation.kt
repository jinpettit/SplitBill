package com.example.splitbill.ui.navigation


import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.splitbill.ui.screens.upload.UploadReceiptScreen
import com.example.splitbill.ui.screens.upload.UploadReceiptViewModel
import com.example.splitbill.ui.screens.welcome.WelcomeScreen
import com.example.splitbill.utils.CameraUtils

object AppRoutes {
    const val WELCOME = "welcome"
    const val UPLOAD_RECEIPT = "upload_receipt"
    const val EDIT_RECEIPT = "edit_receipt"
    const val ASSIGN_ITEMS = "assign_items"
    const val BILL_SUMMARY = "bill_summary"
}

@Composable
fun AppNavigation(navController: NavHostController = rememberNavController(), cameraUtils: CameraUtils = CameraUtils(LocalContext.current)) {
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
            val viewModel = remember {
                UploadReceiptViewModel(cameraUtils)
            }
            UploadReceiptScreen(
                onBackClick = { navController.popBackStack() },
                onCloseClick = { navController.navigate(AppRoutes.WELCOME) {
                    popUpTo(AppRoutes.WELCOME) { inclusive = true }
                }},
                onReceiptCaptured = { receiptData ->
                    navController.navigate(AppRoutes.EDIT_RECEIPT)
                },
                viewModel = viewModel
            )
        }

        composable(AppRoutes.EDIT_RECEIPT) {
        }

        composable(AppRoutes.ASSIGN_ITEMS) {
        }

        composable(AppRoutes.BILL_SUMMARY) {
        }
    }
}