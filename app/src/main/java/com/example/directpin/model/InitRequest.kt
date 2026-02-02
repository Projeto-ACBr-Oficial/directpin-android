package com.example.directpin.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class InitRequest(
    val type: String,
    val token: String
) : Parcelable
