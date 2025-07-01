import java.util.HashSet;
import java.util.Set;

public class Sala {
    private String nome;
    private Set<ClientHandler> clientes = new HashSet<>();

    public Sala(String nome) {
        this.nome = nome;
    }

    public synchronized void entrar(ClientHandler cliente) {
        clientes.add(cliente);
        broadcast("[" + nome + "] " + cliente.getUserName() + " entrou na sala.");
    }

    public synchronized void sair(ClientHandler cliente) {
        clientes.remove(cliente);
        broadcast("[" + nome + "] " + cliente.getUserName() + " saiu da sala.");
    }

    public synchronized void broadcast(String msg) {
        for (ClientHandler c : clientes) {
            c.sendMessage(msg);
        }
    }

    public String getNome() {
        return nome;
    }

    public synchronized boolean contemCliente(ClientHandler c) {
        return clientes.contains(c);
    }
}
