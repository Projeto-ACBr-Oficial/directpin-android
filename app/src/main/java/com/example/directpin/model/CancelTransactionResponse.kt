package com.example.directpin.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CancelTransactionResponse(
    val type: String,
    val result: Boolean,
    val message: String,
    val receiptContent: String = ""
) : Parcelable

