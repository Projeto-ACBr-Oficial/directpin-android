# DirectPin Sample - Kotlin

Este é um projeto Android (Kotlin + Jetpack Compose) que demonstra como integrar com o DirectPin (Intent) através de Android Intents.

## Estrutura do Projeto

```
app/src/main/java/com/example/directpin/
├── MainActivity.kt                          # Ponto de entrada e navegação da aplicação
├── model/                                   # Modelos de dados (request/response)
│   ├── InitRequest.kt / InitResponse.kt
│   ├── TransactionRequest.kt / TransactionResponse.kt
│   ├── CancelTransactionRequest.kt / CancelTransactionResponse.kt
│   ├── ConfirmationTransactionRequest.kt / ConfirmationTransactionResponse.kt
│   ├── UndoTransactionRequest.kt / UndoTransactionResponse.kt
│   ├── AbortRequest.kt
│   ├── FinalResult.kt                       # Enum do resultado do processamento
│   └── CodeResult.kt                        # Enum dos códigos de retorno
├── ui/
│   ├── common/                              # Constantes e helpers compartilhados
│   │   ├── Constants.kt
│   │   ├── TransactionConstants.kt
│   │   ├── DirectPinIntentHelper.kt
│   │   └── TransactionHelper.kt
│   ├── screens/                             # Telas da aplicação
│   │   ├── InitScreen.kt
│   │   ├── AmountEntryScreen.kt
│   │   ├── PaymentTypeScreen.kt
│   │   └── CancelTransactionScreen.kt
│   └── theme/                               # Tema Material 3 (cores claro/escuro)
│       ├── Color.kt
│       └── Theme.kt
```

## Funcionalidades

1. **Inicialização (Init)**: Inicializa a conexão com o DirectPin usando um token de 4 dígitos
2. **Transação**: Permite realizar transações de débito, PIX, crédito à vista ou parcelado (estabelecimento/emissor)
   - Botão "Opções da Transação" na tela de tipo de pagamento abre um menu com switches para `autoConfirm`, `isTyped`, `isPreAuth` e `printReceipt`
3. **Cancelamento**: Cancela uma transação usando o NSU

> Confirmação e Desfazimento (fluxo de `autoConfirm: false`) e Abort já têm os modelos de dados prontos em `model/`, mas ainda não possuem tela própria no app.

## Dependências

- Jetpack Compose (Material 3, navigation-compose, activity-compose)
- `com.google.code.gson:gson` — serialização das requisições/respostas trocadas com o DirectPin

## Comunicação

A comunicação com o app DirectPin é feita via `Intent`, usando `DirectPinIntentHelper` (`ui/common/DirectPinIntentHelper.kt`) para montar a requisição (JSON via Gson) e processar a resposta recebida por `ActivityResultLauncher`.

### Intent Action

```
br.com.inovare.directpin_intent.action.PROCESS
```

## Como Usar

1. Abra o projeto no Android Studio
2. Sincronize o Gradle
3. Execute o app em um dispositivo/emulador com o DirectPin instalado

## Requisitos

- Android SDK 23+ (compileSdk 34)
- Kotlin 1.9+ / JVM 17
- DirectPin instalado no dispositivo Android

## Notas

- A comunicação com o DirectPin é feita através de Android Intents
- O resultado das operações é retornado via `ActivityResultLauncher`/`onActivityResult`
- Os campos de cada classe de `model/` seguem a documentação oficial "DirectPin – Integração Intent"
