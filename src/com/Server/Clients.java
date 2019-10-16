package com.Server;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.List;

public class Clients extends Thread{
    private Socket ClientSocket;
    private BufferedWriter writer;
    private BufferedReader reader;
    private String password;
    private String name = "none";

    public Clients(Socket ClientSocket, String name){

        this.ClientSocket = ClientSocket;
        this.name = name;

        try{
            this.writer =
                    new BufferedWriter(
                            new OutputStreamWriter(
                                    this.ClientSocket.getOutputStream()));

            this.reader =
                    new BufferedReader(
                            new InputStreamReader(
                                    this.ClientSocket.getInputStream()));

        } catch (IOException e) {e.printStackTrace();}
    }

    public void Write(String request){

        if( this.name.hashCode() == ("none").hashCode())return;

        try{

            this.writer.write(request);
            this.writer.newLine();
            this.writer.flush();

        } catch (IOException e) {e.printStackTrace();}
    }

    public String GetName(){ return this.name; }

    public void CloseAll(List<Rooms> rooms){
        try {

            for(int i = 0; i < rooms.size(); i++)
                for(int j = 0; j < rooms.get(i).clients_name.size(); j++)
                    if(this.name.hashCode() == rooms.get(i).clients_name.get(j).hashCode()){
                        rooms.get(i).clients_name.remove(j);
                        break;
                    }

            this.writer.close();
            this.reader.close();
            this.ClientSocket.close();
            this.name = "none";

        } catch (IOException e) { e.printStackTrace();}
    }

    public String record_return(){
        try {

            return this.reader.readLine().toString();

        } catch (IOException e) { e.printStackTrace(); }

        return LocalDateTime.now() + " : Reader error";
    }
}
