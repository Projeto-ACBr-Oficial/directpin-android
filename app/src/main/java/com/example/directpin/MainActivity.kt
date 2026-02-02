package com.example.directpin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.Payment
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.directpin.model.TransactionResponse
import com.example.directpin.ui.screens.InitScreen
import com.example.directpin.ui.screens.AmountEntryScreen
import com.example.directpin.ui.screens.PaymentTypeScreen
import com.example.directpin.ui.screens.CancelTransactionScreen
import com.example.directpin.ui.theme.DirectPinTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DirectPinTheme {
                DirectPinApp()
            }
        }
    }
}

enum class Screen { INIT, TRANSACTION, CANCEL_TRANSACTION }

/** Sub-telas do fluxo de transação: valor → tipo de pagamento */
enum class TransactionFlow { AMOUNT_ENTRY, PAYMENT_TYPE }

@Composable
fun DirectPinApp() {
    var currentScreen by remember { mutableStateOf(Screen.INIT) }
    var transactionFlow by remember { mutableStateOf(TransactionFlow.AMOUNT_ENTRY) }
    var pendingAmountCents by remember { mutableStateOf<Long?>(null) }
    var transactionResponse by remember { mutableStateOf<TransactionResponse?>(null) }
    var isInitialized by remember { mutableStateOf(false) }

    if (!isInitialized) {
        InitScreen(
            onSuccess = {
                isInitialized = true
                currentScreen = Screen.TRANSACTION
            }
        )
        return
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = currentScreen == Screen.TRANSACTION,
                    onClick = {
                        currentScreen = Screen.TRANSACTION
                        transactionFlow = TransactionFlow.AMOUNT_ENTRY
                        pendingAmountCents = null
                    },
                    icon = {
                        Icon(
                            imageVector = if (currentScreen == Screen.TRANSACTION) Icons.Filled.Payment else Icons.Outlined.Payment,
                            contentDescription = "Transação"
                        )
                    },
                    label = { Text("Transação") }
                )
                NavigationBarItem(
                    selected = currentScreen == Screen.CANCEL_TRANSACTION,
                    onClick = {
                        currentScreen = Screen.CANCEL_TRANSACTION
                    },
                    icon = {
                        Icon(
                            imageVector = if (currentScreen == Screen.CANCEL_TRANSACTION) Icons.Filled.Cancel else Icons.Outlined.Cancel,
                            contentDescription = "Cancelar"
                        )
                    },
                    label = { Text("Cancelar") }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (currentScreen) {
                Screen.INIT -> InitScreen(
                    onSuccess = {
                        isInitialized = true
                        currentScreen = Screen.TRANSACTION
                    }
                )
                Screen.TRANSACTION -> {
                    if (transactionFlow == TransactionFlow.AMOUNT_ENTRY) {
                        AmountEntryScreen(
                            onPay = { amountCents ->
                                pendingAmountCents = amountCents
                                transactionFlow = TransactionFlow.PAYMENT_TYPE
                            }
                        )
                    } else {
                        pendingAmountCents?.let { amountCents ->
                            PaymentTypeScreen(
                                amountCents = amountCents,
                                onFinish = { response ->
                                    transactionResponse = response
                                    transactionFlow = TransactionFlow.AMOUNT_ENTRY
                                    pendingAmountCents = null
                                },
                                onBack = {
                                    transactionFlow = TransactionFlow.AMOUNT_ENTRY
                                    pendingAmountCents = null
                                }
                            )
                        }
                    }
                }
                Screen.CANCEL_TRANSACTION -> CancelTransactionScreen(
                    initialNsu = transactionResponse?.nsu,
                    onFinish = { }
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewApp() {
    DirectPinTheme {
        DirectPinApp()
    }
}
