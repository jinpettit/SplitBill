package com.example.splitbill.ui.screens.assign

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.splitbill.data.models.Participant
import com.example.splitbill.data.models.ReceiptItem

@Composable
fun AssignItemRow(
    item: ReceiptItem,
    participants: List<Participant>,
    assignedParticipantIds: List<String>,
    onAssignParticipant: (Participant) -> Unit,
    onRemoveParticipant: (String) -> Unit
) {
    val assignedParticipants = participants.filter { it.id in assignedParticipantIds }
    var showParticipantPicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = item.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = "$${String.format("%.2f", item.price * item.quantity)}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (assignedParticipants.isEmpty()) {
            OutlinedButton(
                onClick = { showParticipantPicker = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.Gray
                )
            ) {
                Text("Assign to someone")
            }
        } else {
            Flow(
                modifier = Modifier.fillMaxWidth(),
                mainAxisSpacing = 8.dp,
                crossAxisSpacing = 8.dp
            ) {
                assignedParticipants.forEach { participant ->
                    AssignedParticipantChip(
                        name = participant.name,
                        onRemove = { onRemoveParticipant(participant.id) }
                    )
                }

                AssignMoreButton(
                    onClick = { showParticipantPicker = true }
                )
            }
        }

        if (showParticipantPicker) {
            ParticipantPickerDialog(
                participants = participants,
                alreadyAssignedIds = assignedParticipantIds,
                onParticipantSelected = {
                    onAssignParticipant(it)
                    showParticipantPicker = false
                },
                onDismiss = { showParticipantPicker = false }
            )
        }
    }
}

@Composable
fun AssignedParticipantChip(
    name: String,
    onRemove: () -> Unit
) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.bodyMedium
            )

            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Remove",
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun AssignMoreButton(onClick: () -> Unit) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "+ Add more",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
    }
}