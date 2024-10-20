# yasDenuncias - Plugin Denuncias

## Visão Geral
O **yasDenuncias** é um plugin para servidores Bukkit/Spigot do Minecraft 1.8.8, que permite que jogadores denunciem comportamentos inadequados de outros jogadores. Com um sistema de comandos intuitivo e notificações para a equipe de moderação, este plugin ajuda a manter a ordem e a justiça no servidor.

## Funcionalidades
- **Denúncias de Jogadores**: Permite que jogadores reportem outros por comportamentos inadequados.
- **Notificações para Staff**: Notificações instantâneas para a equipe sobre novas denúncias.
- **Cooldown para Denúncias**: Tempo de espera para evitar spam de denúncias.
- **Menu Intuitivo**: Interface gráfica para gerenciar denúncias.

## Instalação
1. Baixe a versão mais recente do arquivo JAR do plugin Denuncias.
2. Coloque o arquivo JAR na pasta `plugins` do seu servidor Spigot ou Bukkit.
3. Inicie seu servidor para carregar o plugin.
4. Coloque o LuckPerms (da versão 5.4) na pasta `plugins` do seu servidor Spigot ou Bukkit.

## Comandos
### Denunciar
- **Comando**: `/denuncias <jogador> <motivo>`
- **Permissão**: `denuncias.report`
- **Descrição**: Denuncia outro jogador por um motivo específico.
  
### Menu de Denúncias
- **Comando**: `/menudenuncias`
- **Permissão**: `denuncias.open`
- **Descrição**: Abre um menu GUI para gerenciar denúncias.

## Permissões
| Permissão            | Descrição                                        |
|---------------------|--------------------------------------------------|
| `denuncias.report`  | Permite que um jogador denuncie outros jogadores.|
| `denuncias.open`    | Permite que um jogador abra o menu de denúncias.|
| `denuncias.notify`  | Permite que membros da equipe recebam notificações sobre novas denúncias. |

## Cooldown de Denúncias
Os jogadores têm um cooldown de 1 minuto entre as denúncias. Se um jogador tentar denunciar antes que o cooldown termine, ele receberá uma mensagem informando o tempo restante.

## Notificações
A equipe com a permissão `denuncias.notify` será notificada sempre que uma nova denúncia for feita, incluindo detalhes do denunciante, do denunciado e do motivo.

## Contribuições
Contribuições são bem-vindas! Se você quiser ajudar a melhorar o plugin, sinta-se à vontade para fazer um fork do repositório e enviar um pull request.

## Suporte
Para dúvidas ou problemas, entre em contato com a equipe de suporte do plugin ou consulte a comunidade.

## Licença
Este plugin está licenciado sob a Licença MIT. Consulte o arquivo LICENSE incluído na JAR para mais detalhes.
