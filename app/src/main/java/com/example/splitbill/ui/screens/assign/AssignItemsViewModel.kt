package com.example.splitbill.ui.screens.assign

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.splitbill.data.models.Participant
import com.example.splitbill.data.models.Receipt
import com.example.splitbill.data.models.ReceiptItem
import java.util.UUID

class AssignItemsViewModel : ViewModel() {

    private val _participants = mutableStateListOf<Participant>()
    val participants: List<Participant> = _participants

    private val _receiptItems = mutableStateListOf<ReceiptItem>()
    val receiptItems: List<ReceiptItem> = _receiptItems

    private val _restaurantName = mutableStateOf("")
    val restaurantName = _restaurantName

    private val _subtotal = mutableStateOf<Double?>(null)
    val subtotal = _subtotal

    private val _tip = mutableStateOf<Double?>(null)
    val tip = _tip

    private val _tax = mutableStateOf<Double?>(null)
    val tax = _tax

    private val _receiptTotal = mutableStateOf<Double?>(null)
    val receiptTotal = _receiptTotal

    private var receipt: Receipt? = null

    fun initializeWithReceipt(receipt: Receipt) {
        this.receipt = receipt
        _receiptItems.clear()

        receipt.items.forEach { originalItem ->
            if (originalItem.quantity > 1) {
                for (i in 1..originalItem.quantity) {
                    _receiptItems.add(
                        originalItem.copy(
                            id = UUID.randomUUID().toString(),
                            quantity = 1,
                            assignedParticipantIds = emptyList()
                        )
                    )
                }
            } else {
                _receiptItems.add(originalItem)
            }
        }

        _participants.clear()
        _participants.addAll(receipt.participants)
        _restaurantName.value = receipt.restaurantName
        _receiptTotal.value = receipt.totalAmount
        _subtotal.value = receipt.subtotal
        _tip.value = receipt.tip
        _tax.value = receipt.tax
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
            participants = participants.toList(),
            subtotal = subtotal.value,
            tip = tip.value,
            tax = tax.value,
            totalAmount = receiptTotal.value
        ) ?: Receipt(
            restaurantName = restaurantName.value,
            items = receiptItems.toList(),
            participants = participants.toList(),
            subtotal = subtotal.value,
            tip = tip.value,
            tax = tax.value,
            totalAmount = receiptTotal.value
        )
    }
}