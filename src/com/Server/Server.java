package com.Server;

import sun.rmi.runtime.Log;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Server {

    public static void main(String[] args) {

        List<Clients> clients = new ArrayList<>();
        List<Rooms> rooms = new ArrayList<>();

        try (ServerSocket server = new ServerSocket(8000)) {

            try (BufferedWriter logs =
                         new BufferedWriter(
                                 new OutputStreamWriter(
                                         new FileOutputStream(
                                                 new File(".logs.txt"))));) {


                System.out.println("Сервер запущен...");
                Logs_Rec(logs, LocalDateTime.now().toString() + " : Сервер запущен");

                while (true) {

                    Socket socket = server.accept();

                    BufferedReader reader =
                            new BufferedReader(
                                    new InputStreamReader(
                                            socket.getInputStream()));

                    new Thread(() -> {

                        String Client_name = AddClient(clients, socket);
                        Thread.currentThread().setName(Client_name);

                        Server_Message("Сервер : <<" + Client_name + ">> добро пожаловать в чат!", clients);
                        Logs_Rec(logs, LocalDateTime.now().toString() + " : Клиент <<" + Client_name + ">> вошел в чат");

                        while (true)
                            try {
                                if (!Thread.currentThread().isInterrupted()) {
                                    String request;

                                    if ((request = reader.readLine()).hashCode() == ("-выход").hashCode()) {

                                        for (int i = 0; i < clients.size(); i++)
                                            if (Thread.currentThread().getName().hashCode() == clients.get(i).GetName().hashCode()) {

                                                clients.get(i).CloseAll(rooms);
                                                Thread.currentThread().interrupt();

                                            } else {

                                                clients.get(i).Write("Сервер : <<" + Thread.currentThread().getName()
                                                        + ">> покинул чат...");

                                            }

                                        Logs_Rec(logs, LocalDateTime.now().toString() + " : Клиент <<"
                                                + Thread.currentThread().getName() + ">> покинул чат");

                                    } else if (request.charAt(0) == '@') {

                                        String[] name = request.split(" ");
                                        String request_private = "";

                                        for (int i = 1; i < name.length; i++)
                                            request_private += (name[i] + " ");

                                        for (int i = 0; i < clients.size(); i++)
                                            if (name[0].hashCode() == ("@" + clients.get(i).GetName()).hashCode()) {

                                                clients.get(i).Write(Thread.currentThread().getName().toString()
                                                        + " шепчет вам : " + request_private);
                                                Logs_Rec(logs, LocalDateTime.now().toString() + " : Клиент <<"
                                                        + Thread.currentThread().getName() + ">> передает личное сообщение клиенту <<"
                                                + clients.get(i).GetName() + ">> : " + request_private);

                                            }
                                    } else if (request.charAt(0) == '#') {

                                        String[] name = request.split(" ");
                                        String request_room = "";
                                        String name_room = name[0].substring(1, name[0].length());

                                        for (int i = 1; i < name.length; i++)
                                            request_room += (name[i] + " ");

                                        int index;
                                        if ((index = room_enter(name_room, rooms)) >= 0) {

                                            if (rooms.get(index).He_in_a_room(Thread.currentThread().getName())) {

                                                for (int i = 0; i < clients.size(); i++)
                                                    if (rooms.get(index).He_in_a_room(clients.get(i).GetName()) &&
                                                            Thread.currentThread().getName().hashCode() !=
                                                                    clients.get(i).GetName().hashCode())
                                                        clients.get(i).Write("#" + rooms.get(index).Get_Name() + " от " +
                                                                Thread.currentThread().getName() + " : " + request_room);

                                                    Logs_Rec(logs, LocalDateTime.now().toString() + " : Комната #"
                                                                + rooms.get(index).Get_Name() + " : " + Thread.currentThread().getName()
                                                                + " : " + request_room);

                                            } else {

                                                rooms.get(index).Add_in_room(Thread.currentThread().getName());
                                                Logs_Rec(logs, LocalDateTime.now().toString() + " : Клиент <<"
                                                        + Thread.currentThread().getName() + ">> вошел в комнату #"
                                                        + rooms.get(index).Get_Name());
                                            }
                                        } else {

                                            rooms.add(new Rooms(name_room));
                                            rooms.get(rooms.size() - 1).Add_in_room(Thread.currentThread().getName());

                                            Logs_Rec(logs, LocalDateTime.now().toString() + " : Создана новая комната #"
                                                    + rooms.get(rooms.size()-1).Get_Name());
                                            Logs_Rec(logs, LocalDateTime.now().toString() + " : Клиент <<"
                                                    + Thread.currentThread().getName() + ">> вошел в комнату #"
                                                    + rooms.get(rooms.size()-1).Get_Name());

                                        }

                                    } else {

                                        request = Thread.currentThread().getName() + " : " + request;

                                        try {
                                            Thread.sleep(50);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }

                                        Server_Message(request, clients);
                                        Logs_Rec(logs, LocalDateTime.now().toString() + " : Клиент пишет в общий чат : "
                                                + request);
                                    }
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                    }).start();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String AddClient(List<Clients> clients, Socket socket) {

        for (int i = 0; i < clients.size(); i++)
            if (clients.get(i).GetName().hashCode() == ("none").hashCode()) {

                clients.set(i, new Clients(socket, "Клиент_" + (i + 1)));
                return ("Клиент_" + (i + 1));
            }

        clients.add(new Clients(socket, "Клиент_" + (clients.size() + 1)));
        return ("Клиент_" + clients.size());
    }

    public static void Server_Message(String request, List<Clients> clients) {

        for (int i = 0; i < clients.size(); i++)
            if (Thread.currentThread().getName().hashCode() != clients.get(i).GetName().hashCode()
                    && clients.get(i).GetName().hashCode() != ("none").hashCode())

                clients.get(i).Write(request);
    }

    public static int room_enter(String room_name, List<Rooms> rooms) {
        for (int i = 0; i < rooms.size(); i++)
            if (room_name.hashCode() == rooms.get(i).Get_Name().hashCode()) return i;

        return -1;
    }

    public static void Logs_Rec(BufferedWriter logs, String record) {
        try {

            logs.write(record);
            logs.newLine();
            logs.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


