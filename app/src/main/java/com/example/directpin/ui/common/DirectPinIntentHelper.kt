package com.example.directpin.ui.common

import android.app.Activity
import android.content.Intent
import android.util.Log
import com.google.gson.Gson

/**
 * Helper para comunicação com DirectPin via Intent
 */
object DirectPinIntentHelper {
    private const val TAG = "DirectPinIntentHelper"
    private val gsonInstance = Gson()

    /**
     * Cria uma Intent para enviar requisição ao DirectPin
     */
    fun createRequestIntent(request: Any): Intent {
        return Intent(DIRECT_PIN_ACTION).apply {
            putExtra("request", gsonInstance.toJson(request))
        }
    }

    /**
     * Processa a resposta do DirectPin
     */
    fun <T> processResponse(
        resultCode: Int,
        data: Intent?,
        responseClass: Class<T>,
        onSuccess: (T) -> Unit,
        onError: (String) -> Unit = {}
    ) {
        if (resultCode == Activity.RESULT_OK) {
            val json = data?.getStringExtra("response")
            Log.d(TAG, "Response: $json")

            if (!json.isNullOrEmpty()) {
                try {
                    @Suppress("UNCHECKED_CAST")
                    val response = gsonInstance.fromJson(json, responseClass) as T
                    onSuccess(response)
                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing response: ${e.message}", e)
                    onError("Erro ao processar resposta: ${e.message}")
                }
            } else {
                onError("Resposta vazia recebida")
            }
        } else {
            onError("Operação cancelada ou falhou")
        }
    }
}