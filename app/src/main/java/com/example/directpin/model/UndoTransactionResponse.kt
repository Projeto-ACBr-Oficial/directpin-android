package com.example.directpin.model

data class UndoTransactionResponse(
    val type: String,
    val result: Boolean,
    val message: String,
    val finalResult: FinalResult? = null,
)

