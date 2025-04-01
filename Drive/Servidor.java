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

    static class ClienteHandler implements Runnable{
        private Socket socket;

        public ClienteHandler(Socket socket){
            this.socket = socket;
        }
        @Override
        public void run(){
            try{
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter saida = new PrintWriter(socket.getOutputStream());

                saida.println("Digite: LOGIN <usuario> <senha>");
                saida.flush();

                String credenciais = reader.readLine();
                if (credenciais == null || !credenciais.startsWith("LOGIN ")){
                    saida.println("LOGIN_FAILED");
                    socket.close();
                    return;
                }

                String[] partes = credenciais.split(" ");
                if (partes.length != 3){
                    saida.println("LOGIN_FAILED");
                    socket.close();
                    return;
                }

                String usuario = partes[1];
                String senha = partes[2];

                if (users.containsKey(usuario) && users.get(usuario).equals(senha)){
                    saida.println("LOGIN_OK");
                    System.out.println("Usuario autenticado: " + usuario);
                } else {
                    saida.println("LOGIN_FAILED");
                    socket.close();
                    return;
                }

                String mensagem;
                while ((mensagem = reader.readLine()) != null){
                    System.out.println("Mensagem de " + usuario + ": " + mensagem);
                    saida.println("Servidor: " + mensagem);
                }

                socket.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}