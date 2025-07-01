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

            case "salas":
                if (salas.isEmpty()) {
                    clientHandler.sendMessage("Nenhuma sala criada ainda.");
                } else {
                    StringBuilder sb = new StringBuilder("Salas disponiveis:\n");
                    for (Sala s : salas.values()) {
                        sb.append(" - ").append(s.getNome()).append("\n");
                    }
                    clientHandler.sendMessage((sb.toString()));
                }
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

                if (sala == null) {
                    clientHandler.sendMessage("Sala nao encontrada.");
                    break;
                }

                Sala salaAtual = clientHandler.getSalaAtual();
                if (salaAtual != null) {
                    salaAtual.sair(clientHandler);
                }

                sala.entrar(clientHandler);
                clientHandler.setSalaAtual(sala);
                clientHandler.sendMessage("Voceh entrou na sala " + sala.getNome());
                break;  // faltava o break aqui!

            case "sair":
                salaAtual = clientHandler.getSalaAtual();

                if (salaAtual != null) {
                    salaAtual.sair(clientHandler);
                    clientHandler.setSalaAtual(null);
                    clientHandler.sendMessage("Saiu da sala");
                } else {
                    clientHandler.sendMessage("Nao esta em nenhuma sala");
                }
                break;

            case "help":
                String mensagemAjuda = Utils.gerarMensagemAjuda(clientHandler.getUserInfo().IsAdmin());
                clientHandler.sendMessage(mensagemAjuda);
                break;
                
            default:
                clientHandler.sendMessage("Comando desconhecido. Use /help para ajuda.");
                break;
        }
    }

    public synchronized String listarSalas(boolean adm) {
        if (salas.isEmpty()) {
            if (adm) {
                return "Olah admin, crie uma sala (/criar)";
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
