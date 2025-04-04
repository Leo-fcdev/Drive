package Drive;

import java.io.*;
import java.net.*;

public class ClienteThread extends Thread {
    private Socket socket;

    public ClienteThread(Socket socket){
        this.socket = socket;
    }

    @Override
    public void run(){
        try {
            BufferedReader reader =  new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String mensagem;

            while ((mensagem = reader.readLine()) != null);{
                System.out.println(mensagem);
            }
        } catch (Exception ex) {
            System.out.println("Conexão com o servidor encerrada.");
        }
    }
}
