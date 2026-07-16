# SytemUpdate v2 — modo mínimo de consentimento

Este projeto foi ajustado para manter o serviço ativo com a notificação obrigatória do Android no modo mais discreto possível.

## Fluxo depois de instalar

1. Depois de instalar o APK, nada roda automaticamente ainda. O Android não permite execução imediata antes da primeira abertura do app.
2. Na primeira abertura, aparece somente uma tela mínima de consentimento com o botão **Permitir e iniciar**.
3. No Android 13 ou superior, o sistema também pode exibir a permissão de notificações.
4. Após aceitar, o app:
   - ativa o auto-início;
   - agenda o WorkManager periódico;
   - agenda uma verificação extra inicial;
   - inicia o Foreground Service;
   - mostra uma notificação fixa obrigatória, silenciosa e de baixa prioridade;
   - fecha a tela automaticamente.
5. Depois disso, ao abrir o app novamente pelo ícone ou pela notificação, ele apenas garante que o serviço está agendado/iniciado e fecha a tela automaticamente.

## O que foi ajustado na notificação discreta

- Canal novo: `sytemupdate_channel_v3`, para o Android aplicar as novas configurações mesmo em atualização.
- Importância mínima no canal de notificação.
- Sem som, sem vibração e sem badge.
- Sem horário exibido.
- Sem botão **Parar**.
- Texto curto: **Serviço ativo** / **Executando em segundo plano**.
- Visibilidade reduzida na tela bloqueada.

## Como parar

A notificação fixa não possui mais a ação **Parar**.

Para interromper o app, use as configurações do Android, por exemplo em **Configurações > Apps > SytemUpdate > Forçar parada** ou removendo permissões do app.

## Limites do Android

- Nenhum app comum inicia sozinho imediatamente após instalação sem ser aberto pelo usuário.
- Foreground Service precisa manter notificação visível, mas ela foi configurada como silenciosa, sem vibração, sem badge, sem horário e com prioridade mínima.
- Android 13+ exige permissão de notificações.
- Se o usuário tocar em **Forçar parada** nas configurações do Android, boot receiver e WorkManager ficam bloqueados até o app ser aberto manualmente outra vez.
