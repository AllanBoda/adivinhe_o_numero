package br.org.catolicasc.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class GreetServer {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private int numberToGuess;

    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("Servidor iniciado. Aguardando conexão...");
        String quitMessage = "";

        while (!quitMessage.equals("!quit")) {
            clientSocket = serverSocket.accept();
            System.out.println("Conexão estabelecida com o cliente.");
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            numberToGuess = generateRandomNumber();
            out.println("Digite um número entre 1 e 10:");

            gameHandler();

            quitMessage = in.readLine();
            System.out.println("Cliente encerrou a conexão.");
            break;
        }

        stop();
    }

    private void gameHandler() throws IOException {
        boolean correctGuess = false;

        while (!correctGuess) {
            String guess = in.readLine();

            try {
                int number = Integer.parseInt(guess);

                if (number == numberToGuess) {
                    out.println("Você acertou! Parabéns!");
                    correctGuess = true;
                } else if (number < numberToGuess) {
                    out.println("O número é maior. Tente novamente:");
                } else {
                    out.println("O número é menor. Tente novamente:");
                }
            } catch (NumberFormatException e) {
                out.println("Entrada inválida. Digite um número válido:");
            }

            if (!correctGuess) {
                out.println("Digite um número entre 1 e 10:");
            }
        }
    }


    private int generateRandomNumber() {
        Random random = new Random();
        return random.nextInt(10) + 1;
    }

    public void stop() {
        try {
            in.close();
            out.close();
            clientSocket.close();
            serverSocket.close();
        } catch (IOException ex) {
            System.out.println("Erro ao fechar a conexão.");
        }
    }

    public static void main(String[] args) {
        GreetServer server = new GreetServer();
        try {
            server.start(12345);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
