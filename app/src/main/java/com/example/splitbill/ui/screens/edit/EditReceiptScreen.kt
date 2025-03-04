package com.example.splitbill.ui.screens.edit

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.splitbill.data.models.ReceiptItem
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditReceiptScreen(
    receiptItems: List<ReceiptItem>,
    restaurantName: String,
    receiptDate: Date?,
    receiptTotal: Double,
    onUpdateRestaurantInfo: (String, Date?) -> Unit,
    onItemUpdate: (ReceiptItem) -> Unit,
    onItemDelete: (ReceiptItem) -> Unit,
    onAddItem: () -> Unit,
    onContinue: () -> Unit,
    onBackClick: () -> Unit
) {
    val dateFormatter = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
    val formattedDate = receiptDate?.let { dateFormatter.format(it) } ?: "Date Unknown"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Receipt Items") },
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
            var isEditingRestaurantInfo by remember { mutableStateOf(false) }
            var editedRestaurantName by remember { mutableStateOf(restaurantName) }

            LaunchedEffect(restaurantName) {
                editedRestaurantName = restaurantName
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    if (isEditingRestaurantInfo) {
                        OutlinedTextField(
                            value = editedRestaurantName,
                            onValueChange = { editedRestaurantName = it },
                            label = { Text("Restaurant Name") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = {
                                    editedRestaurantName = restaurantName
                                    isEditingRestaurantInfo = false
                                }
                            ) {
                                Text("Cancel")
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Button(
                                onClick = {
                                    onUpdateRestaurantInfo(editedRestaurantName, receiptDate)
                                    isEditingRestaurantInfo = false
                                }
                            ) {
                                Text("Save")
                            }
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Restaurant Name",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )

                            IconButton(
                                onClick = { isEditingRestaurantInfo = true },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit",
                                    tint = Color.Gray
                                )
                            }
                        }

                        Text(
                            text = restaurantName,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "$formattedDate • $${String.format("%.2f", receiptTotal)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                }
            }

            HorizontalDivider()

            LazyColumn(
                modifier = Modifier.weight(1f),
            ) {
                items(receiptItems) { item ->
                    ReceiptItemRow(
                        item = item,
                        onUpdate = onItemUpdate,
                        onDelete = onItemDelete
                    )
                    HorizontalDivider()
                }
            }

            Button(
                onClick = onAddItem,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Item"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add New Item")
            }
        }
    }
}

@Composable
fun ReceiptItemRow(
    item: ReceiptItem,
    onUpdate: (ReceiptItem) -> Unit,
    onDelete: (ReceiptItem) -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }
    var editedName by remember { mutableStateOf(item.name) }
    var editedPrice by remember { mutableStateOf(item.price.toString()) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        if (isEditing) {
            OutlinedTextField(
                value = editedName,
                onValueChange = { editedName = it },
                label = { Text("Item Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = editedPrice,
                onValueChange = {
                    if (it.isEmpty() || it.matches(Regex("""^\d*\.?\d*$"""))) {
                        editedPrice = it
                    }
                },
                label = { Text("Price") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = { isEditing = false }) {
                    Text("Cancel")
                }
                TextButton(
                    onClick = {
                        val price = editedPrice.toDoubleOrNull() ?: 0.0
                        onUpdate(item.copy(name = editedName, price = price))
                        isEditing = false
                    }
                ) {
                    Text("Save")
                }
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = item.name,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                        IconButton(onClick = { isEditing = true }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit",
                                tint = Color.Gray
                            )
                        }
                    }
                    Text(
                        text = "$${String.format("%.2f", item.price)}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    if (item.quantity > 1) {
                        onUpdate(item.copy(quantity = item.quantity - 1))
                    }
                },
                modifier = Modifier.size(32.dp)
            ) {
                Surface(
                    modifier = Modifier.size(24.dp),
                    shape = MaterialTheme.shapes.small,
                    color = Color.LightGray
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("−")
                    }
                }
            }

            Text(
                text = "${item.quantity}",
                modifier = Modifier.padding(horizontal = 12.dp),
                style = MaterialTheme.typography.bodyLarge
            )

            IconButton(
                onClick = { onUpdate(item.copy(quantity = item.quantity + 1)) },
                modifier = Modifier.size(32.dp)
            ) {
                Surface(
                    modifier = Modifier.size(24.dp),
                    shape = MaterialTheme.shapes.small,
                    color = Color.LightGray
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("+")
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            IconButton(onClick = { onDelete(item) }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color.Gray
                )
            }
        }
    }
}