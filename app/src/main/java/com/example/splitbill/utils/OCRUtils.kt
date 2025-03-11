package com.example.splitbill.utils

import android.content.ContentValues.TAG
import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.splitbill.R
import com.example.splitbill.data.models.Receipt
import com.example.splitbill.data.models.ReceiptItem
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class OCRUtils(private val context: Context) {

    private val cloudVisionApi: CloudVisionApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://vision.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CloudVisionApi::class.java)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun processReceiptImage(imageUri: Uri): Receipt = suspendCancellableCoroutine { continuation ->
        try {
            val imageBytes = context.contentResolver.openInputStream(imageUri)?.use { stream ->
                stream.readBytes()
            } ?: throw IllegalStateException("Failed to read image file")

            val base64Image = Base64.getEncoder().encodeToString(imageBytes)

            val request = VisionRequest(
                requests = listOf(
                    Request(
                        image = Image(content = base64Image),
                        features = listOf(Feature())
                    )
                )
            )

            cloudVisionApi.annotateImage(apiKey = context.getString(R.string.API_KEY), request = request).enqueue(
                object : Callback<VisionResponse> {
                    override fun onResponse(
                        call: Call<VisionResponse>,
                        response: Response<VisionResponse>
                    ) {
                        if (response.isSuccessful) {
                            val textAnnotations = response.body()?.responses?.firstOrNull()?.textAnnotations
                            Log.d(TAG, "API Response successful: ${response.body()}")

                            if (textAnnotations != null) {
                                Log.d(TAG, "Raw text annotations: ${textAnnotations.first().description}")

                                val lines = textAnnotations.first().description.split("\n")
                                Log.d(TAG, "Split lines: $lines")

                                val parsedReceipt = parseReceipt(lines, imageUri)
                                Log.d(TAG, "Parsed receipt: $parsedReceipt")
                                continuation.resume(parsedReceipt)
                            } else {
                                Log.e(TAG, "No text annotations found in response")
                                continuation.resumeWithException(Exception("No text found in the image"))
                            }
                        } else {
                            Log.e(TAG, "API request failed: ${response.errorBody()?.string()}")
                            continuation.resumeWithException(Exception("API request failed: ${response.errorBody()?.string()}"))
                        }
                    }

                    override fun onFailure(call: Call<VisionResponse>, t: Throwable) {
                        Log.e(TAG, "API call failed", t)
                        continuation.resumeWithException(t)
                    }

                }
            )
        } catch (e: Exception) {
            continuation.resumeWithException(e)
        }
    }

    private fun parseReceipt(lines: List<String>, imageUri: Uri): Receipt {
        Log.d(TAG, "Starting receipt parsing with ${lines.size} lines")

        val text = lines.joinToString("\n")
        val restaurantName = extractRestaurantName(lines)
        Log.d(TAG, "Extracted restaurant name: $restaurantName")

        val date = extractReceiptDate(text)
        Log.d(TAG, "Extracted date: $date")

        val items = parseReceiptItems(text)
        Log.d(TAG, "Extracted items: $items")

        val totalAmount = extractTotalAmount(text)
        Log.d(TAG, "Extracted total amount: $totalAmount")

        val subTotal = extractSubtotal(text)
        val tip = extractTip(text)
        val tax = extractTax(text)

        return Receipt(
            imageUri = imageUri,
            restaurantName = restaurantName,
            date = date,
            items = items,
            totalAmount = totalAmount,
            subtotal = subTotal,
            tip = tip,
            tax = tax
        )
    }

    private fun extractRestaurantName(lines: List<String>): String {
        Log.d(TAG, "Looking for restaurant name in lines: $lines")

        val addressPattern = Regex(""".*\d+.*(?:st|street|rd|road|ave|avenue|blvd|boulevard|drive).*""", RegexOption.IGNORE_CASE)

        for ((index, line) in lines.withIndex()) {
            if (index > 0 && addressPattern.matches(line.trim())) {
                val restaurantName = lines[index - 1].trim()
                Log.d(TAG, "Found address: $line")
                Log.d(TAG, "Restaurant name is: $restaurantName")
                return restaurantName
            }
        }

        for (line in lines.take(5)) {
            val trimmedLine = line.trim()
            if (trimmedLine.isNotEmpty() &&
                !trimmedLine.matches(Regex(".*(?:TEL|FAX|TABLE|#|\\d{3}-\\d{3}-\\d{4}).*", RegexOption.IGNORE_CASE)) &&
                !trimmedLine.matches(Regex("\\d+[/\\-]\\d+[/\\-]\\d+.*")) && // Date
                !trimmedLine.matches(Regex(".*\\d{2}:\\d{2}.*"))) { // Time
                Log.d(TAG, "Using fallback restaurant name: $trimmedLine")
                return trimmedLine
            }
        }

        return "Unknown Restaurant"
    }

    private fun extractReceiptDate(text: String): Date? {
        val datePattern = Regex("""(\d{1,2}/\d{1,2}/\d{2,4})\s+\d{1,2}:\d{2}\s*(?:AM|PM)""", RegexOption.IGNORE_CASE)
        val match = datePattern.find(text)

        if (match != null) {
            val dateStr = match.groupValues[1]
            val formatter = SimpleDateFormat("MM/dd/yy", Locale.US)
            try {
                return formatter.parse(dateStr)
            } catch (e: ParseException) {
                Log.e(TAG, "Failed to parse date: $dateStr", e)
            }
        }

        return Calendar.getInstance().time
    }}

