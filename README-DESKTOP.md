# Holy Financial - Versão Desktop Windows

Aplicativo de gerenciamento de finanças para igrejas, agora disponível para Windows Desktop com suporte offline completo.

## Características

✅ **Interface Moderna**: Desenvolvido com Jetpack Compose Multiplatform  
✅ **Funciona Offline**: Todos os dados armazenados localmente  
✅ **Instalação Fácil**: Instaladores .exe e .msi nativos do Windows  
✅ **Persistência de Dados**: SQLite para armazenamento robusto  
✅ **Temas Personalizáveis**: 4 cores de tema diferentes  
✅ **Gerenciamento de Finanças**:
- 📊 Dashboard com resumo de receitas e despesas
- 💰 Registrar receitas de diferentes fontes
- 💸 Registrar despesas por categoria
- 📈 Visualizar histórico de transações
- 🎨 Personalizar tema da aplicação

## Requisitos do Sistema

- **Windows**: 7 ou superior (64-bit ou 32-bit)
- **Java Runtime**: JRE 11 ou superior (incluído nos instaladores)
- **RAM**: Mínimo 2GB recomendado
- **Espaço em Disco**: ~200MB

## Instalação Rápida

### Opção 1: Instalador EXE (Recomendado)

1. Baixar `Holy Financial-1.0.0.exe`
2. Duplo clique para executar
3. Seguir instruções de instalação
4. O aplicativo será adicionado ao Menu Iniciar

### Opção 2: Instalador MSI

```bash
msiexec /i "Holy Financial-1.0.0.msi" /qb
```

### Opção 3: Build Personalizável

Ver [DESKTOP_BUILD.md](./DESKTOP_BUILD.md) para instruções de build customizado.

## Guia de Uso

### Abas Principais

**Dashboard**
- Visualizar resumo de receitas totais
- Ver despesas totais
- Acompanhar saldo disponível

**Receitas**
- Clique em "Adicionar Receita"
- Preencha: Origem, Descrição, Valor e Data
- Clique em "Adicionar" para salvar

**Despesas**
- Clique em "Adicionar Despesa"
- Preencha: Categoria, Descrição, Valor e Data
- Clique em "Adicionar" para salvar

**Configurações**
- Escolha entre 4 temas diferentes
- Preferências são salvas automaticamente

## Armazenamento de Dados

Todos os dados são salvos localmente em:

```
C:\Users\{seu-usuario}\.holy-financial\
```

Backup automático é realizado regularmente.

## Desinstalação

### Via Painel de Controle

1. Abrir: Painel de Controle → Programas → Programas e Recursos
2. Localizar "Holy Financial"
3. Clicar em "Desinstalar"

### Via Linha de Comando

```bash
wmic product where name="Holy Financial" call uninstall
```

## Troubleshooting

**Aplicativo não inicia**
- Verificar se Java está instalado: `java -version`
- Verificar logs em `C:\Users\{user}\.holy-financial\logs\`
- Reinstalar a aplicação

**Dados foram perdidos**
- Verificar pasta: `C:\Users\{user}\.holy-financial\`
- Se vazia, dados não foram sincronizados
- Neste caso, não há recuperação possível

**Erro de permissão durante instalação**
- Executar instalador como Administrador
- Desativar antivírus temporariamente

## Atualização

Para atualizar para uma versão mais recente:

1. Download do novo instalador
2. Executar novo instalador (dados serão preservados)
3. Selecionar "Upgrade" quando perguntado

## Desenvolvimento e Contribuição

Ver [CONTRIBUTING.md](./CONTRIBUTING.md) para instruções de desenvolvimento.

## Licença

MIT License - Ver [LICENSE](./LICENSE) para detalhes.

## Suporte

Para reportar bugs ou solicitar features:
- Abrir issue no GitHub: https://github.com/LukeTwister2000/holy-financial/issues
- Enviar email: support@holyfinancial.com

## Changelog

### v1.0.0 (2026-07-04)
- ✨ Lançamento inicial da versão Desktop Windows
- 🎨 Interface moderna com Compose Multiplatform
- 💾 Suporte completo offline
- 📦 Instaladores .exe e .msi
- 🔧 Gerenciamento completo de finanças

---

**Desenvolvido com ❤️ para Igrejas**
