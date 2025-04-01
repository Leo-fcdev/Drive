package Drive;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Cliente {
    public static void main(String[] args) throws IOException {
        //Estabelece uma conexão com o servidor
        Socket socket = new Socket("127.0.0.1" , 12345);
        Scanner sc = new Scanner(System.in);

        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        System.out.println(reader.readLine());

        System.out.println("Digite seu login (ex: LOGIN user1 pass1): ");
        String login = sc.nextLine();
        out.println(login);

        String resposta = reader.readLine();
        if (!"LOGIN_OK".equals(resposta)){
            System.out.println("Falha no login: " + resposta);
            socket.close();
            sc.close();
            return;
        }

        System.out.println("Login realizado com sucesso");

        //Rersponsaveis por permitir que o cliente receba e envie mensagem ao servidor
        ClienteThread clienteThread = new ClienteThread(socket);
        clienteThread.start();

        while (true){
            String comando = sc.nextLine();
            out.println(comando);
            if (comando.equalsIgnoreCase("EXIT")){
                break;
            }
        }

        socket.close();
        sc.close();
    }
}

