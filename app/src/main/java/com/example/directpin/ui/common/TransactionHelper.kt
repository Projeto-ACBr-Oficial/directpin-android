package com.example.directpin.ui.common

import com.example.directpin.model.TransactionRequest
import java.text.NumberFormat
import java.util.Locale

object TransactionHelper {
    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

    /**
     * Formata valor em centavos para exibição em reais
     */
    fun formatAmount(amountInCents: Long): String {
        return currencyFormat.format(amountInCents / 100.0)
    }

    /**
     * Cria a requisição de transação
     */
    fun createTransactionRequest(
        amountDigits: String,
        transactionType: String,
        creditType: String,
        installments: String,
        interestType: String = TransactionConstants.DEFAULT_INTEREST_TYPE,
        autoConfirm: Boolean = true,
        isTyped: Boolean = false,
        isPreAuth: Boolean = false,
        printReceipt: Boolean = true
    ): TransactionRequest {
        val amount = amountDigits.toLongOrNull() ?: 0L
        val finalCreditType = if (transactionType == TransactionConstants.TYPE_CREDIT) {
            creditType
        } else {
            TransactionConstants.CREDIT_NO_INSTALLMENT
        }

        val finalInstallments = if (finalCreditType == TransactionConstants.CREDIT_INSTALLMENT) {
            installments.toIntOrNull() ?: TransactionConstants.DEFAULT_INSTALLMENTS
        } else {
            TransactionConstants.DEFAULT_INSTALLMENTS
        }

        return TransactionRequest(
            type = REQUEST_TYPE_TRANSACTION,
            amount = amount,
            typeTransaction = transactionType,
            creditType = finalCreditType,
            installment = finalInstallments,
            isTyped = isTyped,
            isPreAuth = isPreAuth,
            autoConfirm = autoConfirm,
            interestType = interestType,
            printReceipt = printReceipt
        )
    }
}