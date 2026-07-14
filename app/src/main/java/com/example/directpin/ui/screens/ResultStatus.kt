package com.example.directpin.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.directpin.model.FinalResult

/** Lógica compartilhada de exibição do resultado de uma operação (Transação, Estorno, Confirmação, Desfazimento) */

fun isFinalResultApproved(finalResult: FinalResult?, result: Boolean): Boolean {
    return finalResult == FinalResult.APPROVED || (finalResult == null && result)
}

fun finalResultLabel(finalResult: FinalResult?, result: Boolean): String {
    return when (finalResult) {
        FinalResult.APPROVED -> "Transação aprovada"
        FinalResult.REPROVED_HOST -> "Reprovada pelo host"
        FinalResult.REPROVED_CARD -> "Reprovada pelo cartão"
        FinalResult.CANCELED -> "Transação cancelada"
        FinalResult.ABORTED -> "Transação abortada"
        FinalResult.CONNECTION_ERROR -> "Erro de conexão"
        FinalResult.CARD_READ_ERROR -> "Erro na leitura do cartão"
        null -> if (result) "Transação aprovada" else "Transação reprovada"
    }
}

@Composable
fun resultStatusIcon(isApproved: Boolean): ImageVector {
    return if (isApproved) Icons.Filled.CheckCircle else Icons.Filled.Error
}

@Composable
fun resultStatusColor(isApproved: Boolean): Color {
    return if (isApproved) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
}
