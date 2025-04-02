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

        String instrucoes = reader.readLine();
        System.out.println(instrucoes);

        System.out.println("Digite um dos comandos:");
        System.out.println("- LOGIN <usuario> <senha>    : Para logar");
        System.out.println("- REGISTER <usuario> <senha> : Para se registrar ");

        // Responsavel por ler o comando do cliente
        String comando = sc.nextLine();
        out.println(comando);

        String resposta = reader.readLine();
        if (resposta == null) {
            System.out.println("Servidor desconectado");
            socket.close();
            sc.close();
            return;
        }

        System.out.println("Resposta" + resposta);

        if (resposta.equals("LOGIN_OK")) {
            System.out.println("Login realizado com sucesso");
        } else if (resposta.equals("Cadastro realizado com sucesso")) {
            String resporta2 = reader.readLine();

            if (resporta2 != null && resporta2.equals("LOGIN_OK")) {
                System.out.println("Cadastro realizado e login efetuado com sucesso");
            } else {
                System.out.println("Cadastro realizado, mas houve problrma com o login automático");
                socket.close();
                sc.close();
                return;
            }
        } else {
            System.out.println("Erro: " + resposta);
            socket.close();
            sc.close();
            return;
        }

        //Rersponsaveis por permitir que o cliente receba e envie mensagem ao servidor
        ClienteThread clienteThread = new ClienteThread(socket);
        clienteThread.start();

        while (true){
            String comandoMensagem = sc.nextLine();
            out.println(comandoMensagem);
            if (comandoMensagem.equalsIgnoreCase("EXIT")){
                break;
            }
        }

        socket.close();
        sc.close();
    }
}

