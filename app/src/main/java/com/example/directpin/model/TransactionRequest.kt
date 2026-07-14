package com.example.directpin.model

data class TransactionRequest(
    val type: String,
    val amount: Long,
    val typeTransaction: String,
    val creditType: String,
    val installment: Int,
    val isTyped: Boolean,
    val isPreAuth: Boolean,
    val autoConfirm: Boolean,
    val interestType: String,
    val printReceipt: Boolean
)
