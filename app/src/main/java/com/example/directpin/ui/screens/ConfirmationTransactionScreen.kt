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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.example.directpin.model.ConfirmationTransactionRequest
import com.example.directpin.model.ConfirmationTransactionResponse
import com.example.directpin.ui.common.DirectPinIntentHelper
import com.example.directpin.ui.common.REQUEST_TYPE_CONFIRMATION

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ConfirmationTransactionScreen(
    initialNsu: String? = null,
    onFinish: (ConfirmationTransactionResponse?) -> Unit
) {
    var nsu by remember { mutableStateOf(initialNsu ?: "") }
    var processing by remember { mutableStateOf(false) }
    var confirmationResponse by remember { mutableStateOf<ConfirmationTransactionResponse?>(null) }
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
            responseClass = ConfirmationTransactionResponse::class.java,
            onSuccess = { response -> confirmationResponse = response }
        )
    }

    val isValid = nsu.isNotBlank()

    fun sendRequest() {
        val request = ConfirmationTransactionRequest(
            type = REQUEST_TYPE_CONFIRMATION,
            nsu = nsu.trim()
        )
        Log.d("ConfirmationTransactionScreen", "Sending confirmation request: $request")
        val intent = DirectPinIntentHelper.createRequestIntent(request)
        processing = true
        launcher.launch(intent)
    }

    confirmationResponse?.let { resp ->
        AlertDialog(
            onDismissRequest = {
                confirmationResponse = null
                onFinish(resp)
            },
            icon = {
                Icon(
                    Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                )
            },
            title = { Text("Resultado") },
            text = { Text(resp.message) },
            confirmButton = {
                Button(onClick = {
                    confirmationResponse = null
                    onFinish(resp)
                }) { Text("OK") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Confirmar Transação") },
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
                        imageVector = Icons.Filled.TaskAlt,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Confirmar Transação",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Informe o NSU da transação pendente que deseja confirmar",
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
                        enabled = isValid && !processing
                    ) {
                        if (processing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Confirmar Transação")
                        }
                    }
                }
            }
        }
    }
}
