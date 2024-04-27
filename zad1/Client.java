package zad1;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;


public class Client extends Application {

    private static final int MAIN_SERVER_PORT = 5000;
    private static final String SERVER_NAME = "localhost";

    private static final int CLIENT_SERVER_PORT = 1500;

    static Socket clientSocket;
    static BufferedWriter bw;
    static BufferedReader br;

    public static void main(String[] args) {

        launch(args);


    }

    public String clientLogic(String inputWordToTranslate, String inputLanguageCode) {

        String translateToReturn = null;

//        Scanner scanner = new Scanner(System.in);
//
//        log("Podaj słowo do przetłumaczenia:");
//        inputWordToTranslate = scanner.nextLine();
//
//        log("Podaj jezyk na który chcesz przetłumaczyć np. EN, DE, ES, FR:");
//        inputLanguageCode = scanner.nextLine().toUpperCase();
//
//        log("Przetlumacz slowo: " + inputWordToTranslate);
//        log("Jezyk: " + inputLanguageCode);
//
//        scanner.close();

        // TODO Klient ma odpytać serwer główny o dostępnym porcie dla danego języka
        //  Jeśli taki język istnieje zwraca port do serwera językowego




            try {
                InetAddress serverIp = InetAddress.getByName(SERVER_NAME);//tłumaczenie adresu servera na adresIP

                clientSocket = new Socket(serverIp, MAIN_SERVER_PORT);
                bw = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())); //Wysyłka do serwera
                br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); //Odczyt z serwera

                bw.write("client");
                bw.newLine();
                bw.flush();

                String query = String.format(inputWordToTranslate + ":" + inputLanguageCode + ":" + CLIENT_SERVER_PORT);
                System.out.println(inputWordToTranslate + ":" + inputLanguageCode + ":" + CLIENT_SERVER_PORT);
                bw.write(query);
                bw.newLine();
                bw.flush();

                System.out.println("client po sendWord");

                String isLanguageServerExist = br.readLine();

                if (isLanguageServerExist != null) {

                    translateToReturn = serwerKlienta();
                } else {
                    translateToReturn = "nie ma";
                    clientSocket.close();
                }

                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return translateToReturn;

    }

    public static void sendWordToTranslate(String inputWordToTranslate, String inputLanguageCode, int languageServerPort) {

        //Zwrotka z serwera czy taki język istnieje
        try {
            String query = String.format(inputWordToTranslate + ":" + inputLanguageCode + ":" + languageServerPort);
            System.out.println(inputWordToTranslate + ":" + inputLanguageCode + ":" + languageServerPort);
            bw.write(query);
            bw.newLine();
            bw.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public String serwerKlienta() {
        String msgFromClient = null;
        try {
            ServerSocket serverSocket = new ServerSocket(CLIENT_SERVER_PORT);
            Socket clientSocket = serverSocket.accept();
            InputStream is = clientSocket.getInputStream(); // Czytanie danych ze socketa - od klienta
            OutputStream os = clientSocket.getOutputStream();
            br = new BufferedReader(new InputStreamReader(is)); // Za pomocą tych dwóch łączymy się ze strumieniami
            bw = new BufferedWriter(new OutputStreamWriter(os));

            msgFromClient = br.readLine();

            System.out.println("Tlumaczenie: " + msgFromClient);

            br.close();
            clientSocket.close();
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return msgFromClient;
    }

    public static void log(String message) {
        System.out.println("[Client]:  " + message);
        System.out.flush();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        ClientUI simpleUI = new ClientUI();
        simpleUI.ui(primaryStage);
    }
}