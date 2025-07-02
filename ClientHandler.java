/*
    Roda em uma thread separada por cliente, lê mensagens que o cliente envia,
    Repassa a mensagem para os outros clientes conectados (broadcast),
    Cuida da entrada e saída do cliente, inclusive o nome.
    Usa synchronized pra evitar conflitos ao acessar a lista de clientes
 */

import java.io.*;
import java.net.*;
import java.util.*;

public class ClientHandler implements Runnable {
    private Socket socket;
    private PrintWriter out; // Para enviar mensagens para o cliente
    private BufferedReader in; // Para ler mensagens enviadas pelo cliente
    private UserInfo userInfo;
    private Map<String, ClientHandler> clients;
    private ChatServer chatServer; // Referência para o servidor para acessar métodos globais
    private Sala salaAtual;

    // Construtor: inicializa os recursos para comunicação com o cliente
    public ClientHandler(Socket socket, ChatServer chatServer, Map<String, ClientHandler> clients) throws IOException {
        this.socket = socket;
        this.chatServer = chatServer;
        this.clients = clients;
        this.userInfo = new UserInfo(""); // Usuário começa sem nome
        this.out = new PrintWriter(socket.getOutputStream(), true); // Envia para o cliente
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream())); // Lê do cliente
    }

    public UserInfo getUserInfo(){
        return userInfo;
    }

    public Socket getSocket(){
        return socket;
    }

    public void run() {
        try {
            sendMessage("Faca seu login. Ex: /login <username> <isAdmin (para o caso de ser admin)> ");
            
            String primeiraString;
            while ((primeiraString = in.readLine()) != null) {
                if (primeiraString.startsWith("/login")) {
                    String[] parts = primeiraString.substring(1).split(" ", 2); // Divide comando e argumentos
                    String comando = parts[0];
                    String argumentos = parts.length > 1 ? parts[1] : "";

                    // Processa o comando de login
                    chatServer.processarComando(this, comando, argumentos);

                    // Se o login foi realizado com sucesso, sai do loop
                    if (this.userInfo != null && this.userInfo.getUserName() != null && !this.userInfo.getUserName().isEmpty()) {
                        sendMessage("Login realizado com sucesso!");
                        break;
                    } else {
                        sendMessage("Login falhou, favor tentar novamente");
                    }
                } else {
                    sendMessage("Comando invalido, favor usar /login para iniciar.");
                }
            }

            String msg;
            while ((msg = in.readLine()) != null) {
                if (msg.startsWith("/")) {
                    // Se for comando, envia para o servidor processar
                    String[] partes = msg.substring(1).split(" ", 2);
                    String comando = partes[0];
                    String argumentos = partes.length > 1 ? partes[1] : "";
                    chatServer.processarComando(this, comando, argumentos);
                } else {
                    // Mensagens sem comando são rejeitadas
                    sendMessage("Mensagem invalida, favor digitar um comando valido. (Para informaçoes: /help)");
                }
            }
        } catch (IOException e) {
            // Erro na comunicação com o cliente
            System.out.println("Erro com o usuario: " + this.userInfo.getUserName());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {}

            // Remove o cliente da lista global de clientes
            synchronized (clients) {
                clients.remove(this.userInfo.getUserName());
            }
            broadcast(this.userInfo.getUserName() + " saiu do servidor.");
        }
    }

    // Envia uma mensagem para todos os outros clientes (dependendo se estiver numa sala ou não)
    private void broadcast(String msg) {
        if (salaAtual != null) {
            salaAtual.broadcast("[" + salaAtual.getNome() + "] " + msg, this);
        } else {
            synchronized (clients) {
                for (ClientHandler cliente : clients.values()) {
                    if (cliente != this) {
                        cliente.sendMessage(msg);
                    }
                }
            }
        }
        System.out.println(msg);
    }

    // Envia uma mensagem direta para este cliente
    public void sendMessage(String msg){
        if (out != null) {
            out.println(msg);
        } else {
            System.err.println("Erro ao enviar mensagem");
        }
    }
}
