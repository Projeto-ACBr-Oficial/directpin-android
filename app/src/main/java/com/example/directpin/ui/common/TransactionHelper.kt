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
     * Extrai apenas dígitos de uma string
     */
    fun extractDigits(input: String): String {
        return input.filter { it.isDigit() }
    }

    /**
     * Obtém o label do tipo de transação
     */
    fun getTransactionTypeLabel(type: String): String {
        return when (type) {
            TransactionConstants.TYPE_DEBIT -> TransactionConstants.LABEL_DEBIT
            TransactionConstants.TYPE_CREDIT -> TransactionConstants.LABEL_CREDIT
            TransactionConstants.TYPE_VOUCHER -> TransactionConstants.LABEL_VOUCHER
            TransactionConstants.TYPE_PIX -> TransactionConstants.LABEL_PIX
            TransactionConstants.TYPE_NONE -> TransactionConstants.LABEL_NONE
            else -> TransactionConstants.LABEL_DEBIT
        }
    }

    /**
     * Obtém o label do tipo de crédito
     */
    fun getCreditTypeLabel(type: String): String {
        return when (type) {
            TransactionConstants.CREDIT_INSTALLMENT -> TransactionConstants.LABEL_INSTALLMENT
            TransactionConstants.CREDIT_NO_INSTALLMENT -> TransactionConstants.LABEL_NO_INSTALLMENT
            else -> TransactionConstants.LABEL_NO_INSTALLMENT
        }
    }

    /**
     * Valida se o valor é válido
     */
    fun isValidAmount(amountDigits: String): Boolean {
        val amount = amountDigits.toLongOrNull() ?: 0L
        return amount >= TransactionConstants.MIN_AMOUNT
    }

    /**
     * Valida se o número de parcelas é válido
     */
    fun isValidInstallments(installments: String): Boolean {
        val value = installments.toIntOrNull() ?: 0
        return value >= 1 && value <= 99 // Limite razoável de parcelas
    }

    /**
     * Cria a requisição de transação
     */
    fun createTransactionRequest(
        amountDigits: String,
        transactionType: String,
        creditType: String,
        installments: String,
        interestType: String = TransactionConstants.DEFAULT_INTEREST_TYPE
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
            type = TransactionConstants.REQUEST_TYPE,
            amount = amount,
            typeTransaction = transactionType,
            creditType = finalCreditType,
            installment = finalInstallments,
            isTyped = false,
            isPreAuth = false,
            interestType = interestType,
            printReceipt = true
        )
    }
}