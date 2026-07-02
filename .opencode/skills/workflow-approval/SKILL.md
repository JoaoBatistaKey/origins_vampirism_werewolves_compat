---
name: workflow-approval
description: "Fluxo obrigatorio para todas as alteracoes: exige relatorio de aprovacao antes de qualquer mudanca e relatorio de conclusao apos. Nunca alternar do modo plano para o modo construcao sem permissao explicita do usuario. Usar em toda interacao do projeto que envolva modificacoes de arquivos, builds, ou execucao do Minecraft."
---

# Fluxo de Aprovacao (Workflow)

## Regras

1. **Portugues Brasil** — Todas as explicacoes, relatorios e comunicacao devem ser em portugues brasil.

2. **Modo Plano vs Modo Construcao** — Nunca alternar do modo `plano` para o modo `construcao` sem pedir permissao explicita do usuario primeiro.

3. **Relatorio Pre-Alteracao** — Antes de fazer qualquer modificacao (editar arquivos, escrever novos arquivos, executar builds, lancar o jogo, ou instalar ferramentas), gere um relatorio explicando:
   - O que sera alterado
   - Por que precisa ser alterado
   - Um resumo do diff ou novo conteudo
   - Riscos ou efeitos colaterais
   - **Peca permissao** antes de prosseguir.

4. **Relatorio de Conclusao** — Apos cada alteracao ser executada, gere um relatorio de:
   - O que foi realmente feito (arquivos modificados/criados/deletados)
   - O resultado (sucesso, erros, avisos)
   - Proximos passos necessarios

5. **Execucao do Minecraft** — SEMPRE pergunte ao usuario antes de iniciar o jogo (`runClient`). Nunca execute sem autorizacao.

6. **Escopo** — Estas regras se aplicam a todas as interacoes do projeto: editar codigo fonte, arquivos de recursos, scripts de build, arquivos de configuracao, executar tarefas gradle, lancar o jogo, ou instalar dependencias.
