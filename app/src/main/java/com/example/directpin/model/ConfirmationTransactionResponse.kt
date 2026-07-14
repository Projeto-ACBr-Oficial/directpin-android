package com.example.directpin.model

data class ConfirmationTransactionResponse(
    val type: String,
    val result: Boolean,
    val message: String,
    val finalResult: FinalResult? = null,
)

