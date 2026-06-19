package com.example.appauthbase.config

/**
 * CONFIGURAÇÃO DE REDE
 *
 * ⚠️  IMPORTANTE: Dispositivo físico conectado via USB NÃO usa 10.0.2.2!
 *
 * Como descobrir o IP da sua máquina:
 *   • Linux/Mac: rode `ip route get 8.8.8.8 | awk '{print $7}'` no terminal
 *   • Windows:   rode `ipconfig` e procure "IPv4 Address"
 *
 * Exemplo: se o IP da sua máquina for 192.168.1.100, defina:
 *   const val SERVER_HOST = "192.168.1.100"
 *
 * O dispositivo Android e a máquina precisam estar na MESMA rede Wi-Fi,
 * OU o servidor precisa estar acessível via USB (port-forward via adb).
 *
 * Alternativa com ADB port-forward (não precisa mudar o IP):
 *   Execute no terminal: adb reverse tcp:8080 tcp:8080
 *   Execute no terminal: adb reverse tcp:8081 tcp:8081
 *   Aí pode deixar SERVER_HOST = "localhost"
 */
object NetworkConfig {

    /**
     * IP do servidor de desenvolvimento.
     * Use "10.0.2.2" apenas para emulador.
     * Para dispositivo físico: use o IP da máquina na rede local,
     * ou use "localhost" se tiver configurado `adb reverse`.
     */
    //const val SERVER_HOST = "192.168.1.100" // ← ALTERE PARA O SEU IP

    const val SERVER_HOST = "localhost"

    const val AUTH_SERVER_PORT = 8080
    const val RESOURCE_SERVER_PORT = 8081

    const val AUTH_BASE_URL = "http://$SERVER_HOST:$AUTH_SERVER_PORT"
    const val RESOURCE_BASE_URL = "http://$SERVER_HOST:$RESOURCE_SERVER_PORT"

    // Endpoints OAuth2
    const val AUTHORIZE_URL = "$AUTH_BASE_URL/oauth2/authorize"
    const val TOKEN_URL = "$AUTH_BASE_URL/oauth2/token"

    // Client OAuth2 (deve bater com o DataInitializer do servidor)
    const val CLIENT_ID = "meu-client"
    const val REDIRECT_URI = "com.example.appauthbase:/oauth2redirect"
    const val SCOPES = "openid profile email"
}