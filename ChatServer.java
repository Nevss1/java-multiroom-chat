import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    // Map: nome do usuário -> ClientHandler
    private static Map<String, ClientHandler> clients = new HashMap<>();
    // private Set<String> nomesAdms = new HashSet<>(); aparentemente agora vai ser inutil

    // Map: nome da sala -> Sala
    private static Map<String, Sala> salas = new HashMap<>();
    private static Map<ClientHandler, UserInfo> usuariosConectados = new HashMap<>();

    public void startServer() throws IOException { // Método de instância
        try (ServerSocket server = new ServerSocket(12345)) {
            System.out.println("Servidor iniciado na porta 12345...");
            while (true) {
                Socket socket = server.accept();
                System.out.println("Novo cliente conectado!");
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
                String[] partes = argumentos.split(" ", 2);
                String userName = partes[0];
                boolean isAdmin;

                if (userName == null || userName.trim().isEmpty()) {
                    clientHandler.sendMessage("Nome de usuario invalido.");
                    break;
                }

                clientHandler.getUserInfo().setUserName(userName);

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

                // Enviar lista de salas
                String lista = listarSalas(isAdmin);
                clientHandler.sendMessage(lista);
                break;

            case "criar":
                if (!clientHandler.getUserInfo().IsAdmin()) {
                    clientHandler.sendMessage("Apenas administradores podem criar salas.");
                    break;
                }

                String nomeSala = argumentos.trim();

                if (nomeSala.isEmpty()) {
                    clientHandler.sendMessage("Nome da sala nao pode ser vazio.");
                    break;
                }

                if (salas.containsKey(nomeSala)) {
                    clientHandler.sendMessage("Jah existe uma sala com esse nome.");
                    break;
                }

                Sala novaSala = new Sala(nomeSala);
                salas.put(nomeSala, novaSala);

                // Notificar todos os clientes da nova sala
                for (ClientHandler c : clients.values()) {
                    c.sendMessage("Nova sala criada: " + nomeSala);
                }

                clientHandler.sendMessage("Sala '" + nomeSala + "' criada com sucesso.");
                break;

            case "entrar":
                Sala sala = salas.get(argumentos.trim());
                
                if(sala == null) {
                    clientHandler.sendMessage("Sala nao encontrada.");
                    break;
                }

                Sala salaAtual = clientHandler.getUserInfo().getSalaAtual();
                if(salaAtual != null) {
                    salaAtual.sair(clientHandler);
                }

                sala.entrar(clientHandler);
                clientHandler.getUserInfo().setSalaAtual(sala);
                clientHandler.sendMessage("Voceh entrou em " + sala.getNome());    
                break;

            case "sair":
                salaAtual = clientHandler.getUserInfo().getSalaAtual();

                if (salaAtual != null) {
                    salaAtual.sair(clientHandler);
                    clientHandler.getUserInfo().setSalaAtual(null); // <- adiciona isso
                    clientHandler.sendMessage("Saiu da sala.");
                } else {
                    clientHandler.sendMessage("Entre na sala primeiro.");
                }
                break;

            case "salas":
                lista = listarSalas(clientHandler.getUserInfo().IsAdmin());
                clientHandler.sendMessage(lista);
                break;
            
            case "expulsar": 
                if(clientHandler.getUserInfo().IsAdmin()){
                    if(clientHandler.getUserInfo().getSalaAtual() != null){
                        String nomeExpulsado = argumentos.trim();
                        salaAtual = clientHandler.getUserInfo().getSalaAtual();
                        if(clients.get(nomeExpulsado) != null){
                            salaAtual.sair(clients.get(nomeExpulsado));
                            clientHandler.sendMessage(nomeExpulsado + " foi expulso da sala.");
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
                if (clientHandler.getUserInfo().getSalaAtual() != null) {
                    Sala atual = clientHandler.getUserInfo().getSalaAtual();
                    String msg = argumentos;
                    atual.broadcast("["+ clientHandler.getUserInfo().getSalaAtual().getNome() + "]" + "(" + clientHandler.getUserInfo().getUserName() + "): "  + msg); 
                    break; 
                } else {
                    clientHandler.sendMessage("Entre em uma sala primeiro.");
                }
            default:

        }
    }

    public synchronized String listarSalas(boolean adm) {
        if(salas.isEmpty()) {
            if(adm) {
                return "Ola admin, crie uma sala (/criarSala)";
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
