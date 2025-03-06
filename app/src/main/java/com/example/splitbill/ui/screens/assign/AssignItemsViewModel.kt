package com.example.splitbill.ui.screens.assign

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.splitbill.data.models.Participant
import com.example.splitbill.data.models.Receipt
import com.example.splitbill.data.models.ReceiptItem

class AssignItemsViewModel : ViewModel() {

    private val _participants = mutableStateListOf<Participant>()
    val participants: List<Participant> = _participants

    private val _receiptItems = mutableStateListOf<ReceiptItem>()
    val receiptItems: List<ReceiptItem> = _receiptItems

    private val _restaurantName = mutableStateOf("")
    val restaurantName = _restaurantName

    private val _receiptTotal = mutableStateOf(0.0)
    val receiptTotal = _receiptTotal

    private var receipt: Receipt? = null

    fun initializeWithReceipt(receipt: Receipt) {
        this.receipt = receipt
        _receiptItems.clear()
        _receiptItems.addAll(receipt.items)
        _participants.clear()
        _participants.addAll(receipt.participants)
        _restaurantName.value = receipt.restaurantName
        _receiptTotal.value = receipt.totalAmount
    }

    fun assignParticipant(item: ReceiptItem, participant: Participant) {
        val index = _receiptItems.indexOfFirst { it.id == item.id }
        if (index != -1) {
            val currentItem = _receiptItems[index]
            val updatedAssignments = currentItem.assignedParticipantIds.toMutableList()

            if (!updatedAssignments.contains(participant.id)) {
                updatedAssignments.add(participant.id)

                val updatedItem = currentItem.copy(
                    assignedParticipantIds = updatedAssignments
                )

                _receiptItems[index] = updatedItem
            }
        }
    }

    fun removeParticipant(item: ReceiptItem, participantId: String) {
        val index = _receiptItems.indexOfFirst { it.id == item.id }
        if (index != -1) {
            val currentItem = _receiptItems[index]
            val updatedAssignments = currentItem.assignedParticipantIds.toMutableList()

            if (updatedAssignments.contains(participantId)) {
                updatedAssignments.remove(participantId)

                val updatedItem = currentItem.copy(
                    assignedParticipantIds = updatedAssignments
                )

                _receiptItems[index] = updatedItem
            }
        }
    }

    fun addParticipant(name: String, email: String? = null, phoneNumber: String? = null) {
        val newParticipant = Participant(
            name = name,
            email = email,
            phoneNumber = phoneNumber
        )
        _participants.add(newParticipant)
    }

    fun createUpdatedReceipt(): Receipt {
        return receipt?.copy(
            items = receiptItems.toList(),
            participants = participants.toList()
        ) ?: Receipt(
            restaurantName = restaurantName.value,
            items = receiptItems.toList(),
            participants = participants.toList(),
            totalAmount = receiptTotal.value
        )
    }
}