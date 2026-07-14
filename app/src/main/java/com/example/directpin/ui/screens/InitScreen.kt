package com.example.directpin.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.sp
import com.example.directpin.ui.common.DirectPinIntentHelper
import com.example.directpin.ui.common.REQUEST_TYPE_INIT
import com.example.directpin.model.InitRequest
import com.example.directpin.model.InitResponse
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.text.font.FontWeight

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun InitScreen(onSuccess: (InitResponse) -> Unit) {
    var token by remember { mutableStateOf("") }
    var processing by remember { mutableStateOf(false) }
    var initResponse by remember { mutableStateOf<InitResponse?>(null) }
    val keyboardController = LocalSoftwareKeyboardController.current

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        processing = false
        DirectPinIntentHelper.processResponse(
            resultCode = result.resultCode,
            data = result.data,
            responseClass = InitResponse::class.java,
            onSuccess = { response -> initResponse = response }
        )
    }

    val isValid = token.length == 4

    fun sendRequest() {
        val request = InitRequest(type = REQUEST_TYPE_INIT, token = token)
        Log.d("InitScreen", "Sending init request: $request")
        val intent = DirectPinIntentHelper.createRequestIntent(request)
        processing = true
        launcher.launch(intent)
    }

    initResponse?.let { resp ->
        AlertDialog(
            onDismissRequest = {
                initResponse = null
                if (resp.result) onSuccess(resp)
            },
            icon = {
                Icon(
                    imageVector = if (resp.result) Icons.Filled.CheckCircle else Icons.Outlined.Info,
                    contentDescription = null,
                    tint = if (resp.result) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(48.dp)
                )
            },
            title = { Text(if (resp.result) "Sucesso" else "Resultado") },
            text = { Text(resp.message) },
            confirmButton = {
                FilledTonalButton(
                    onClick = {
                        initResponse = null
                        if (resp.result) onSuccess(resp)
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("DirectPin") },
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
                    Text(
                        "Inicialização",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Insira o token de 4 dígitos",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    OutlinedTextField(
                        value = token,
                        onValueChange = { value ->
                            val digits = value.filter { ch -> ch.isDigit() }.take(4)
                            token = digits
                        },
                        label = { Text("Token") },
                        placeholder = { Text("••••") },
                        singleLine = true,
                        textStyle = MaterialTheme.typography.headlineSmall.copy(
                            fontSize = 24.sp,
                            letterSpacing = 8.sp
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                keyboardController?.hide()
                                if (isValid) sendRequest()
                            }
                        ),
                        isError = token.isNotEmpty() && !isValid,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (token.isNotEmpty() && !isValid) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "O token deve ter 4 dígitos",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    FilledTonalButton(
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
                            Text("Iniciar")
                        }
                    }
                }
            }
        }
    }
}
