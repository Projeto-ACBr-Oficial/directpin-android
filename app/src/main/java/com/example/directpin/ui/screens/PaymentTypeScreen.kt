package com.example.directpin.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.directpin.model.TransactionResponse
import com.example.directpin.ui.common.DirectPinIntentHelper
import com.example.directpin.ui.common.TransactionConstants
import com.example.directpin.ui.common.TransactionHelper

enum class PaymentOption {
    DEBITO,
    PIX,
    AVISTA,
    PARCELADO_ESTABELECIMENTO,
    PARCELADO_EMISSOR
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentTypeScreen(
    amountCents: Long,
    onFinish: (TransactionResponse?) -> Unit,
    onBack: (() -> Unit)?
) {
    var option by remember { mutableStateOf(PaymentOption.DEBITO) }
    var installments by remember { mutableStateOf("2") }
    var processing by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var autoConfirm by remember { mutableStateOf(true) }
    var isTyped by remember { mutableStateOf(false) }
    var isPreAuth by remember { mutableStateOf(false) }
    var printReceipt by remember { mutableStateOf(true) }
    var showOptionsSheet by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        processing = false
        DirectPinIntentHelper.processResponse(
            resultCode = result.resultCode,
            data = result.data,
            responseClass = TransactionResponse::class.java,
            onSuccess = { onFinish(it) },
            onError = { errorMessage = it }
        )
    }

    val isParcelado = option == PaymentOption.PARCELADO_ESTABELECIMENTO ||
            option == PaymentOption.PARCELADO_EMISSOR
    val isValidInstallments = !isParcelado || run {
        val n = installments.toIntOrNull()
        n != null && n in 1..99
    }
    val canConfirm = !processing && (!isParcelado || isValidInstallments)
    val amountFormatted = TransactionHelper.formatAmount(amountCents)

    fun confirmAndSend() {
        if (!canConfirm) return
        val transactionType = when (option) {
            PaymentOption.DEBITO -> TransactionConstants.TYPE_DEBIT
            PaymentOption.PIX -> TransactionConstants.TYPE_PIX
            else -> TransactionConstants.TYPE_CREDIT
        }
        val creditType = if (isParcelado)
            TransactionConstants.CREDIT_INSTALLMENT
        else
            TransactionConstants.CREDIT_NO_INSTALLMENT
        val installmentsStr = if (isParcelado) (installments.toIntOrNull() ?: 2).toString() else "1"
        val interestType = when (option) {
            PaymentOption.PARCELADO_ESTABELECIMENTO -> TransactionConstants.INTEREST_TYPE_MERCHANT
            PaymentOption.PARCELADO_EMISSOR -> TransactionConstants.INTEREST_TYPE_ISSUER
            else -> TransactionConstants.DEFAULT_INTEREST_TYPE
        }
        val request = TransactionHelper.createTransactionRequest(
            amountDigits = amountCents.toString(),
            transactionType = transactionType,
            creditType = creditType,
            installments = installmentsStr,
            interestType = interestType,
            autoConfirm = autoConfirm,
            isTyped = isTyped,
            isPreAuth = isPreAuth,
            printReceipt = printReceipt
        )
        processing = true
        errorMessage = null
        val intent = DirectPinIntentHelper.createRequestIntent(request)
        launcher.launch(intent)
    }

    if (errorMessage != null) {
        AlertDialog(
            onDismissRequest = { errorMessage = null },
                icon = {
                Icon(
                    Icons.Outlined.Error,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(48.dp)
                )
            },
            title = { Text("Erro") },
            text = { Text(errorMessage!!) },
            confirmButton = {
                Button(onClick = { errorMessage = null }) { Text("OK") }
            }
        )
    }

