package com.example.splitbill.ui.navigation


import com.example.splitbill.ui.screens.edit.EditReceiptViewModel
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.splitbill.data.models.Receipt
import com.example.splitbill.ui.screens.assign.AddParticipantDialog
import com.example.splitbill.ui.screens.assign.AssignItemsScreen
import com.example.splitbill.ui.screens.assign.AssignItemsViewModel
import com.example.splitbill.ui.screens.edit.EditReceiptScreen
import com.example.splitbill.ui.screens.upload.UploadReceiptScreen
import com.example.splitbill.ui.screens.upload.UploadReceiptViewModel
import com.example.splitbill.ui.screens.welcome.WelcomeScreen
import com.example.splitbill.utils.CameraUtils
import com.example.splitbill.utils.OCRUtils

object AppRoutes {
    const val WELCOME = "welcome"
    const val UPLOAD_RECEIPT = "upload_receipt"
    const val EDIT_RECEIPT = "edit_receipt"
    const val ASSIGN_ITEMS = "assign_items"
    const val BILL_SUMMARY = "bill_summary"

    fun editReceiptRoute(receiptUri: String): String {
        return "edit_receipt/${Uri.encode(receiptUri)}"
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation(navController: NavHostController = rememberNavController(),
                  cameraUtils: CameraUtils = CameraUtils(LocalContext.current),
                  ocrUtils: OCRUtils = OCRUtils(LocalContext.current)
) {
    val assignItemsViewModel = remember { AssignItemsViewModel() }

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
                    val receiptUri = receiptData.toString()
                    navController.navigate(AppRoutes.editReceiptRoute(receiptUri))
                },
                viewModel = viewModel
            )
        }

        composable(
            route = "${AppRoutes.EDIT_RECEIPT}/{receiptUri}",
            arguments = listOf(
                navArgument("receiptUri") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val receiptUriString = backStackEntry.arguments?.getString("receiptUri")
            val receiptUri = remember(receiptUriString) {
                receiptUriString?.let { Uri.parse(it) }
            }

            val viewModel = remember {
                EditReceiptViewModel(ocrUtils)
            }

            receiptUri?.let {
                LaunchedEffect(it) {
                    viewModel.processReceiptImage(it)
                }
            }

            EditReceiptScreen(
                receiptItems = viewModel.receiptItems,
                restaurantName = viewModel.restaurantName.value,
                receiptDate = viewModel.receiptDate.value,
                receiptTotal = viewModel.calculateTotal(),
                onUpdateRestaurantInfo = viewModel::updateRestaurantInfo,
                onItemUpdate = viewModel::updateItem,
                onItemDelete = viewModel::deleteItem,
                onAddItem = viewModel::addNewItem,
                onContinue = {
                    val receipt = Receipt(
                        restaurantName = viewModel.restaurantName.value,
                        items = viewModel.receiptItems,
                        participants = emptyList(),
                        totalAmount = viewModel.calculateTotal()
                    )
                    assignItemsViewModel.initializeWithReceipt(receipt)
                    navController.navigate(AppRoutes.ASSIGN_ITEMS)
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(AppRoutes.ASSIGN_ITEMS) {
            var showAddParticipantDialog by remember { mutableStateOf(false) }

            AssignItemsScreen(
                restaurantName = assignItemsViewModel.restaurantName.value,
                receiptTotal = assignItemsViewModel.receiptTotal.value,
                receiptItems = assignItemsViewModel.receiptItems,
                participants = assignItemsViewModel.participants,
                onAssignParticipant = assignItemsViewModel::assignParticipant,
                onRemoveParticipant = assignItemsViewModel::removeParticipant,
                onAddParticipant = { showAddParticipantDialog = true },
                onContinue = {
                    navController.navigate(AppRoutes.BILL_SUMMARY)
                },
                onBackClick = { navController.popBackStack() }
            )

            if (showAddParticipantDialog) {
                AddParticipantDialog(
                    onAddParticipant = { name, email, phone ->
                        assignItemsViewModel.addParticipant(name, email, phone)
                    },
                    onDismiss = { showAddParticipantDialog = false }
                )
            }
        }

        composable(AppRoutes.BILL_SUMMARY) {
        }
    }
}