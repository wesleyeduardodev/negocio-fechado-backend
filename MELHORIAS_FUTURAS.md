# Melhorias Futuras

## Mensageria para Push Notifications

Atualmente as notificacoes push sao enviadas de forma sincrona no momento da acao (criar solicitacao, demonstrar interesse). Para melhorar a escalabilidade e resiliencia, considerar usar um sistema de mensageria.

**Opcoes simples:**
- **Redis Pub/Sub** - Ja seria suficiente para o MVP, facil de configurar
- **Amazon SQS** - Se estiver na AWS, simples e sem servidor para gerenciar
- **RabbitMQ** - Robusto, mas adiciona complexidade operacional

**Beneficios:**
- Desacopla o envio de notificacoes do fluxo principal
- Se o Expo Push API estiver lento/fora, nao afeta a resposta ao usuario
- Permite retry automatico em caso de falha
- Melhor escalabilidade para muitos usuarios