private fun parseReceiptItems(text: String): List<ReceiptItem> {
    val items = mutableListOf<ReceiptItem>()
    Log.d(TAG, "Starting to parse items from text: $text")

    val lines = text.split("\n")

    var startIndex = -1
    val datePattern = Regex("""(\d{1,2}/\d{1,2}/\d{2})""")

    for ((index, line) in lines.withIndex()) {
        if (datePattern.find(line) != null) {
            startIndex = index + 1
            Log.d(TAG, "Found date at line $index, starting item search from line ${index + 1}")
            break
        }
    }

    if (startIndex == -1) {
        Log.d(TAG, "No date found, starting from beginning")
        startIndex = 0
    }

    var i = startIndex

    while (i < lines.size - 1) {
        val currentLine = lines[i].trim()
        val nextLine = lines[i + 1].trim()
        Log.d(TAG, "Processing line: $currentLine")
        Log.d(TAG, "Next line: $nextLine")

        if (currentLine.contains("subtotal", ignoreCase = true)) {
            Log.d(TAG, "Found subtotal, stopping item parsing")
            break
        }

        val itemPattern = Regex("""^(\d+)\s+(.+)$""")
        val pricePattern = Regex("""\$?\s*(\d+\.\d{2})""")

        val itemMatch = itemPattern.find(currentLine)
        val priceMatch = pricePattern.find(nextLine)

        if (itemMatch != null && priceMatch != null) {
            val quantity = itemMatch.groupValues[1].toInt()
            val name = itemMatch.groupValues[2].trim()
            val totalPrice = priceMatch.groupValues[1].toDouble()
            val individualPrice = totalPrice / quantity

            Log.d(
                TAG,
                "Found item: $name, quantity: $quantity, total price: $totalPrice, individual price: $individualPrice"
            )

            items.add(
                ReceiptItem(
                    name = name,
                    quantity = quantity,
                    price = individualPrice
                )
            )

            i += 2
        } else {
            i++
        }
    }

    Log.d(TAG, "Finished parsing items. Found ${items.size} items: $items")
    return items
}

    private fun extractTotalAmount(text: String): Double? {
        val totalPattern = Regex("""(?<!SUB|SUB-)(?:TOTAL|AMOUNT|GRAND\s+TOTAL|BALANCE|DUE)\s*\$?\s*(\d+\.\d{2})""", RegexOption.IGNORE_CASE)

        val matches = totalPattern.findAll(text).toList()
        return if (matches.isNotEmpty()) {
            matches.last().groupValues[1].toDoubleOrNull()
        } else {
            null
        }
    }

    private fun extractSubtotal(text: String): Double? {
        val subtotalPattern = Regex("""(?:SUBTOTAL|SUB-TOTAL)\s*\$?\s*(\d+\.\d{2})""", RegexOption.IGNORE_CASE)
        return subtotalPattern.find(text)?.groupValues?.get(1)?.toDoubleOrNull()
    }

    private fun extractTip(text: String): Double? {
        val tipPattern = Regex("""(?:TIP|GRATUITY)\s*\$?\s*(\d+\.\d{2})""", RegexOption.IGNORE_CASE)
        return tipPattern.find(text)?.groupValues?.get(1)?.toDoubleOrNull()
    }

    private fun extractTax(text: String): Double? {
        val taxPattern = Regex("""(?:TAX|SALES TAX)\s*\$?\s*(\d+\.\d{2})""", RegexOption.IGNORE_CASE)
        return taxPattern.find(text)?.groupValues?.get(1)?.toDoubleOrNull()
    }