package Drive;

import java.io.*;
import java.net.*;
import java.nio.file.Path;
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

                String basePath = "files" + File.separator + usuario;
                String[] tipos = {"PDF", "JPG", "TXT", "OUTROS"};
                for (String tipo : tipos){
                    File diretorio = new File(basePath + File.separator + tipo);
                    if (!diretorio.exists()){
                        diretorio.mkdirs();
                    }
                }

                String linha;
                while ((linha = reader.readLine()) != null){
                    if (linha.equalsIgnoreCase("LIST")){
                        listUserFiles(basePath, saida);
                    } else {
                        System.out.println("Mensagem de " + usuario + ": " + linha);
                        saida.println("Servidor: " + linha);
                    }
                }
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        private void listUserFiles(String basePath, PrintWriter saida){
            File userDir = new File(basePath);
            if (!userDir.exists()){
                saida.println(("Nenhum arquivo encontrado"));
            } else {
                walkAndSend(userDir, userDir, saida);
            }
            saida.println("END-OF-LIST");
        }

        private void walkAndSend(File base, File diretorio, PrintWriter saida){
            File[] files = diretorio.listFiles();
            if (files != null){
                for (File f : files){
                    if (f.isDirectory()){
                        walkAndSend(base, f, saida);
                    } else {
                        Path rel = base.toPath().relativize(f.toPath());
                        saida.println(rel.toString());
                    }
                }
            }
        }
    }
}