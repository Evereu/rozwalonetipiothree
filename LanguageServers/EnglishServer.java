package LanguageServers;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EnglishServer {
    public static final int EnglishServerPORT = 1001;
    public static final String EnglishServerCode = "EN";

    static HashMap<String, String> ENdictionary = new HashMap<String, String>() {{
        put("pies", "dog");
        put("kot", "cat");
        put("Samolot", "Airplane");
        put("Książka", "Book");
        put("Szachy", "Chess");
        put("Drzewo", "Tree");
        put("Motyl", "Butterfly");
        put("Komputer", "Computer");
        put("Plaża", "Beach");
        put("Serce", "Heart");
        put("Miasto", "City");
        put("Deszcz", "Rain");
    }};

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(EnglishServerPORT);
            sendPortAndHostToMainServer();

            while (true) {
                Socket clientSocket = serverSocket.accept();
                languageServerRequestHandler(clientSocket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void sendPortAndHostToMainServer(){
        try {
            Socket clientResponseSocket = new Socket("localhost", 5000);
            PrintWriter clientResponseOut = new PrintWriter(clientResponseSocket.getOutputStream(), true);
            clientResponseOut.println("languageServer");
            clientResponseOut.println(EnglishServerCode+":"+EnglishServerPORT);

            clientResponseSocket.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void languageServerRequestHandler(Socket socket) {
        Thread clientThread = new Thread(() -> {
            String toSend = null;
            String wordToTranslate;
            String clientAdress;
            int clientPort;

            try {
                InputStream is = socket.getInputStream();//czytanie danych z socketa -od clienta
                OutputStream os = socket.getOutputStream();
                InputStreamReader isreader = new InputStreamReader(is);//ułatwienie aby w byteach nie pracować
                OutputStreamWriter oswriter = new OutputStreamWriter(os);
                BufferedReader br = new BufferedReader(isreader);//za pomoc tych dwóch łaczymy się ze strumieniami

                System.out.println("EnglishServer tutututu");

                String[] msgFromMainServer = br.readLine().split(":");

                wordToTranslate = msgFromMainServer[0].toLowerCase();
                clientAdress = msgFromMainServer[1];
                clientPort = Integer.parseInt(msgFromMainServer[2]);

                for (String part : ENdictionary.keySet()) {
                    if (part.toLowerCase().equals(wordToTranslate)) {
                        toSend = ENdictionary.get(part);
                    }
                }

//                if (toSend == null) {
//                    toSend = null;
//                }

                // Zamknięcie strumieni i gniazda
                br.close();
                socket.close();

                Socket clientResponseSocket = new Socket(clientAdress, clientPort);
                PrintWriter clientResponseOut = new PrintWriter(clientResponseSocket.getOutputStream(), true);
                clientResponseOut.println(toSend);
                clientResponseSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        clientThread.start();
    }
}


