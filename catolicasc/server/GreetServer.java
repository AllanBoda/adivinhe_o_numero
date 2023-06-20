package br.org.catolicasc.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GreetServer {
    private ServerSocket serverSocket;
    private List<Socket> clients;
    private List<PrintWriter> clientWriters;
    private List<BufferedReader> clientReaders;
    private List<Integer> numbersToGuess;
    private int currentPlayer;

    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        clients = new ArrayList<>();
        clientWriters = new ArrayList<>();
        clientReaders = new ArrayList<>();
        numbersToGuess = new ArrayList<>();
        currentPlayer = -1;

        System.out.println("Servidor iniciado. Aguardando conexão...");

        // Aguardar a conexão de dois jogadores
        while (clients.size() < 2) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("Conexão estabelecida com o cliente.");
            clients.add(clientSocket);
            clientWriters.add(new PrintWriter(clientSocket.getOutputStream(), true));
            clientReaders.add(new BufferedReader(new InputStreamReader(clientSocket.getInputStream())));
        }

        // Escolher aleatoriamente um jogador para começar
        Random random = new Random();
        currentPlayer = random.nextInt(2);

        // Jogador 1 envia o número para o Jogador 2
        int numberToGuess = generateRandomNumber();
        numbersToGuess.add(numberToGuess);
        clientWriters.get(0).println("Digite um número entre 1 e 10:");
        clientWriters.get(1).println("Seu oponente está escolhendo um número. Aguarde...");

        // Jogador 2 envia o número para o Jogador 1
        numberToGuess = generateRandomNumber();
        numbersToGuess.add(numberToGuess);
        clientWriters.get(1).println("Digite um número entre 1 e 10:");
        clientWriters.get(0).println("Seu oponente está escolhendo um número. Aguarde...");

        // Iniciar o jogo
        playGame();

        // Finalizar os servidores
        stop();
    }

    private void playGame() throws IOException {
        boolean gameFinished = false;

        while (!gameFinished) {
            int currentPlayerIndex = currentPlayer % 2;
            int opponentPlayerIndex = (currentPlayer + 1) % 2;

            String guess = clientReaders.get(currentPlayerIndex).readLine();

            if (guess.equals("!quit")) {
                break;
            }

            try {
                int number = Integer.parseInt(guess);

                if (number == numbersToGuess.get(opponentPlayerIndex)) {
                    clientWriters.get(currentPlayerIndex).println("Você acertou! Parabéns!");
                    clientWriters.get(opponentPlayerIndex).println("Seu oponente acertou! Você perdeu.");
                    gameFinished = true;
                } else if (number < numbersToGuess.get(opponentPlayerIndex)) {
                    clientWriters.get(currentPlayerIndex).println("O número é maior. Tente novamente:");
                } else {
                    clientWriters.get(currentPlayerIndex).println("O número é menor. Tente novamente:");
                }
            } catch (NumberFormatException e) {
                clientWriters.get(currentPlayerIndex).println("Entrada inválida. Digite um número válido:");
            }

            currentPlayer++;
        }
    }

    private int generateRandomNumber() {
        Random random = new Random();
        return random.nextInt(10) + 1;
    }

    public void stop() {
        try {
            for (PrintWriter writer : clientWriters) {
                writer.close();
            }
            for (BufferedReader reader : clientReaders) {
                reader.close();
            }
            for (Socket client : clients) {
                client.close();
            }
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
