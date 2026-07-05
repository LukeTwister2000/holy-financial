# Guia de Build Desktop - Holy Financial

## Requisitos do Sistema

- **Java**: JDK 11 ou superior
- **Gradle**: 7.0 ou superior
- **Windows**: Windows 7 ou superior (para gerar instaladores .msi)
- **WiX Toolset**: 3.11 ou superior (para gerar .msi) - [Download](https://github.com/wixtoolset/wix3/releases)

## Instalação de Dependências

### 1. Instalar WiX Toolset (para gerar .msi)

```bash
# Baixar e instalar WiX 3.11
choco install wixtoolset -y
# ou
msiexec /i wix311.msi
```

## Build do Aplicativo

### Opção 1: Build Completo (Exe + Msi)

```bash
# Na raiz do projeto
./gradlew :desktop:packageDistributionForCurrentOS
```

### Opção 2: Apenas Executável

```bash
./gradlew :desktop:createExe
```

### Opção 3: Apenas MSI

```bash
./gradlew :desktop:createMsi
```

### Opção 4: Executar Aplicativo (Modo Desenvolvimento)

```bash
./gradlew :desktop:run
```

## Localização dos Arquivos Gerados

Após o build bem-sucedido, os instaladores estarão em:

```
desktop/build/compose/binaries/main/
├── exe/
│   ├── Holy Financial-1.0.0.exe
│   └── [outros arquivos]
└── msi/
    ├── Holy Financial-1.0.0.msi
    └── [outros arquivos]
```

## Instalação do Aplicativo

### Via EXE

1. Duplo clique em `Holy Financial-1.0.0.exe`
2. Seguir o assistente de instalação
3. Escolher diretório de instalação (padrão: `C:\Program Files\Holy Financial`)

### Via MSI

```bash
msiexec /i "Holy Financial-1.0.0.msi" /qn
# ou com interface gráfica
msiexec /i "Holy Financial-1.0.0.msi" /qb
```

## Desinstalação

### Via Painel de Controle

1. Abrir Painel de Controle → Programas → Programas e Recursos
2. Localizar "Holy Financial"
3. Clicar em "Desinstalar"

### Via Linha de Comando

```bash
msiexec /x {PRODUCT-GUID} /qn
```

## Configuração Offline

O aplicativo foi configurado para funcionar offline:

- **Banco de Dados Local**: SQLite em `%APPDATA%/.holy-financial/`
- **Dados Sincronizados**: Quando conectado à internet (opcional)
- **Cache**: Todos os dados principais armazenados localmente

## Diretório de Dados

Os dados da aplicação são armazenados em:

```
C:\Users\{username}\.holy-financial\
├── data.json       # Dados das transações
├── prefs.txt       # Preferências do usuário
└── logs/           # Arquivo de logs
```

## Solução de Problemas

### Erro: "WiX Toolset não encontrado"

Certifique-se de que WiX está instalado e adicionado ao PATH:

```bash
wix -version
```

Se não funcionar, adicione manualmente ao PATH:
- Windows 64-bit: `C:\Program Files (x86)\WiX Toolset v3.11\bin`

### Erro: "Permissão negada durante instalação"

Execute o instalador como Administrador:

```bash
Start-Process -FilePath ".\Holy Financial-1.0.0.exe" -Verb RunAs
```

### Aplicativo não inicia

1. Verificar logs em `%APPDATA%/.holy-financial/logs/`
2. Verificar se o Java está instalado: `java -version`
3. Tentar reinstalar o aplicativo

## Desenvolvimento

### Estrutura do Módulo Desktop

```
desktop/
├── build.gradle.kts           # Configuração do build
├── LICENSE.txt                 # Arquivo de licença
├── src/
│   └── jvmMain/
│       ├── kotlin/
│       │   └── com/example/desktop/
│       │       ├── Main.kt              # Ponto de entrada
│       │       └── ui/
│       │           ├── ChurchAppScreen.kt   # Tela principal
│       │           ├── ChurchViewModel.kt   # ViewModel
│       │           └── theme/
│       │               └── Theme.kt
│       └── resources/
│           ├── icon.ico              # Ícone da aplicação
│           └── application.properties # Propriedades
```

### Adicionar Novas Dependências

Editar `desktop/build.gradle.kts` e adicionar no bloco `jvmMain`:

```kotlin
sourceSets {
  val jvmMain by getting {
    dependencies {
      implementation("com.example:library:1.0.0")
    }
  }
}
```

## Variáveis de Ambiente

Para build/deployment em CI/CD, configure:

```bash
JAVA_HOME=C:\Program Files\Java\jdk-11
GRADLE_HOME=C:\gradle-7.0
WIX_HOME=C:\Program Files (x86)\WiX Toolset v3.11
```

## Publicação e Distribuição

### Preparar Release

1. Gerar instaladores:
```bash
./gradlew :desktop:packageDistributionForCurrentOS
```

2. Assinar executável (opcional):
```bash
signtool sign /f certificate.pfx /p password /t http://timestamp.server.com Holy Financial-1.0.0.exe
```

3. Fazer upload para servidor de distribuição

## Suporte

Para issues ou dúvidas, abra uma issue no repositório: https://github.com/LukeTwister2000/holy-financial/issues
