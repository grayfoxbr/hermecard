# Hermecard Android App

App Android que implementa login OAuth2 Authorization Code com o `hermecard-auth-api-server`
e consome endpoints protegidos da `hermecard-resource-api`.

---

## Arquitetura

```
┌─────────────────────┐     Authorization Code Flow     ┌───────────────────────────────┐
│   Android App       │ ──────────────────────────────► │  hermecard-auth-api-server    │
│   (este projeto)    │ ◄─────────────────────────────  │  localhost:8080               │
│                     │        access_token              │  Spring Authorization Server  │
│                     │                                  └───────────────────────────────┘
│                     │     Bearer Token                 ┌───────────────────────────────┐
│                     │ ──────────────────────────────► │  hermecard-resource-api       │
│                     │ ◄─────────────────────────────  │  localhost:8081               │
│                     │     dados protegidos             │  Spring WebFlux + JWT         │
└─────────────────────┘                                  └───────────────────────────────┘
```

---

## Pré-requisitos

- Android Studio Hedgehog ou superior
- JDK 11+
- Android SDK 26+ (minSdk)
- Os dois servidores rodando localmente

---

## ⚙️ Configuração necessária no servidor

### 1. Adicionar o redirect URI do Android no `DataInitializer.java`

O app Android usa o scheme customizado `com.angels.hermecard://callback`.
Adicione-o no servidor junto com os outros redirect URIs:

```java
// Em DataInitializer.java, na configuração do client:
.redirectUri("http://localhost:8081/login/oauth2/code/meu-client")
.redirectUri("http://localhost:8081/callback")
.redirectUri("com.angels.hermecard://callback")   // ← ADICIONAR ESTA LINHA
.postLogoutRedirectUri("http://localhost:8081/logout")
```

> **Por que?** O servidor OAuth2 só aceita redirect URIs pré-registrados por segurança.
> O scheme customizado permite que o Android capture o redirect sem precisar de um servidor HTTP.

### 2. Permitir CORS (opcional, para testes diretos)

Se necessário, adicione CORS na Resource API para aceitar requisições do emulador.

---

## 📱 Configuração do app

### Endereços IP (OAuthConfig.java)

| Ambiente         | IP a usar            |
|-----------------|---------------------|
| Emulador Android | `10.0.2.2`          |
| Dispositivo real | IP local da máquina  |
| Produção         | domínio real         |

Edite `app/src/main/java/com/angels/hermecard/auth/OAuthConfig.java`:

```java
public static final String AUTH_SERVER_BASE = "http://10.0.2.2:8080";   // emulador
public static final String RESOURCE_API_BASE = "http://10.0.2.2:8081";  // emulador

// Para dispositivo real na mesma rede Wi-Fi:
// public static final String AUTH_SERVER_BASE = "http://192.168.1.X:8080";
```

---

## 🚀 Rodando o projeto

### 1. Inicie os servidores

```bash
# Terminal 1 — Auth Server (porta 8080)
cd hermecard-auth-api-server
./mvnw spring-boot:run

# Terminal 2 — Resource API (porta 8081)
cd hermecard-resource-api
./mvnw spring-boot:run
```

### 2. Abra o projeto Android no Android Studio

```
File → Open → selecione a pasta hermecard-android
```

### 3. Sincronize o Gradle e rode no emulador

- `Build → Sync Project with Gradle Files`
- Selecione um emulador API 26+
- Clique em ▶ Run

---

## 🔐 Fluxo de login passo a passo

1. App abre a **LoginActivity**
2. Usuário toca **"Entrar com Hermecard"**
3. App abre um **Custom Tab** (navegador embutido) no Auth Server:
   ```
   http://10.0.2.2:8080/oauth2/authorize?
     client_id=meu-client&
     response_type=code&
     redirect_uri=com.angels.hermecard://callback&
     scope=openid profile read write
   ```
4. Auth Server redireciona para a tela de login HTML (`login.html`)
5. Usuário entra com `admin` / `123456`
6. Auth Server redireciona para:
   ```
   com.angels.hermecard://callback?code=XXXX
   ```
7. Android captura o redirect via `RedirectUriReceiverActivity` (AppAuth)
8. App troca o `code` por `access_token` + `refresh_token` + `id_token`
9. Tokens salvos em `EncryptedSharedPreferences`
10. App navega para **HomeActivity**

---

## 📲 Funcionalidades do app

### LoginActivity
- Detecta sessão existente (token salvo) → vai direto para Home
- Detecta token expirado → tenta refresh automático
- Inicia o fluxo OAuth2 via AppAuth

### HomeActivity
- **Exibe dados do usuário** decodificando o ID Token (JWT)
  - Nome, e-mail, perfil (role), subject (ID)
- **GET /public/hello** → endpoint sem autenticação
- **GET /privado/hello** → com Bearer token → retorna `"olá, admin"`
- **GET /privado/claims** → retorna todos os claims do JWT
- **Menu → Info do Token** → mostra preview do access token
- **Menu → Sair** → limpa tokens e volta para Login

---

## 🏗️ Estrutura do projeto

```
app/src/main/java/com/angels/hermecard/
├── auth/
│   ├── OAuthConfig.java        # URLs e credenciais do servidor OAuth2
│   ├── AuthManager.java        # Gerencia o fluxo AppAuth
│   └── TokenManager.java       # Persiste tokens com EncryptedSharedPreferences
├── network/
│   ├── ResourceApiService.java # Interface Retrofit com os endpoints
│   └── ApiClient.java          # Singleton Retrofit com Bearer token interceptor
└── ui/
    ├── login/
    │   └── LoginActivity.java  # Tela de login OAuth2
    └── home/
        └── HomeActivity.java   # Tela principal com dados e testes de API
```

---

## 📦 Dependências principais

| Biblioteca         | Versão  | Função                                    |
|-------------------|---------|-------------------------------------------|
| AppAuth Android    | 0.11.1  | OAuth2/OIDC Authorization Code flow       |
| Retrofit2          | 2.9.0   | Cliente HTTP para a Resource API          |
| OkHttp3            | 4.12.0  | HTTP + logging interceptor                |
| Security Crypto    | 1.1.0   | EncryptedSharedPreferences para os tokens |
| Material3          | 1.11.0  | UI components                             |

---

## 🐛 Troubleshooting

| Problema | Causa provável | Solução |
|----------|---------------|---------|
| Tela branca após login | Redirect URI não registrado | Adicionar `com.angels.hermecard://callback` no DataInitializer |
| `Connection refused` | Servidor não está rodando | Iniciar os dois servidores |
| `401 Unauthorized` | Token expirado | Fazer logout e login novamente |
| Custom Tab não abre | Nenhum navegador no emulador | Instalar Chrome no emulador ou usar dispositivo real |
| `cleartext not permitted` | HTTP bloqueado no Android 9+ | `android:usesCleartextTraffic="true"` já está no Manifest |
