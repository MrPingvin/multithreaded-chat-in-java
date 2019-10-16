package Clients;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {

        try(
                Socket socket = new Socket(InetAddress.getByName("127.0.0.1"), 8000);

                BufferedWriter writer =
                        new BufferedWriter(
                                new OutputStreamWriter(
                                        socket.getOutputStream()));
                BufferedReader reader =
                        new BufferedReader(
                                new InputStreamReader(
                                        socket.getInputStream()));
        ) {

            System.out.println("Добро пожаловать в чат!");

            new Thread(() ->{
                while(true)
                    try {

                        String response = reader.readLine();

                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) { }

                        System.out.println(response);

                    } catch (IOException e) {}

            }).start();

            Scanner input = new Scanner(System.in);
            String request;
            while(true){

                writer.write(request = input.nextLine());
                writer.newLine();
                writer.flush();

                if(request.hashCode() == ("-выход").hashCode()){

                    input.close();
                    reader.close();
                    System.out.println("Вы покидаете чат...");
                    System.exit(0);
                };

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
