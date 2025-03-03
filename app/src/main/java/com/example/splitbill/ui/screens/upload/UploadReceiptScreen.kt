package com.example.splitbill.ui.screens.upload

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.splitbill.utils.PermissionUtils
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun UploadReceiptScreen(
    onBackClick: () -> Unit,
    onCloseClick: () -> Unit,
    onReceiptCaptured: (Uri) -> Unit,
    viewModel: UploadReceiptViewModel
) {
    val permissionState = rememberMultiplePermissionsState(permissions = PermissionUtils.REQUIRED_PERMISSIONS.toList())
    var currentUri by remember { mutableStateOf<Uri?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && currentUri != null) {
            onReceiptCaptured(currentUri!!)
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { onReceiptCaptured(it) }
    }

    fun launchCamera() {
        if (permissionState.allPermissionsGranted) {
            viewModel.createImageUri()?.let { uri ->
                currentUri = uri
                cameraLauncher.launch(uri)
            }
        }
        else {
            permissionState.launchMultiplePermissionRequest()
        }
    }

    fun launchGallery() {
        if (permissionState.allPermissionsGranted) {
            galleryLauncher.launch("image/*")
        } else {
            permissionState.launchMultiplePermissionRequest()
        }
    }

    LaunchedEffect(permissionState.allPermissionsGranted) {
        if (!permissionState.allPermissionsGranted) {
            permissionState.launchMultiplePermissionRequest()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Upload Receipt") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onCloseClick) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.TopCenter
        ) {
            ReceiptUploadContent(
                onTakePhoto = { launchCamera() },
                onGallerySelect = { launchGallery() }
            )
        }
    }
}