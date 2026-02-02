package com.example.directpin.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CancelTransactionRequest(
    val type: String,
    val nsu: String,
    val entityIdentifier: String? = null
) : Parcelable

