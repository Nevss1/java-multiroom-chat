/*
    Conecta ao servidor (IP e porta)
    Envia mensagens que o user digita
    Escuta mensagens vindas do servidor e as imprime no terminal
    Roda uma thread pra ficar escutando mensagens enquanto o usuário digita
 */

import java.io.*;
import java.net.*;

public class ChatClient {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("26.159.125.196", 12345); // Substitua pelo IP do Radmin do servidor
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        Thread receive = new Thread(() -> {
            String msg;
            try {
                while ((msg = in.readLine()) != null) {
                    System.out.println(msg);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        receive.start();

        while (true) {
            String msg = input.readLine();
            out.println(msg);
        }
    }
}
