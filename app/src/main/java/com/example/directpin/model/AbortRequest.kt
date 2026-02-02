package com.example.directpin.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AbortRequest(
    val type: String,
    val entityIdentifier: String? = null
) : Parcelable

