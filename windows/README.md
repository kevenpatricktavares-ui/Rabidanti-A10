# Rabidanti — Windows

## Estado desta versão

Esta pasta gera um **instalador Windows (.exe)** para um lançador gráfico do Rabidanti. O lançador é real e compilado para Windows, mas **não é ainda o Core completo do Rabidanti**.

O projeto atual do repositório contém um companion Android e um runtime específico para Android/Termux. Não foi encontrada evidência de que esse runtime seja executável no Windows, por isso esta versão não o copia nem tenta executá-lo indevidamente.

## Compilação automatizada

1. Abra **Actions → Build Rabidanti Windows Installer** no GitHub.
2. Execute **Run workflow** na branch `windows-installer` ou faça push de alterações em `windows/`.
3. Aguarde os passos de compilação do `Rabidanti.exe`, validação do ficheiro e criação do instalador Inno Setup.
4. Transfira `Rabidanti-Windows-Setup.exe` em **Artifacts** do workflow.

## O que o instalador faz

- Instala um lançador gráfico para Windows.
- Verifica se existe um script de arranque do Core Windows.
- Só inicia o runtime quando este estiver presente.
- Mostra uma mensagem clara quando o Core Windows ainda não está disponível.

## Próxima etapa necessária para a versão completa

Adaptar e validar o Core Python para Windows: dependências, caminhos de ficheiros, processos/serviços, armazenamento, inicialização e testes. O script esperado pelo lançador é:

`%LOCALAPPDATA%\Rabidanti\windows-runtime\START-RABIDANTI-WINDOWS.ps1`

Não copie os scripts Android/Termux para esse local: é necessário criar e testar uma implementação Windows específica primeiro.
