package LanguageServers;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class SpanishServer {

    public static final int SpanishServerPORT = 1003;
    public static final String SpanishServerCode = "ES";

    static HashMap<String, String> ESdictionary = new HashMap<String, String>() {{
        put("pies", "perro");
        put("kot", "gato");
        put("Samolot", "Avión");
        put("Książka", "Libro");
        put("Szachy", "Ajedrez");
        put("Drzewo", "Árbol");
        put("Motyl", "Mariposa");
        put("Komputer", "Computadora");
        put("Plaża", "Playa");
        put("Serce", "Corazón");
        put("Miasto", "Ciudad");
        put("Deszcz", "Lluvia");
    }};

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(SpanishServerPORT);
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
            clientResponseOut.println(SpanishServerCode+":"+SpanishServerPORT);

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

                System.out.println("SpanishServer tutututu");

                String[] msgFromMainServer = br.readLine().split(":");

                wordToTranslate = msgFromMainServer[0].toLowerCase();
                clientAdress = msgFromMainServer[1];
                clientPort = Integer.parseInt(msgFromMainServer[2]);

                for (String part : ESdictionary.keySet()) {
                    if (part.toLowerCase().equals(wordToTranslate)) {
                        toSend = ESdictionary.get(part);
                    }
                }

//                if (toSend == null) {
//                    toSend = "Brak słowa w słowniku";
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
