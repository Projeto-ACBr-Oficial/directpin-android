package com.example.directpin.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.directpin.model.CodeResult
import com.example.directpin.model.TransactionResponse
import com.example.directpin.ui.common.TransactionHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionResultScreen(
    response: TransactionResponse,
    onNewSale: () -> Unit
) {
    val isApproved = isFinalResultApproved(response.finalResult, response.result)
    val statusColor = resultStatusColor(isApproved)
    val codeResult = CodeResult.fromCode(response.codeResult)

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Resultado da Transação") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = resultStatusIcon(isApproved),
                contentDescription = null,
                tint = statusColor,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                finalResultLabel(response.finalResult, response.result),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                color = statusColor
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                response.message,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                textAlign = TextAlign.Center
            )
            if (!isApproved && codeResult != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Detalhe técnico: ${codeResult.message}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    InfoRow("Valor", TransactionHelper.formatAmount(response.amount))
                    if (response.panMasked.isNotBlank()) {
                        InfoRow("Cartão", response.panMasked)
                    }
                    if (response.brand.isNotBlank()) {
                        InfoRow("Bandeira", response.brand)
                    }
                    if (response.installments > 1) {
                        InfoRow("Parcelas", response.installments.toString())
                    }
                    if (response.typeCard.isNotBlank()) {
                        InfoRow("Forma de leitura", typeCardLabel(response.typeCard))
                    }
                    if (response.date > 0) {
                        InfoRow("Data", formatDate(response.date))
                    }
                    if (response.nsu.isNotBlank()) {
                        InfoRow("NSU", response.nsu)
                    }
                    if (response.nsuAcquirer.isNotBlank()) {
                        InfoRow("NSU Adquirente", response.nsuAcquirer)
                    }
                    if (response.acquirerName.isNotBlank()) {
                        InfoRow("Adquirente", response.acquirerName)
                    }
                    if (response.authCode.isNotBlank()) {
                        InfoRow("Autorização", response.authCode)
                    }
                    if (response.serialNumber.isNotBlank()) {
                        InfoRow("Terminal", response.serialNumber)
                    }
                }
            }

            if (response.receiptContent.isNotBlank()) {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            "Comprovante",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            response.receiptContent,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = FontFamily.Monospace
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = onNewSale,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Nova Venda")
            }
        }
    }
}

private fun typeCardLabel(typeCard: String): String {
    return when (typeCard) {
        "MAGNETIC" -> "Tarja magnética"
        "EMV_CONTACT" -> "Chip"
        "CONTACTLESS_STRIPE" -> "Aproximação (tarja)"
        "CONTACTLESS_EMV" -> "Aproximação (chip)"
        "TYPED" -> "Digitado"
        "NONE" -> "Nenhum"
        else -> typeCard
    }
}

private fun formatDate(timestampMillis: Long): String {
    val format = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale("pt", "BR"))
    return format.format(Date(timestampMillis))
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            modifier = Modifier.weight(1f)
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
        )
    }
}