    if (showOptionsSheet) {
        TransactionOptionsSheet(
            autoConfirm = autoConfirm,
            onAutoConfirmChange = { autoConfirm = it },
            isTyped = isTyped,
            onIsTypedChange = { isTyped = it },
            isPreAuth = isPreAuth,
            onIsPreAuthChange = { isPreAuth = it },
            printReceipt = printReceipt,
            onPrintReceiptChange = { printReceipt = it },
            onDismiss = { showOptionsSheet = false }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tipo de pagamento") },
                navigationIcon = {
                    if (onBack != null) {
                        IconButton(onClick = onBack) {
                            Icon(
                                Icons.Filled.ArrowBack,
                                contentDescription = "Voltar"
                            )
                        }
                    }
                },
                actions = {
                    TextButton(onClick = { showOptionsSheet = true }) {
                        Icon(
                            Icons.Filled.Tune,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Opções da Transação")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        "Valor",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        amountFormatted,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "Como deseja pagar?",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
            Spacer(modifier = Modifier.height(12.dp))
            OptionTile(
                selected = option == PaymentOption.DEBITO,
                title = "Débito",
                subtitle = "Pagamento à vista no débito",
                icon = Icons.Filled.CreditCard,
                onClick = { option = PaymentOption.DEBITO }
            )
            OptionTile(
                selected = option == PaymentOption.PIX,
                title = "PIX",
                subtitle = "Pagamento instantâneo via PIX",
                icon = Icons.Filled.QrCode2,
                onClick = { option = PaymentOption.PIX }
            )
            OptionTile(
                selected = option == PaymentOption.AVISTA,
                title = "Crédito à vista",
                subtitle = "Crédito em uma única parcela",
                icon = Icons.Filled.Payment,
                onClick = { option = PaymentOption.AVISTA }
            )
            OptionTile(
                selected = option == PaymentOption.PARCELADO_ESTABELECIMENTO,
                title = "Parcelado estabelecimento",
                subtitle = "Parcelas com juros do estabelecimento",
                icon = Icons.Filled.Store,
                onClick = { option = PaymentOption.PARCELADO_ESTABELECIMENTO }
            )
            OptionTile(
                selected = option == PaymentOption.PARCELADO_EMISSOR,
                title = "Parcelado emissor",
                subtitle = "Parcelas com juros do emissor",
                icon = Icons.Filled.AccountBalanceWallet,
                onClick = { option = PaymentOption.PARCELADO_EMISSOR }
            )
            if (isParcelado) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    "Número de parcelas",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = installments,
                    onValueChange = {
                        val digits = it.filter { c -> c.isDigit() }
                        if (digits.length <= 2) installments = digits
                    },
                    label = { Text("Parcelas (1 a 99)") },
                    placeholder = { Text("Ex: 3") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = installments.isNotEmpty() && !isValidInstallments,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = { confirmAndSend() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = canConfirm
            ) {
                if (processing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Confirmar e pagar")
                }
            }
        }
    }
}

@Composable
private fun OptionTile(
    selected: Boolean,
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    val backgroundColor = if (selected)
        MaterialTheme.colorScheme.primaryContainer
    else
        MaterialTheme.colorScheme.surfaceVariant
    val contentColor = if (selected)
        MaterialTheme.colorScheme.onPrimaryContainer
    else
        MaterialTheme.colorScheme.onSurfaceVariant
    val titleColor = if (selected)
        MaterialTheme.colorScheme.onPrimaryContainer
    else
        MaterialTheme.colorScheme.onSurface
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.size(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = titleColor
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
        if (selected) {
            Icon(
                Icons.Filled.CheckCircle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TransactionOptionsSheet(
    autoConfirm: Boolean,
    onAutoConfirmChange: (Boolean) -> Unit,
    isTyped: Boolean,
    onIsTypedChange: (Boolean) -> Unit,
    isPreAuth: Boolean,
    onIsPreAuthChange: (Boolean) -> Unit,
    printReceipt: Boolean,
    onPrintReceiptChange: (Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            Text(
                "Opções da transação",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            LabeledSwitch("Confirmar automaticamente", autoConfirm, onAutoConfirmChange)
            LabeledSwitch("Transação digitada", isTyped, onIsTypedChange)
            LabeledSwitch("Pré-autorização", isPreAuth, onIsPreAuthChange)
            LabeledSwitch("Imprimir comprovante", printReceipt, onPrintReceiptChange)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun LabeledSwitch(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
