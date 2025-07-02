import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Servidor principal do sistema de chat
 * Gerencia conexões de clientes, salas e processamento de comandos
 */
public class ChatServer {
    // Mapas para gerenciar clientes e salas
    private static Map<String, ClientHandler> clients = new HashMap<>(); // nome -> ClientHandler
    private static Map<String, Sala> salas = new HashMap<>(); // nome da sala -> Sala
    private static Map<ClientHandler, UserInfo> usuariosConectados = new HashMap<>(); // handler -> info do usuário
    public void startServer() throws IOException {
        try (ServerSocket server = new ServerSocket(12345)) {
            System.out.println("Servidor iniciado na porta 12345...");
            while (true) {
                // Aceita nova conexão de cliente
                Socket socket = server.accept();
                System.out.println("Novo cliente conectado!");
                
                // Cria handler para o cliente e inicia thread
                ClientHandler handler = new ClientHandler(socket, this, clients);
                usuariosConectados.put(handler, handler.getUserInfo());
                new Thread(handler).start();
            }
        }
    }

    public static void main(String[] args) {
        try {
            ChatServer chatServer = new ChatServer();
            chatServer.startServer();
        } catch (IOException e) {
            System.err.println("Erro ao iniciar o servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public synchronized void processarComando(ClientHandler clientHandler, String comando, String argumentos) {
        switch (comando) {
            case "login":
                // Processa login do usuário
                String[] partes = argumentos.split(" ", 2);
                String userName = partes[0];
                boolean isAdmin;

                // Valida nome do usuário
                if (userName == null || userName.trim().isEmpty()) {
                    clientHandler.sendMessage("Nome de usuario invalido.");
                    break;
                }

                clientHandler.getUserInfo().setUserName(userName);

                // Verifica se é administrador
                if (partes.length > 1 && partes[1].equalsIgnoreCase("isAdmin")) {
                    isAdmin = true;
                } else {
                    isAdmin = false;
                }

                clientHandler.getUserInfo().setAdmin(isAdmin);
                clients.put(userName, clientHandler);
                clientHandler.sendMessage("Bem vindo, " + userName + "!");

                if (isAdmin) {
                    clientHandler.sendMessage("Permissoes de administrador concedidas!");
                }

                // Enviar lista de salas disponíveis
                String lista = listarSalas(isAdmin);
                clientHandler.sendMessage(lista);
                break;

            case "criar":
                // Só administradores podem criar salas
                if (!clientHandler.getUserInfo().IsAdmin()) {
                    clientHandler.sendMessage("Apenas administradores podem criar salas.");
                    break;
                }

                String nomeSala = argumentos.trim();

                // Valida nome da sala
                if (nomeSala.isEmpty()) {
                    clientHandler.sendMessage("Nome da sala nao pode ser vazio.");
                    break;
                }

                // Verifica se sala já existe
                if (salas.containsKey(nomeSala)) {
                    clientHandler.sendMessage("Jah existe uma sala com esse nome.");
                    break;
                }

                // Cria nova sala
                Sala novaSala = new Sala(nomeSala);
                salas.put(nomeSala, novaSala);

                // Notifica todos os clientes sobre a nova sala
                for (ClientHandler c : clients.values()) {
                    c.sendMessage("Nova sala criada: " + nomeSala);
                }

                clientHandler.sendMessage("Sala '" + nomeSala + "' criada com sucesso.");
                break;

            case "entrar":
                // Busca a sala solicitada
                Sala sala = salas.get(argumentos.trim());
                
                if(sala == null) {
                    clientHandler.sendMessage("Sala nao encontrada.");
                    break;
                }

                // Sai da sala atual (se houver)
                Sala salaAtual = clientHandler.getUserInfo().getSalaAtual();
                if(salaAtual != null) {
                    salaAtual.sair(clientHandler);
                }

                // Entra na nova sala
                sala.entrar(clientHandler);
                clientHandler.getUserInfo().setSalaAtual(sala);
                clientHandler.sendMessage("Voceh entrou em " + sala.getNome());   
                break;

            case "sair":
                // Sai da sala atual
                salaAtual = clientHandler.getUserInfo().getSalaAtual();

                if (salaAtual != null) {
                    salaAtual.sair(clientHandler);
                    clientHandler.getUserInfo().setSalaAtual(null);
                    clientHandler.sendMessage("Saiu da sala.");
                } else {
                    clientHandler.sendMessage("Entre na sala primeiro.");
                }
                break;

            case "salas":
                // Lista todas as salas disponíveis
                lista = listarSalas(clientHandler.getUserInfo().IsAdmin());
                clientHandler.sendMessage(lista);
                break;
            
            case "expulsar": 
                // Só administradores podem expulsar usuários
                if(clientHandler.getUserInfo().IsAdmin()){
                    if(clientHandler.getUserInfo().getSalaAtual() != null){
                        String nomeExpulsado = argumentos.trim();
                        salaAtual = clientHandler.getUserInfo().getSalaAtual();
                        
                        // Verifica se usuário existe
                        if(clients.get(nomeExpulsado) != null){
                            // Remove usuário da sala
                            salaAtual.sair(clients.get(nomeExpulsado));
                            clients.get(nomeExpulsado).getUserInfo().setSalaAtual(null);
                            clientHandler.sendMessage(nomeExpulsado + " foi expulso da sala.");
                            
                            // Notifica o usuário expulso
                            ClientHandler userExpulsado = clients.get(nomeExpulsado);
                            userExpulsado.sendMessage("Voce foi expulso da sala.");
                            break;
                        } else {
                            clientHandler.sendMessage("Usuario nao encontrado.");
                            break;
                        }
                    } else {
                        clientHandler.sendMessage("Favor entrar em uma sala.");
                        break;
                    }
                } else {
                    clientHandler.sendMessage("Somente admins podem usar esse comando.");
                    break;
                }   
            
            case "msg":
                // Envia mensagem para a sala atual
                if (clientHandler.getUserInfo().getSalaAtual() != null) {
                    Sala atual = clientHandler.getUserInfo().getSalaAtual();
                    String msg = argumentos;
                    // Faz broadcast da mensagem para todos na sala
                    atual.broadcast("["+ clientHandler.getUserInfo().getSalaAtual().getNome() + "]" + "(" + clientHandler.getUserInfo().getUserName() + "): "  + msg); 
                    break; 
                } else {
                    clientHandler.sendMessage("Entre em uma sala primeiro.");
                    break;
                }

            case "help": 
                // Mostra comandos disponíveis
                String mensagemAjuda = Utils.gerarMensagemAjuda(clientHandler.getUserInfo().IsAdmin());
                clientHandler.sendMessage(mensagemAjuda);
                break;

            case "encerrar":
                // Só administradores podem encerrar salas
                if (clientHandler.getUserInfo().IsAdmin()) {
                    if(salas != null){
                        nomeSala = argumentos.trim();
                        
                        // Verifica se sala existe
                        if(salas.containsKey(nomeSala)){
                            Sala salaRemovida = salas.get(nomeSala);
                            salaRemovida.broadcast("Sala sendo removida...");
                            
                            // Remove todos os usuários da sala
                            ArrayList<ClientHandler> membrosParaRemover = new ArrayList<>(salaRemovida.getMembers());
                            for (ClientHandler c : membrosParaRemover){
                                c.getUserInfo().getSalaAtual().sair(c);
                                c.getUserInfo().setSalaAtual(null);
                                c.sendMessage("Sala removida, voce foi expulso da sala.");
                            }
                            
                            // Remove sala do servidor
                            salas.remove(nomeSala);
                            clientHandler.sendMessage("Sala removida.");
                            break;
                        } else {
                            clientHandler.sendMessage("Sala nao encontrada.");
                            break;
                        }
                    } else {
                        clientHandler.sendMessage("Nao ha nenhuma sala criada.");
                        break;
                    }
                } else {
                    clientHandler.sendMessage("Voce precisa ser admin para utilizar essa funcao");
                }
            
            case "sairServidor":
                // Desconecta cliente do servidor
                clientHandler.sendMessage("Você encerrou sua conexão com o servidor.");
                try {
                    clientHandler.sendMessage("Desconectando...");
                    clientHandler.getSocket().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            default:
                // Comando não reconhecido
                clientHandler.sendMessage("Comando desconhecido: /" + comando + ". Digite /help para ver os comandos disponíveis.");
                break;                
        }
    }

    public synchronized String listarSalas(boolean adm) {
        if(salas.isEmpty()) {
            if(adm) {
                return "Ola admin, crie uma sala (/criar)";
            }
            return "Nenhuma sala criada ainda. Aguarde um administrador.";
        }

        StringBuilder sb = new StringBuilder("Salas disponiveis:\n");
        for (String nome : salas.keySet()) {
            sb.append(" - ").append(nome).append("\n");
        }
        return sb.toString();
    }
}