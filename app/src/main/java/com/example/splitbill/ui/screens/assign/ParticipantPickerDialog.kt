package com.example.splitbill.ui.screens.assign

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.splitbill.data.models.Participant

@Composable
fun ParticipantPickerDialog(
    participants: List<Participant>,
    alreadyAssignedIds: List<String>,
    onParticipantSelected: (Participant) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Assign to") },
        text = {
            LazyColumn {
                items(participants) { participant ->
                    val isAssigned = participant.id in alreadyAssignedIds
                    ParticipantPickerItem(
                        participant = participant,
                        isAssigned = isAssigned,
                        onClick = {
                            if (!isAssigned) {
                                onParticipantSelected(participant)
                            }
                        }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun ParticipantPickerItem(
    participant: Participant,
    isAssigned: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isAssigned, onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape),
            color = if (isAssigned)
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
            else
                MaterialTheme.colorScheme.primaryContainer
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = participant.name.first().uppercase(),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = participant.name,
            style = MaterialTheme.typography.bodyLarge,
            color = if (isAssigned) Color.Gray else Color.Unspecified
        )

        if (isAssigned) {
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Already Assigned",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}