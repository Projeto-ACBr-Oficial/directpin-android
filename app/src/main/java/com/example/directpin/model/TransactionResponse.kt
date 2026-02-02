package com.example.directpin.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TransactionResponse(
    val type: String,
    val result: Boolean,
    val message: String,
    val amount: Long = 0,
    val nsu: String = "",
    val nsuAcquirer: String = "",
    val panMasked: String = "",
    val date: Long = 0,
    val typeCard: String = "",
    val finalResult: String = "",
    val codeResult: Int = 0,
    val receiptContent: String = "",
    val serialNumber: String = "",
    val brand: String = "",
    val authCode: String = ""
) : Parcelable
