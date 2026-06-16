# Guia de Configuração e Uso do AppAuth 🔐

Este projeto base já conta com toda a estrutura pronta do `AppAuth-Android` rodando com Jetpack Compose. Para conectar ao seu Provedor de Identidade (IdP) e começar a usar, siga os 3 passos simples abaixo.

---

## 1. Configurar as Credenciais no `AuthManager`

O principal arquivo que você precisa mexer é o `AuthManager.kt`. Nele estão definidos os endpoints e o Client ID que vão direcionar o usuário para o servidor correto de autenticação.

Abra o arquivo:
`app/src/main/java/com/example/appauthbase/auth/AuthManager.kt`

Localize o seguinte trecho (por volta da linha 27):
```kotlin
// TODO: Replace with your actual IdP data
private val authEndpoint = Uri.parse("https://accounts.google.com/o/oauth2/v2/auth")
private val tokenEndpoint = Uri.parse("https://oauth2.googleapis.com/token")
private val clientId = "YOUR_CLIENT_ID.apps.googleusercontent.com"
```

**O que você deve alterar:**
- **`authEndpoint`**: É a URL para onde o usuário será redirecionado para digitar login e senha (ex: endpoint de `/auth` do Keycloak, Auth0, Okta, etc).
- **`tokenEndpoint`**: É a URL que o aplicativo usa "por baixo dos panos" para trocar o código de sucesso por um Access Token (ex: endpoint `/token`).
- **`clientId`**: O identificador gerado no painel do seu Provedor de Identidade.

> **Dica (Auto-Discovery):** Se o seu provedor suportar o `.well-known/openid-configuration`, você pode apagar o `authEndpoint` e `tokenEndpoint` manuais e usar a função `AuthorizationServiceConfiguration.fetchFromIssuer(Uri.parse("Sua URL Base")) { config, ex -> ... }`.

---

## 2. Configurar o Redirect URI (Deep Link)

O "Redirect URI" é o endereço que o navegador (Custom Tabs) chama para devolver o usuário ao seu aplicativo após o login. 

Atualmente, ele está configurado como `com.example.appauthbase:/oauth2redirect`.

Se você precisar mudar esse esquema (ex: `meuapp://login` ou `com.minhaempresa.app:/callback`), você precisa alterar em **dois lugares**:

1. No `AuthManager.kt`:
```kotlin
private val redirectUri = Uri.parse("com.example.appauthbase:/oauth2redirect") // Altere aqui
```

2. No `build.gradle.kts` do módulo app (`app/build.gradle.kts`):
```kotlin
// Altere o valor do scheme se necessário:
manifestPlaceholders["appAuthRedirectScheme"] = "com.example.appauthbase"
```

---

## 3. Rodar e Testar!

1. Conecte um emulador ou dispositivo físico.
2. No Android Studio, clique no botão de **Run** (ou rode `./gradlew installDebug`).
3. Ao abrir o App, você verá um botão **"Login with Identity Provider"**.
4. Clicar no botão abrirá a aba customizada do navegador com a tela do seu servidor de autenticação.
5. Após o login correto, você voltará para o app e sua tela será atualizada automaticamente mostrando:
   - **Access Token:** O token de acesso seguro que você usará nas chamadas de API (`Bearer <Token>`).
   - Um botão de **Logout**.

## Como fazer chamadas HTTP autenticadas?

O `AuthViewModel` já possui um método chamado `performActionWithFreshTokens`. Ele verifica se o seu token ainda é válido, renova ele automaticamente (via Refresh Token) caso tenha expirado, e depois te devolve um token novinho para usar na sua requisição de rede.

Exemplo de uso:
```kotlin
authViewModel.performActionWithFreshTokens { accessToken ->
    if (accessToken != null) {
        // Chame sua API Retrofit/Ktor com o Header: "Authorization: Bearer $accessToken"
    } else {
        // O usuário não está logado ou a sessão expirou
    }
}
```

Qualquer dúvida, é só modificar o ViewModel ou a View de acordo com o design do seu aplicativo! Happy Coding! 🚀
