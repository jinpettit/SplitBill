package com.example.splitbill.ui.screens.edit

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.splitbill.data.models.Receipt
import com.example.splitbill.data.models.ReceiptItem
import com.example.splitbill.utils.OCRUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.*

class EditReceiptViewModel(
    private val ocrUtils: OCRUtils
) : ViewModel() {

    private val _receiptItems = mutableStateListOf<ReceiptItem>()
    val receiptItems: List<ReceiptItem> = _receiptItems

    private val _restaurantName = mutableStateOf("")
    val restaurantName = _restaurantName

    private val _receiptDate = mutableStateOf<Date?>(null)
    val receiptDate = _receiptDate

    private val _isLoading = MutableStateFlow(false)

    private val _error = MutableStateFlow<String?>(null)

    private var imageUri: Uri? = null

    @RequiresApi(Build.VERSION_CODES.O)
    fun processReceiptImage(uri: Uri) {
        imageUri = uri
        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                val receipt = ocrUtils.processReceiptImage(uri)
                _receiptItems.clear()
                _receiptItems.addAll(receipt.items)
                _restaurantName.value = receipt.restaurantName
                _receiptDate.value = receipt.date
                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = "Failed to process receipt: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    fun updateRestaurantInfo(name: String, date: Date?) {
        _restaurantName.value = name
        _receiptDate.value = date
    }

    fun updateItem(item: ReceiptItem) {
        val index = _receiptItems.indexOfFirst { it.id == item.id }
        if (index != -1) {
            _receiptItems[index] = item
        }
    }

    fun deleteItem(item: ReceiptItem) {
        _receiptItems.removeIf { it.id == item.id }
    }

    fun addNewItem() {
        _receiptItems.add(
            ReceiptItem(
                name = "New Item",
                price = 0.0,
                quantity = 1
            )
        )
    }

    fun calculateTotal(): Double {
        return receiptItems.sumOf { it.price * it.quantity }
    }

    fun createReceipt(): Receipt {
        return Receipt(
            imageUri = imageUri,
            restaurantName = restaurantName.value,
            date = receiptDate.value,
            items = receiptItems.toList(),
            totalAmount = calculateTotal()
        )
    }
}