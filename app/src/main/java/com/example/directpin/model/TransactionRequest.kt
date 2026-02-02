package com.example.directpin.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TransactionRequest(
    val type: String,
    val amount: Long,
    val typeTransaction: String,
    val creditType: String,
    val installment: Int,
    val isTyped: Boolean,
    val isPreAuth: Boolean,
    val interestType: String,
    val printReceipt: Boolean,
    val entityIdentifier: String? = null
) : Parcelable
