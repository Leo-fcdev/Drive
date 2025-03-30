package Drive;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Cliente {
    public static void main(String[] args) throws IOException {

        //Estabelece uma conexão com o servidor
        Socket socket = new Socket("Localhost" , 12345);
        Scanner sc = new Scanner(System.in);

        //Rersponsaveis por permitir que o cliente receba e envie mensagem ao servidor
        ClienteThread clienteThread = new ClienteThread(socket);
        clienteThread.start();
        //Classe para enviar informações para o servidor
        PrintStream saida = new PrintStream(socket.getOutputStream());
        String teclado = sc.nextLine();
        saida.println(teclado);
        }
    }

