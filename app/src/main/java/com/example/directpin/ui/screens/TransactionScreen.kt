package com.example.directpin.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.directpin.model.TransactionResponse
import com.example.directpin.ui.common.DirectPinIntentHelper
import com.example.directpin.ui.common.TransactionConstants
import com.example.directpin.ui.common.TransactionHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionScreen(onFinish: (TransactionResponse?) -> Unit) {
    // Estados do formulário
    var amountDigits by remember { mutableStateOf("") }
    var transactionType by remember { mutableStateOf(TransactionConstants.TYPE_DEBIT) }
    var transactionTypeExpanded by remember { mutableStateOf(false) }
    var creditType by remember { mutableStateOf(TransactionConstants.CREDIT_NO_INSTALLMENT) }
    var creditTypeExpanded by remember { mutableStateOf(false) }
    var installments by remember { mutableStateOf("1") }

    // Estados de controle
    var processing by remember { mutableStateOf(false) }
    var transactionResponse by remember { mutableStateOf<TransactionResponse?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        processing = false
        DirectPinIntentHelper.processResponse(
            resultCode = result.resultCode,
            data = result.data,
            responseClass = TransactionResponse::class.java,
            onSuccess = { response ->
                transactionResponse = response
            },
            onError = { error ->
                errorMessage = error
            }
        )
    }

    // Validações
    val isValidAmount = TransactionHelper.isValidAmount(amountDigits)
    val isValidInstallments = TransactionHelper.isValidInstallments(installments)
    val isCreditTransaction = transactionType == TransactionConstants.TYPE_CREDIT
    val isInstallmentCredit = isCreditTransaction && 
                              creditType == TransactionConstants.CREDIT_INSTALLMENT
    val canSubmit = isValidAmount && 
                   (!isInstallmentCredit || isValidInstallments) && 
                   !processing

    // Função para enviar transação
    fun sendTransaction() {
        errorMessage = null // Limpa erro anterior
        val request = TransactionHelper.createTransactionRequest(
            amountDigits = amountDigits,
            transactionType = transactionType,
            creditType = creditType,
            installments = installments
        )
        val intent = DirectPinIntentHelper.createRequestIntent(request)
        processing = true
        launcher.launch(intent)
    }

    Column(modifier = Modifier.padding(16.dp)) {
        // Campo de valor
        AmountField(
            amountDigits = amountDigits,
            onAmountChange = { amountDigits = TransactionHelper.extractDigits(it) },
            isValid = isValidAmount
        )
        // Dropdown de tipo de transação
        TransactionTypeDropdown(
            selectedType = transactionType,
            expanded = transactionTypeExpanded,
            onExpandedChange = { transactionTypeExpanded = it },
            onTypeSelected = { 
                transactionType = it
                transactionTypeExpanded = false
            }
        )

        // Dropdown de tipo de crédito (apenas para transações de crédito)
        if (isCreditTransaction) {
            CreditTypeDropdown(
                selectedType = creditType,
                expanded = creditTypeExpanded,
                onExpandedChange = { creditTypeExpanded = it },
                onTypeSelected = {
                    creditType = it
                    creditTypeExpanded = false
                }
            )

            // Campo de parcelas (apenas para crédito parcelado)
            if (isInstallmentCredit) {
                InstallmentsField(
                    value = installments,
                    onValueChange = { installments = TransactionHelper.extractDigits(it) },
                    isValid = isValidInstallments
                )
            }
        }

        // Botão de envio
        SubmitButton(
            enabled = canSubmit,
            processing = processing,
            onClick = { sendTransaction() }
        )
    }

    // Dialog de resultado
    transactionResponse?.let { resp ->
        ResultDialog(
            message = resp.message,
            onDismiss = {
                transactionResponse = null
                onFinish(resp)
            }
        )
    }
    
    // Dialog de erro
    errorMessage?.let { error ->
        ErrorDialog(
            message = error,
            onDismiss = { errorMessage = null }
        )
    }
}

