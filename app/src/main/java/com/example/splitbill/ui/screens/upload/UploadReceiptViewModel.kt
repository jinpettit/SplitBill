package com.example.splitbill.ui.screens.upload

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.splitbill.utils.CameraUtils

class UploadReceiptViewModel(
    private val cameraUtils: CameraUtils
) : ViewModel() {

    fun createImageUri(): Uri? = cameraUtils.createImageUri()
}