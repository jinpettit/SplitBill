package com.example.splitbill.ui.screens.assign

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.splitbill.data.models.Participant
import com.example.splitbill.data.models.ReceiptItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignItemsScreen(
    restaurantName: String,
    receiptTotal: Double,
    receiptItems: List<ReceiptItem>,
    participants: List<Participant>,
    onAssignParticipant: (ReceiptItem, Participant) -> Unit,
    onRemoveParticipant: (ReceiptItem, String) -> Unit,
    onAddParticipant: () -> Unit,
    onContinue: () -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Assign Items") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onContinue) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Continue"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = restaurantName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Total: $${String.format("%.2f", receiptTotal)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            ParticipantsList(
                participants = participants,
                onAddParticipant = onAddParticipant
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(receiptItems) { item ->
                    AssignItemRow(
                        item = item,
                        participants = participants,
                        assignedParticipantIds = item.assignedParticipantIds,
                        onAssignParticipant = { participant -> onAssignParticipant(item, participant) },
                        onRemoveParticipant = { participantId -> onRemoveParticipant(item, participantId) }
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}