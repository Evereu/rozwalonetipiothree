package zad1;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Objects;


//Musi być klientem i serwerem bo ma odebrać żądanie i przekazać do serwera języków


public class MainServer {

    public static final int PORT = 5000;
    private static final String SERVER_NAME = "localhost";

    static BufferedReader br;
    static BufferedWriter bw;

//    public static HashMap<String, Integer> languageServersDic = new HashMap<String, Integer>() {{
//        put("EN", 1001);
//        put("DE", 1002);
//        put("ES", 1003);
//        put("FR", 1004);
//    }};

    public static HashMap<String, Integer> languageServersDic = new HashMap<>();

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            while (true) {
                Socket clientSocket = serverSocket.accept();

                String connectionType = setConnectionType(clientSocket);

                if(Objects.equals(connectionType, "languageServer")){
                    String[] newServer = br.readLine().split(":");

                    languageServersDic.put(newServer[0], Integer.parseInt(newServer[1]));

                    System.out.println("Dodano nowy serwer jezykowy: "+newServer[0] +" "+ newServer[1]);
                    br.close();
                    clientSocket.close();
                }

                if(Objects.equals(connectionType, "client")){
                    System.out.println("aaaaaa");
                    requestMainServerHandlerThread(clientSocket);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String setConnectionType(Socket socket) {
        String connectionType = null;
        try {

            InputStream is = socket.getInputStream(); // Czytanie danych ze socketa - od klienta
            OutputStream os = socket.getOutputStream();
            br = new BufferedReader(new InputStreamReader(is)); // Za pomocą tych dwóch łączymy się ze strumieniami
            bw = new BufferedWriter(new OutputStreamWriter(os));

            connectionType = br.readLine();
            bw.newLine();
            bw.flush();
            System.out.println("connectionType: " + connectionType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return connectionType;
    }


    //{"polskie słowo do przetłumaczenia", adres klienta, port na którym klient czeka na wynik}


    public static void requestMainServerHandlerThread(Socket socket) {
        Thread clientThread = new Thread(() -> {
            String clientServerPort;
            String word;
            String lang;

            try {
                // Akceptowanie połączeń klientów

                InputStream is = socket.getInputStream(); // Czytanie danych ze socketa - od klienta
                OutputStream os = socket.getOutputStream();
                br = new BufferedReader(new InputStreamReader(is)); // Za pomocą tych dwóch łączymy się ze strumieniami
                bw = new BufferedWriter(new OutputStreamWriter(os));

                for(String part : languageServersDic.keySet()){

                    System.out.println(  languageServersDic.get(part));
                    System.out.println(  languageServersDic.keySet());
                }


                String[] msgFromClient = br.readLine().split(":");
                word = msgFromClient[0];
                lang = msgFromClient[1].toUpperCase();
                clientServerPort = msgFromClient[2];

                System.out.println("jestem tutaj");

                for (String part : msgFromClient) {
                    log(part);
                }

                log("Odczytany kod języka przez MainServer: " + lang);

                String langserv = languageServersDic.get(lang).toString();

                String clientAdress = socket.getInetAddress().getHostAddress().replace("/", "-");

                if (languageServersDic.containsKey(lang)) {
                    bw.write(languageServersDic.get(lang).toString());
                    bw.newLine();
                    bw.flush();


                    InetAddress serverIp = InetAddress.getByName(SERVER_NAME); // Tłumaczenie adresu serwera na adres IP
                    Socket clientResponseSocket = new Socket(serverIp, Integer.parseInt(langserv));
                    PrintWriter clientResponseOut = new PrintWriter(clientResponseSocket.getOutputStream(), true);
                    clientResponseOut.println(word + ":" + clientAdress + ":" + clientServerPort);
                    clientResponseSocket.close();

                    br.close();
                    socket.close();
                }
                br.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        clientThread.start();
    }

    public static void log(String message) {
        System.out.println("[MainServer]:  " + message);
        System.out.flush();
    }
}




















