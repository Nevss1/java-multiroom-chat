public class Utils {

    // Exibe mensagem de ajuda conforme o tipo de usuário
    public static String gerarMensagemAjuda(boolean isAdmin) {
        StringBuilder helpMsg = new StringBuilder();
        helpMsg.append("Comandos disponiveis:\n");
        helpMsg.append(" /login <nome> [isAdmin]      -> Faz login no sistema\n");
        helpMsg.append(" /salas                       -> Lista as salas disponíveis\n");
        helpMsg.append(" /entrar <nome_da_sala>       -> Entra em uma sala existente\n");
        helpMsg.append(" /sair                        -> Sai da sala atual\n");
        helpMsg.append(" /msg <mensagem>              -> Envia mensagem para a sala\n");

        if (isAdmin) {
            helpMsg.append(" /criar <nome_da_sala>        -> Cria uma nova sala\n");
            helpMsg.append(" /expulsar <usuario>          -> Expulsa um usuário da sala\n");
            helpMsg.append(" /encerrar <nome_da_sala>     -> Encerra uma sala existente\n");
        }

        helpMsg.append(" /sairServidor                -> Encerra sua conexão com o servidor\n");

        return helpMsg.toString();
    }

    // Coloquem funções utilitárias aqui pra encapsular nosso projeto
}