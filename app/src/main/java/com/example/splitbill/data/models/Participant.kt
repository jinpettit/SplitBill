package com.example.splitbill.data.models

import java.util.UUID

data class Participant(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val email: String? = null,
    val phoneNumber: String? = null
)