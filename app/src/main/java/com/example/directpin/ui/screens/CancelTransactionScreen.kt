package com.example.directpin.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.directpin.ui.common.DirectPinIntentHelper
import com.example.directpin.ui.common.REQUEST_TYPE_CANCEL_TRANSACTION
import com.example.directpin.model.CancelTransactionRequest
import com.example.directpin.model.CancelTransactionResponse
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.text.font.FontWeight

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CancelTransactionScreen(
    initialNsu: String? = null,
    onFinish: (CancelTransactionResponse?) -> Unit
) {
    var nsu by remember { mutableStateOf(initialNsu ?: "") }
    var processing by remember { mutableStateOf(false) }
    var cancelResponse by remember { mutableStateOf<CancelTransactionResponse?>(null) }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(initialNsu) {
        if (initialNsu != null && initialNsu.isNotBlank()) {
            nsu = initialNsu
        }
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        processing = false
        DirectPinIntentHelper.processResponse(
            resultCode = result.resultCode,
            data = result.data,
            responseClass = CancelTransactionResponse::class.java,
            onSuccess = { response -> cancelResponse = response }
        )
    }

    val isValid = nsu.isNotBlank()

    fun sendRequest() {
        val request = CancelTransactionRequest(
            type = REQUEST_TYPE_CANCEL_TRANSACTION,
            nsu = nsu.trim()
        )
        Log.d("CancelTransactionScreen", "Sending cancel transaction request: $request")
        val intent = DirectPinIntentHelper.createRequestIntent(request)
        processing = true
        launcher.launch(intent)
    }

    cancelResponse?.let { resp ->
        OperationResultScreen(
            title = "Estorno",
            result = resp.result,
            finalResult = resp.finalResult,
            message = resp.message,
            receiptContent = resp.receiptContent,
            onDismiss = {
                cancelResponse = null
                onFinish(resp)
            }
        )
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Estornar Transação") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Cancel,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Estornar Transação",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Informe o NSU da transação que deseja estornar",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    OutlinedTextField(
                        value = nsu,
                        onValueChange = { nsu = it },
                        label = { Text("NSU da Transação") },
                        placeholder = { Text("Digite o NSU") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                keyboardController?.hide()
                                if (isValid) sendRequest()
                            }
                        ),
                        isError = nsu.isNotEmpty() && !isValid,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (nsu.isNotEmpty() && !isValid) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "O NSU é obrigatório",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { sendRequest() },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = isValid && !processing,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        if (processing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Estornar Transação")
                        }
                    }
                }
            }
        }
    }
}
