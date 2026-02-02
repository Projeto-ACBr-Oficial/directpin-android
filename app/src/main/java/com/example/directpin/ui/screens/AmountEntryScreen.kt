package com.example.directpin.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Backspace
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.directpin.ui.common.TransactionConstants
import com.example.directpin.ui.common.TransactionHelper

/** Valor máximo em centavos: R$ 999.999,99 */
private const val MAX_CENTS = 999_999_99L

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AmountEntryScreen(onPay: (Long) -> Unit) {
    var amountCents by remember { mutableLongStateOf(0L) }
    val primaryColor = MaterialTheme.colorScheme.primary
    val displayText = TransactionHelper.formatAmount(amountCents)
    val canPay = amountCents >= TransactionConstants.MIN_AMOUNT

    fun onDigit(digit: Int) {
        val newAmount = amountCents * 10 + digit
        if (newAmount <= MAX_CENTS) {
            amountCents = newAmount
        }
    }

    fun onBackspace() {
        amountCents = amountCents / 10
    }

    fun onClear() {
        amountCents = 0L
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Transação") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp, horizontal = 24.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                    .padding(vertical = 20.dp, horizontal = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = displayText,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp
                    ),
                    textAlign = TextAlign.Center
                )
            }
            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                KeypadRow(primaryColor = primaryColor, digits = listOf(1, 2, 3)) { onDigit(it) }
                KeypadRow(primaryColor = primaryColor, digits = listOf(4, 5, 6)) { onDigit(it) }
                KeypadRow(primaryColor = primaryColor, digits = listOf(7, 8, 9)) { onDigit(it) }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    KeypadKey(
                        label = "X",
                        backgroundColor = Color.Red,
                        foregroundColor = Color.White,
                        modifier = Modifier.weight(1f),
                        onClick = { onClear() }
                    )
                    KeypadKey(
                        label = "0",
                        backgroundColor = primaryColor.copy(alpha = 0.15f),
                        foregroundColor = primaryColor,
                        modifier = Modifier.weight(1f),
                        onClick = { onDigit(0) }
                    )
                    KeypadKey(
                        icon = Icons.Outlined.Backspace,
                        backgroundColor = Color(0xFFFF9800),
                        foregroundColor = Color.White,
                        modifier = Modifier.weight(1f),
                        onClick = { onBackspace() }
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = { if (canPay) onPay(amountCents) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = canPay,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Pagar")
            }
        }
    }
}

@Composable
private fun KeypadRow(
    primaryColor: Color,
    digits: List<Int>,
    onDigit: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        digits.forEach { d ->
            KeypadKey(
                label = "$d",
                backgroundColor = primaryColor.copy(alpha = 0.15f),
                foregroundColor = primaryColor,
                modifier = Modifier.weight(1f),
                onClick = { onDigit(d) }
            )
        }
    }
}

@Composable
private fun KeypadKey(
    modifier: Modifier = Modifier,
    label: String? = null,
    icon: ImageVector? = null,
    backgroundColor: Color,
    foregroundColor: Color,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(12.dp)
    Box(
        modifier = modifier
            .heightIn(min = 48.dp, max = 72.dp)
            .clip(shape)
            .background(backgroundColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        if (label != null) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 22.sp
                ),
                color = foregroundColor
            )
        } else if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = foregroundColor,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}
