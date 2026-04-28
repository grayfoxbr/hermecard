# OAuth2 — URLs para Teste

> **Authorization Server:** `http://localhost:8080`  
> **Resource Server:** `http://localhost:8081`  
> **Client ID:** `meu-client`  
> **Client Secret:** `meu-secret`

---

## 🔑 Basic Auth

Para gerar o header `Authorization: Basic`:

```bash
echo -n "meu-client:meu-secret" | base64
```

Cole o resultado como:
```
Authorization: Basic <resultado>
```

---

## 1. Descoberta e Chaves Públicas

### Metadata do servidor
```
GET http://localhost:8080/.well-known/openid-configuration
```

### Chaves públicas (JWKs)
```
GET http://localhost:8080/oauth2/jwks
```

---

## 2. Fluxo `client_credentials`

> Comunicação máquina a máquina, sem usuário.

### Obter token
```
POST http://localhost:8080/oauth2/token

Headers:
  Authorization: Basic <base64(meu-client:meu-secret)>
  Content-Type: application/x-www-form-urlencoded

Body:
  grant_type=client_credentials
  scope=openid
```

---

## 3. Fluxo `authorization_code`

> Login de usuário. Executar os 3 passos em ordem.

### Passo 1 — Abrir no browser para login
```
GET http://localhost:8080/oauth2/authorize
  ?response_type=code
  &client_id=meu-client
  &redirect_uri=http://localhost:8081/callback
  &scope=openid profile
  &state=abc123
```

URL completa para copiar:
```
http://localhost:8080/oauth2/authorize?response_type=code&client_id=meu-client&redirect_uri=http://localhost:8081/callback&scope=openid%20profile&state=abc123
```

### Passo 2 — Capturar o código do redirect
Após login, você será redirecionado para:
```
http://localhost:8081/callback?code=CODIGO_AQUI&state=abc123
```
Copie o valor do parâmetro `code`.

### Passo 3 — Trocar o código pelo token
```
POST http://localhost:8080/oauth2/token

Headers:
  Authorization: Basic <base64(meu-client:meu-secret)>
  Content-Type: application/x-www-form-urlencoded

Body:
  grant_type=authorization_code
  code=CODIGO_AQUI
  redirect_uri=http://localhost:8081/callback
```

---

## 4. Refresh Token

> Renovar o access_token sem login novamente.

```
POST http://localhost:8080/oauth2/token

Headers:
  Authorization: Basic <base64(meu-client:meu-secret)>
  Content-Type: application/x-www-form-urlencoded

Body:
  grant_type=refresh_token
  refresh_token=REFRESH_TOKEN_AQUI
```

---

## 5. User Info

> Retorna as claims do usuário autenticado. Requer scope `openid`.

```
GET http://localhost:8080/userinfo

Headers:
  Authorization: Bearer ACCESS_TOKEN_AQUI
```

---

## 6. Inspecionar Token (Introspect)

> Verifica se um token é válido e retorna suas claims.

```
POST http://localhost:8080/oauth2/introspect

Headers:
  Authorization: Basic <base64(meu-client:meu-secret)>
  Content-Type: application/x-www-form-urlencoded

Body:
  token=ACCESS_TOKEN_AQUI
```

---

## 7. Revogar Token

```
POST http://localhost:8080/oauth2/revoke

Headers:
  Authorization: Basic <base64(meu-client:meu-secret)>
  Content-Type: application/x-www-form-urlencoded

Body:
  token=ACCESS_TOKEN_AQUI
```

---

## 8. Resource Server — Endpoints Protegidos

> Substitua `/seu-endpoint` pelo path real da sua API.

### Sem token (deve retornar 401)
```
GET http://localhost:8081/seu-endpoint
```

### Com token válido (deve retornar 200)
```
GET http://localhost:8081/seu-endpoint

Headers:
  Authorization: Bearer ACCESS_TOKEN_AQUI
```

### Endpoint restrito a ADMIN (retorna 403 se não for admin)
```
GET http://localhost:8081/admin/seu-endpoint

Headers:
  Authorization: Bearer ACCESS_TOKEN_AQUI
```

---

## 9. Inspecionar o Token JWT

Cole o `access_token` em [https://jwt.io](https://jwt.io) e verifique se as claims estão corretas:

```json
{
  "sub": "nome-do-usuario",
  "roles": ["ROLE_USER"],
  "iss": "http://localhost:8080",
  "exp": 1714003600
}
```

---

## Respostas esperadas

| Endpoint | Situação | Status esperado |
|---|---|---|
| `/oauth2/token` | Credenciais corretas | `200 OK` |
| `/oauth2/token` | Credenciais erradas | `401 Unauthorized` |
| `/userinfo` | Token válido | `200 OK` |
| `/oauth2/introspect` | Token ativo | `200 OK` com `"active": true` |
| `/seu-endpoint` | Sem token | `401 Unauthorized` |
| `/seu-endpoint` | Token válido | `200 OK` |
| `/admin/seu-endpoint` | Role insuficiente | `403 Forbidden` |
