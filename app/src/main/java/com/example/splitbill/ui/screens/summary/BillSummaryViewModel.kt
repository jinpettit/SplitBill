package com.example.splitbill.ui.screens.summary

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.splitbill.data.models.Receipt
import java.util.Date

class BillSummaryViewModel : ViewModel() {

    private val _restaurantName = mutableStateOf("")
    val restaurantName = _restaurantName

    private val _date = mutableStateOf<Date?>(null)
    val date = _date

    private val _subtotal = mutableStateOf<Double?>(null)
    val subtotal = _subtotal

    private val _tip = mutableStateOf<Double?>(null)
    val tip = _tip

    private val _tax = mutableStateOf<Double?>(null)
    val tax = _tax

    private val _total = mutableStateOf<Double?>(null)
    val total = _total

    private val _participantSummaries = mutableStateOf<List<ParticipantSummary>>(emptyList())
    val participantSummaries = _participantSummaries

    private val _receipt = mutableStateOf<Receipt?>(null)
    val receipt = _receipt

    fun initializeWithReceipt(receipt: Receipt) {
        _receipt.value = receipt
        _restaurantName.value = receipt.restaurantName
        _date.value = receipt.date
        _subtotal.value = receipt.subtotal
        _tip.value = receipt.tip
        _tax.value = receipt.tax
        _total.value = receipt.totalAmount

        calculateParticipantAmounts(receipt)
    }

    private fun calculateParticipantAmounts(receipt: Receipt) {
        val participants = receipt.participants
        val items = receipt.items

        val participantAmounts = mutableMapOf<String, Double>()

        participants.forEach { participant ->
            participantAmounts[participant.id] = 0.0
        }

        items.forEach { item ->
            val assignedParticipants = item.assignedParticipantIds
            if (assignedParticipants.isNotEmpty()) {
                val pricePerPerson = (item.price * item.quantity) / assignedParticipants.size
                assignedParticipants.forEach { participantId ->
                    participantAmounts[participantId] =
                        (participantAmounts[participantId] ?: 0.0) + pricePerPerson
                }
            }
        }

        // Handle unassigned items by splitting equally
        val unassignedItems = items.filter { it.assignedParticipantIds.isEmpty() }
        val unassignedTotal = unassignedItems.sumOf { it.price * it.quantity }
        if (unassignedTotal > 0 && participants.isNotEmpty()) {
            val equalShare = unassignedTotal / participants.size
            participants.forEach { participant ->
                participantAmounts[participant.id] =
                    (participantAmounts[participant.id] ?: 0.0) + equalShare
            }
        }

        // Apply tax to all participants
        val taxValue = _tax.value ?: 0.0
        if (taxValue > 0 && participants.isNotEmpty()) {
            val equalTaxShare = taxValue / participants.size
            participants.forEach { participant ->
                val amount = participantAmounts[participant.id] ?: 0.0
                participantAmounts[participant.id] = amount + equalTaxShare
            }
        }

        // Apply tip to all participants
        val tipValue = _tip.value ?: 0.0
        if (tipValue > 0 && participants.isNotEmpty()) {
            val equalTipShare = tipValue / participants.size
            participants.forEach { participant ->
                val amount = participantAmounts[participant.id] ?: 0.0
                participantAmounts[participant.id] = amount + equalTipShare
            }
        }

        // Create summary objects
        _participantSummaries.value = participants.map { participant ->
            ParticipantSummary(
                id = participant.id,
                name = participant.name,
                amount = participantAmounts[participant.id] ?: 0.0
            )
        }
    }

    fun generateTextSummary(): String {
        val receipt = _receipt.value ?: return ""
        val subtotalValue = _subtotal.value ?: 0.0
        val tipValue = _tip.value ?: 0.0
        val taxValue = _tax.value ?: 0.0
        val totalValue = _total.value ?: 0.0

        val sb = StringBuilder()
        sb.appendLine("${receipt.restaurantName}")
        sb.appendLine("------------------------")
        sb.appendLine("Subtotal: $${String.format("%.2f", subtotalValue)}")
        sb.appendLine("Tax: $${String.format("%.2f", taxValue)}")
        sb.appendLine("Tip: $${String.format("%.2f", tipValue)}")
        sb.appendLine("Total: $${String.format("%.2f", totalValue)}")
        sb.appendLine("------------------------")
        sb.appendLine("Split between ${participantSummaries.value.size} people:")

        participantSummaries.value.forEach { participantSummary ->
            sb.appendLine("${participantSummary.name}: $${String.format("%.2f", participantSummary.amount)}")
        }

        return sb.toString()
    }
}