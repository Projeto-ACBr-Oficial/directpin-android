# DirectPIN - Android

Este é um projeto Android (Kotlin + Jetpack Compose) que demonstra como integrar com o DirectPIN através de Android Intents.

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
│   │   ├── TransactionResultScreen.kt        # Resultado da transação (valor, cartão, comprovante, etc.)
│   │   ├── CancelTransactionScreen.kt        # Estorno de transação
│   │   ├── ConfirmationTransactionScreen.kt
│   │   ├── UndoTransactionScreen.kt
│   │   ├── OperationResultScreen.kt          # Resultado de Estorno/Confirmação/Desfazimento
│   │   └── ResultStatus.kt                   # Ícone/cor/label compartilhados por finalResult
│   └── theme/                               # Tema Material 3 (cores claro/escuro)
│       ├── Color.kt
│       └── Theme.kt
```

## Funcionalidades

1. **Inicialização (Init)**: Inicializa a conexão com o DirectPIN usando um token de 4 dígitos
2. **Transação**: Permite realizar transações de débito, PIX, crédito à vista ou parcelado (estabelecimento/emissor)
   - Botão "Opções da Transação" na tela de tipo de pagamento abre um menu com switches para `autoConfirm`, `isTyped`, `isPreAuth` e `printReceipt`
   - Ao concluir, navega para uma tela de resultado (`TransactionResultScreen`) com valor, cartão, bandeira, parcelas, forma de leitura, data, NSU, autorização, terminal e comprovante (quando `printReceipt: false`), com botão "Nova Venda"
3. **Estornar Transação**: Estorna (cancela) uma transação usando o NSU
4. **Confirmar Transação**: Confirma uma transação pendente (fluxo de `autoConfirm: false`) usando o NSU
5. **Desfazer Transação**: Desfaz uma transação pendente (fluxo de `autoConfirm: false`) usando o NSU

Cada operação (Transação, Estornar, Confirmar, Desfazer) tem sua própria aba na navegação inferior. O NSU da última transação é reaproveitado automaticamente como sugestão nas telas de Estornar/Confirmar/Desfazer. O resultado de Estorno/Confirmação/Desfazimento é exibido numa tela compartilhada (`OperationResultScreen`), com o mesmo tratamento visual de `finalResult` usado na tela de resultado da transação.

> Abort já tem o modelo de dados pronto em `model/`, mas ainda não possui tela própria no app.

## Dependências

- Jetpack Compose (Material 3, navigation-compose, activity-compose)
- `com.google.code.gson:gson` — serialização das requisições/respostas trocadas com o DirectPIN

## Comunicação

A comunicação com o app DirectPIN é feita via `Intent`, usando `DirectPinIntentHelper` (`ui/common/DirectPinIntentHelper.kt`) para montar a requisição (JSON via Gson) e processar a resposta recebida por `ActivityResultLauncher`.

### Intent Action

```
br.com.inovare.directpin_intent.action.PROCESS
```

## Como Usar

1. Abra o projeto no Android Studio
2. Sincronize o Gradle
3. Execute o app em um dispositivo/emulador com o DirectPIN instalado

## Requisitos

- Android SDK 23+ (compileSdk 34)
- Kotlin 1.9+ / JVM 17
- DirectPIN instalado no dispositivo Android

## Notas

- A comunicação com o DirectPIN é feita através de Android Intents
- O resultado das operações é retornado via `ActivityResultLauncher`/`onActivityResult`
- Os campos de cada classe de `model/` seguem a documentação oficial "DirectPIN – Integração Intent"
