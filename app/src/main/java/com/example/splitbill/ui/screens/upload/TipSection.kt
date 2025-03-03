package com.example.splitbill.ui.screens.upload

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TipsSection() {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Tips for best results:",
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        TipItem(text = "Ensure good lighting")

        Spacer(modifier = Modifier.height(12.dp))

        TipItem(text = "Keep receipt flat and wrinkle-free")

        Spacer(modifier = Modifier.height(12.dp))

        TipItem(text = "Capture the entire receipt in frame")
    }
}

@Composable
fun TipItem(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = Color.DarkGray,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = text,
            color = Color.DarkGray,
            fontSize = 14.sp
        )
    }
}