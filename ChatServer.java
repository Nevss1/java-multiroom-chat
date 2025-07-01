/*
    Servidor central do sistema
    Escuta conexões na porta 12345, aceita múltiplos clientes simultaneamente,
    Cria uma nova thread (ClienteHandler) pra cada cliente conectado,
    Mantém o controle da lista de clientes ativos (com Map<String>, ClientHandler)
 */

import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    // Map: nome do usuário -> ClientHandler
    private static Map<String, ClientHandler> clients = new HashMap<>();
    private Set<String> nomesAdms = new HashSet<>();

    // Map: nome da sala -> Sala
    private static Map<String, Sala> salas = new HashMap<>();
    private static Map<ClientHandler, UserInfo> usuariosConectados = new HashMap<>();

    public void startServer() throws IOException { // Este método NÃO é estático
        try(ServerSocket server = new ServerSocket(12345)) {
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
    
    public static void main(String[] args) throws IOException {
        try {
            ChatServer chatServer = new ChatServer(); // Cria uma instância do ChatServer
            chatServer.startServer(); // Chama o método de instância para iniciar o servidor
        } catch (IOException e) {
            System.err.println("Erro ao iniciar o servidor: " + e.getMessage());
            e.printStackTrace(); // Boa prática para ver o stack trace
        }
    }
    
    /*
    */
    public synchronized void processarComando (ClientHandler clientHandler, String comando, String argumentos){
        switch(comando){
            case "login":
                String[] partes = argumentos.split(" ", 2);
                String userName = partes[0];
                boolean IsAdmin;
                 if (userName == null || userName.trim().isEmpty()) {
                    clientHandler.sendMessage("Nome de usuário inválido.");
                    break; 
                }
                clientHandler.getUserInfo().setUserName(userName);
                if (partes.length > 1 && partes[1].equals("isAdmin")) {
                    IsAdmin = true;
                } else {
                    IsAdmin = false;
                }
                clientHandler.getUserInfo().setAdmin(IsAdmin);
                clients.put(userName, clientHandler);
                clientHandler.sendMessage("Bem vindo, " + userName + "!");
                if (IsAdmin){
                    clientHandler.sendMessage("Permissões de administrador concedidas!");
                }
        }
    }
} 

