package br.org.catolicasc.cliente;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class GreetClient {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public void start(String ip, int port) throws IOException {
        clientSocket = new Socket(ip, port);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        System.out.println("Conexão estabelecida com o servidor.");
        String quitMessage = "";

        while (!quitMessage.equals("!quit")) {
            String message = in.readLine();
            System.out.println(message);

            if (message.contains("Parabéns")) {
                break;
            }

            Scanner scanner = new Scanner(System.in);
            String guess = scanner.nextLine();
            out.println(guess);

            String response = in.readLine();
            System.out.println(response);

            if (response.contains("Parabéns")) {
                break;
            }

            if ("!quit".equals(guess)) {
                quitMessage = "!quit";
                System.out.println("Conexão encerrada pelo cliente.");
                break;
            }
        }

        stop();
    }

    public void stop() {
        try {
            in.close();
            out.close();
            clientSocket.close();
        } catch (IOException ex) {
            System.out.println("Erro ao fechar a conexão.");
        }
    }

    public static void main(String[] args) {
        GreetClient client = new GreetClient();
        try {
            client.start("127.0.0.1", 12345);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
