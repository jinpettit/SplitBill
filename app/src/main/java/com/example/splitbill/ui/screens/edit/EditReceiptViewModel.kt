package com.example.splitbill.ui.screens.edit

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    private var processedImageUri: Uri? = null

    private val _subtotal = mutableStateOf<Double?>(null)
    val subtotal = _subtotal
    private val _tip = mutableStateOf<Double?>(null)
    val tip = _tip
    private val _tax = mutableStateOf<Double?>(null)
    val tax = _tax
    private val _totalAmount = mutableStateOf<Double?>(null)
    val totalAmount = _totalAmount

    @RequiresApi(Build.VERSION_CODES.O)
    fun processReceiptImage(uri: Uri) {
        if (uri == processedImageUri && receiptItems.isNotEmpty()) return

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
                processedImageUri = uri
                _subtotal.value = receipt.subtotal
                _tip.value = receipt.tip
                _tax.value = receipt.tax
                _totalAmount.value = receipt.totalAmount
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

}