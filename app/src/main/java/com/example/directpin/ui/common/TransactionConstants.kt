package com.example.directpin.ui.common

object TransactionConstants {
    // Tipos de transação
    const val TYPE_DEBIT = "DEBIT"
    const val TYPE_CREDIT = "CREDIT"
    const val TYPE_VOUCHER = "VOUCHER"
    const val TYPE_PIX = "PIX"
    const val TYPE_NONE = "NONE"

    // Labels dos tipos de transação
    const val LABEL_DEBIT = "débito"
    const val LABEL_CREDIT = "crédito"
    const val LABEL_VOUCHER = "voucher"
    const val LABEL_PIX = "pix"
    const val LABEL_NONE = "nenhum"

    // Tipos de crédito
    const val CREDIT_INSTALLMENT = "INSTALLMENT"
    const val CREDIT_NO_INSTALLMENT = "NO_INSTALLMENT"

    // Labels dos tipos de crédito
    const val LABEL_INSTALLMENT = "Com parcelas"
    const val LABEL_NO_INSTALLMENT = "Sem parcelas"

    // Tipo de parcelamento (juros)
    const val INTEREST_TYPE_MERCHANT = "MERCHANT" // Parcelado estabelecimento
    const val INTEREST_TYPE_ISSUER = "ISSUER"     // Parcelado emissor

    // Configurações padrão
    const val DEFAULT_INSTALLMENTS = 1
    const val DEFAULT_INTEREST_TYPE = "MERCHANT"
    const val REQUEST_TYPE = "TRANSACTION"

    // Validação
    const val MIN_AMOUNT = 1L // Mínimo de 1 centavo
}