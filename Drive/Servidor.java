package Drive;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class Servidor {

    //As informações de login seram armazenadas no HashMap
    private  static Map<String, String> users = new HashMap<>();

    public static void main(String[] args) throws IOException {

        //Vai verificar se o usuario e senha estão corretos antes de conectar
        users.put("user1", "pass1");
        users.put("user2", "pass2");

        //Cria uma conexão
        ServerSocket serverSocket = new ServerSocket(12345);
        System.out.println("Servidor Iniciado. Aguardando conexão...");

        while (true){
            //Espera o Cliente conectar
            Socket socket = serverSocket.accept();
            System.out.println("Cliente conectado");
            new Thread(new ClienteHandler(socket)).start();
        }
    }

    // Classe que trata conexão de cada cliente
    static class ClienteHandler implements Runnable{
        private Socket socket;

        public ClienteHandler(Socket socket){
            this.socket = socket;
        }
        @Override
        public void run(){
            try{

                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter saida = new PrintWriter(socket.getOutputStream(), true);

                // Envia ao cliente intruções iniciais
                saida.println("Bem-vindo");

                // Lê o comando enviado pelo cliente
                String command = reader.readLine();
                System.out.println("Comando recebido: " + command);
                if (command == null){
                    saida.println("Comando invalido");
                    socket.close();
                    return;
                }

                String[] partes = command.split(" ");
                if (partes.length < 3) {
                    saida.println("Comando inválido");
                    socket.close();
                    return;
                }

                String cmd = partes[0];
                String usuario = partes[1];
                String senha = partes[2];

                // Esse bloco é executado se o comando for LOGIN
                if (cmd.equalsIgnoreCase("LOGIN")){
                    if (users.containsKey(usuario) && users.get(usuario).equals(senha)){
                        saida.println("LOGIN_OK");
                        System.out.println("Usuario autenticado: " + usuario);
                    } else {
                        saida.println("LOGIN_FAILED");
                        socket.close();
                        return;
                    }

                // Esse é executado se o comando for REGISTER
                } else if (cmd.equalsIgnoreCase("REGISTER")) {
                    if (users.containsKey(usuario)) {
                        saida.println("Usuário já cadastrado");
                        socket.close();
                        return;
                    } else {
                        users.put(usuario, senha);
                        saida.println("Cadastro realizado com sucesso");
                        saida.println("LOGIN_OK");
                        System.out.println("Usuário cadastrado e autenticado" + usuario);
                    }

                // Caso nenhum comando valido seja chamado pelo cliente executa esse
                } else{
                    saida.println("Comando inválido");
                    socket.close();
                    return;
                }

                String mensagem;
                while ((mensagem = reader.readLine()) != null){
                    System.out.println("Mensagem do cliente " + mensagem);
                    saida.println("Servidor: " + mensagem);
                }

                socket.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}