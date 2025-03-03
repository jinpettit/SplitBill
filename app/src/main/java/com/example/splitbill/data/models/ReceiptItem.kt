package com.example.splitbill.data.models

import java.util.UUID

data class ReceiptItem(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val price: Double,
    val quantity: Int = 1,
    val assignedParticipantIds: List<String> = emptyList()
)