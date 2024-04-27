package LanguageServers;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class FrenchServer {

    public static final int FrenchServerPORT = 1004;

    public static final String FrenchServerCode = "FR";


    static HashMap<String, String> FRdictionary = new HashMap<String, String>() {{
        put("pies", "chien");
        put("kot", "chat");
        put("Samolot", "Avion");
        put("Książka", "Livre");
        put("Szachy", "Échecs");
        put("Drzewo", "Arbre");
        put("Motyl", "Papillon");
        put("Komputer", "Ordinateur");
        put("Plaża", "Plage");
        put("Serce", "Cœur");
        put("Miasto", "Ville");
        put("Deszcz", "Pluie");
    }};

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(FrenchServerPORT);

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
            clientResponseOut.println(FrenchServerCode+":"+FrenchServerPORT);

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

                System.out.println("FrenchServer tutututu");

                String[] msgFromMainServer = br.readLine().split(":");

                wordToTranslate = msgFromMainServer[0].toLowerCase();
                clientAdress = msgFromMainServer[1];
                clientPort = Integer.parseInt(msgFromMainServer[2]);

                for (String part : FRdictionary.keySet()) {
                    if (part.toLowerCase().equals(wordToTranslate)) {
                        toSend = FRdictionary.get(part);
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
