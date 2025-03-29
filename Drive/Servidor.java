package Drive;

import java.io.*;
import java.net.*;

public class Servidor {
    public static void main(String[] args) throws IOException {

        //Cria uma conexão
        ServerSocket serverSocket = new ServerSocket(12345);
        //Espera o Cliente conectar
        Socket socket = serverSocket.accept();
        System.out.println("Cliente conectado");

        InputStreamReader inputReader = new InputStreamReader(socket.getInputStream());
        PrintStream saida = new PrintStream(socket.getOutputStream());
        BufferedReader reader = new BufferedReader(inputReader);
        String x;
        while ((x = reader.readLine()) != null){
            saida.println("Servidor: " + x);
        }
    }
}