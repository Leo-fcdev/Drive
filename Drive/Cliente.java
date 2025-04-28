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
        System.out.println("- LIST                       : Para listar arquivos");
        System.out.println("- UPLOAD <caminhoLocal>      : Para enviar um arquivo");
        System.out.println("- DOWNLOAD <nomeArquivo>     : Para baixar um arquivo");
        System.out.println("- EXIT                       : Para sair");

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
            String line = sc.nextLine();
            if (line.equalsIgnoreCase("LIST")) {
                out.println("LIST");
                // leitor de listagem já na ClienteThread

            } else if (line.startsWith("UPLOAD ")) {
                // UPLOAD <caminhoLocal>
                String localPath = line.substring(7).trim();
                File file = new File(localPath);
                if (!file.exists() || !file.isFile()) {
                    System.out.println("Arquivo não encontrado: " + localPath);
                    continue;
                }
                String filename = file.getName();
                long size = file.length();
                // envia comando com nome e tamanho
                out.println("UPLOAD " + filename + " " + size);
                // envia bytes do arquivo
                try (FileInputStream fis = new FileInputStream(file);
                     OutputStream os = socket.getOutputStream()) {
                    byte[] buffer = new byte[4096];
                    int read;
                    while ((read = fis.read(buffer)) != -1) {
                        os.write(buffer, 0, read);
                    }
                    os.flush();
                }
                // aguarda confirmação
                String confirmation = reader.readLine();
                System.out.println(confirmation);

            } else if (line.startsWith("DOWNLOAD ")) {
                // DOWNLOAD <nomeArquivo>
                String filename = line.substring(9).trim();
                out.println("DOWNLOAD " + filename);
                String header = reader.readLine();
                if (header == null) {
                    System.out.println("Servidor desconectado.");
                    break;
                }
                if (header.startsWith("DOWNLOAD_OK ")) {
                    long size = Long.parseLong(header.split(" ")[1]);
                    File outFile = new File(filename);
                    try (FileOutputStream fos = new FileOutputStream(outFile);
                         InputStream is = socket.getInputStream()) {
                        byte[] buffer = new byte[4096];
                        long remaining = size;
                        int read;
                        while (remaining > 0 && (read = is.read(buffer, 0, (int)Math.min(buffer.length, remaining))) != -1) {
                            fos.write(buffer, 0, read);
                            remaining -= read;
                        }
                        System.out.println("Download concluído: " + filename);
                    }
                } else {
                    System.out.println(header);
                }

            } else if (line.equalsIgnoreCase("EXIT")) {
                out.println("EXIT");
                break;

            } else {
                // comando não específico, envia ao servidor (echo)
                out.println(line);
            }
        }

        socket.close();
        sc.close();
    }
}