// Componentes extraídos para melhor organização

@Composable
private fun AmountField(
    amountDigits: String,
    onAmountChange: (String) -> Unit,
    isValid: Boolean
) {
    OutlinedTextField(
        value = TransactionHelper.formatAmount(amountDigits.toLongOrNull() ?: 0L),
        onValueChange = onAmountChange,
        label = { Text("Valor") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        isError = !isValid && amountDigits.isNotEmpty(),
        modifier = Modifier.fillMaxWidth()
    )
    if (!isValid && amountDigits.isNotEmpty()) {
        Text(
            text = "O valor mínimo é R$ 0,01",
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TransactionTypeDropdown(
    selectedType: String,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onTypeSelected: (String) -> Unit
) {
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = onExpandedChange
    ) {
        OutlinedTextField(
            value = TransactionHelper.getTransactionTypeLabel(selectedType),
            onValueChange = {},
            readOnly = true,
            label = { Text("Tipo") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) }
        ) {
            TransactionTypeOption(
                type = TransactionConstants.TYPE_DEBIT,
                label = TransactionConstants.LABEL_DEBIT,
                onSelected = onTypeSelected
            )
            TransactionTypeOption(
                type = TransactionConstants.TYPE_CREDIT,
                label = TransactionConstants.LABEL_CREDIT,
                onSelected = onTypeSelected
            )
            TransactionTypeOption(
                type = TransactionConstants.TYPE_VOUCHER,
                label = TransactionConstants.LABEL_VOUCHER,
                onSelected = onTypeSelected
            )
            TransactionTypeOption(
                type = TransactionConstants.TYPE_PIX,
                label = TransactionConstants.LABEL_PIX,
                onSelected = onTypeSelected
            )
            TransactionTypeOption(
                type = TransactionConstants.TYPE_NONE,
                label = TransactionConstants.LABEL_NONE,
                onSelected = onTypeSelected
            )
        }
    }
}

@Composable
private fun TransactionTypeOption(
    type: String,
    label: String,
    onSelected: (String) -> Unit
) {
    DropdownMenuItem(
        text = { Text(label) },
        onClick = { onSelected(type) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreditTypeDropdown(
    selectedType: String,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onTypeSelected: (String) -> Unit
) {
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = onExpandedChange
    ) {
        OutlinedTextField(
            value = TransactionHelper.getCreditTypeLabel(selectedType),
            onValueChange = {},
            readOnly = true,
            label = { Text("Tipo de Crédito") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) }
        ) {
            DropdownMenuItem(
                text = { Text(TransactionConstants.LABEL_INSTALLMENT) },
                onClick = { onTypeSelected(TransactionConstants.CREDIT_INSTALLMENT) }
            )
            DropdownMenuItem(
                text = { Text(TransactionConstants.LABEL_NO_INSTALLMENT) },
                onClick = { onTypeSelected(TransactionConstants.CREDIT_NO_INSTALLMENT) }
            )
        }
    }
}

@Composable
private fun InstallmentsField(
    value: String,
    onValueChange: (String) -> Unit,
    isValid: Boolean
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text("Parcelas") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        isError = !isValid && value.isNotEmpty(),
        modifier = Modifier.fillMaxWidth()
    )
    if (!isValid && value.isNotEmpty()) {
        Text(
            text = "Número de parcelas deve ser entre 1 e 99",
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
private fun SubmitButton(
    enabled: Boolean,
    processing: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        enabled = enabled
    ) {
        if (processing) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                strokeWidth = 2.dp
            )
        } else {
            Text("Enviar Transação")
        }
    }
}

@Composable
private fun ResultDialog(
    message: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = onDismiss) { 
                Text("OK") 
            }
        },
        title = { Text("Resultado") },
        text = { Text(message) }
    )
}

@Composable
private fun ErrorDialog(
    message: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = onDismiss) { 
                Text("OK") 
            }
        },
        title = { Text("Erro") },
        text = { Text(message) }
    )
}
