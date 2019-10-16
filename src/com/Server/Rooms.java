package com.Server;

import java.util.ArrayList;
import java.util.List;

public class Rooms {
    String room_name;
    List<String> clients_name = new ArrayList<>();

    public Rooms(String room_name){

        this.room_name = room_name;

    }

    public void Add_in_room(String name){

        clients_name.add(name);

    }

    public boolean He_in_a_room(String name){

        for (int i = 0; i < clients_name.size(); i++)
            if (clients_name.get(i).hashCode() == name.hashCode())
                return true;

        return false;
    }

    public String Get_Name(){
        return this.room_name;
    }

}
