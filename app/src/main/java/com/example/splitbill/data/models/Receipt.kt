package com.example.splitbill.data.models

import android.net.Uri
import java.util.UUID

data class Receipt(
    val id: String = UUID.randomUUID().toString(),
    val imageUri: Uri? = null,
    val items: List<ReceiptItem> = emptyList(),
    val participants: List<Participant> = emptyList(),
    val date: Long = System.currentTimeMillis(),
    val restaurantName: String? = null,
    val totalAmount: Double = 0.0
)