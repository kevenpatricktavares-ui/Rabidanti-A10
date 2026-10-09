# Rabidanti Mobile Companion — Samsung Galaxy A10

Target principal: Samsung Galaxy A10 (SM-A105F/DS), Android 9–11.

Este projeto **não duplica o Core Python**. O APK é uma camada Android segura sobre o Rabidanti v5.9 executado no Termux. O pacote v5.9 original está embutido em `app/src/main/assets/` e pode ser exportado pelo próprio APK.

## Segurança
- Sala fixa em `http://127.0.0.1:8765/ui/`.
- WebView bloqueia navegação fora de localhost.
- Sem permissões de armazenamento amplo, contactos, SMS, localização, microfone ou câmara.
- Sem execução automática de ações externas.
- Sem persistência de token na app.
- `allowBackup=false`.

## Compatibilidade
- `minSdk 28` = Android 9.
- `targetSdk 35`.
- Código Java puro, sem bibliotecas nativas: APK universal, apropriado para o A10.

## Build
Requer Android SDK + Build Tools 35 e Gradle 8.10.2. Em GitHub Actions, execute `Build Rabidanti Android APK`.
