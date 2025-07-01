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
    private Socket socket; // Conexão servidor e cliente
    private PrintWriter out; // escreve texto em forma de saida (out)
    private BufferedReader in; // lê a mensagem linha por linha
    private String userName;
    private Map<String, ClientHandler> clients; // chave é o nome do cliente, valor é o objeto ClientHandler
    private ChatServer chatServer;

    public ClientHandler(Socket socket, ChatServer chatServer,Map<String, ClientHandler> clients) throws IOException {
        this.socket = socket;
        this.chatServer = chatServer;
        this.clients = clients;
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public void run() {
        try {
            out.println("Digite seu nome:");
            userName = in.readLine();
            synchronized (clients) {
                clients.put(userName, this);
            }
            broadcast(userName + " entrou no chat."); // envia a mensagem pra todos clientes

            String msg;
            while ((msg = in.readLine()) != null) {
                // Pra ver depois (KADU)
                if (msg.startsWith("/")){
                    String[] partes = msg.substring(1).split(" ", 2);
                    String comando = partes[0];
                    String argumentos = partes.length > 1 ? partes[1] : "";
                    chatServer.processarComando(this, comando, argumentos);
                } else {
                    broadcast(userName + ": " + msg);
                }
            }
        } catch (IOException e) {
            System.out.println("Erro com o usuário: " + userName);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {}
            synchronized (clients) {
                clients.remove(userName);
            }
            broadcast(userName + " saiu do chat.");
        }
    }

    private void broadcast(String msg) {
        synchronized (clients) {
            for (ClientHandler client : clients.values()) {
                client.out.println(msg);
            }
        }
    }

    // Processar comando?
    private void processarComando(String cmd) {
        String[] partes = cmd.split(" ", 2);
        String comando = partes[0].toLowerCase()

        try {
            switch (comando){
                case "/salas":
                    listarSalas(); // função as er implementada
                    break;
                case "/entrar":
                    entrarSala(partes[1].trim()); // entra na sala específica
                    break;
                case "/sair":
                    sairSala();
                    break;
                case "/criar":
                    if (isAdmin) { // protótipo criar um atributo booleano
                        criarSala(partes[1].trim());
                    }
            }
        }
    }
}
