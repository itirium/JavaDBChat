/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.pakets;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author itirium
 */
public class ListOfUsersPacket extends Packet{
   
    //incomming constructor
    public List<String> users = new ArrayList<String>();
    public int count = 0;
    
    public ListOfUsersPacket(){
         super(PacketType.LIST_OF_USERS);
    }
    public ListOfUsersPacket(String[] rawData){
        super(PacketType.LIST_OF_USERS);
        for(String user: rawData){
             users.add(user);
             count++;
        }    
    }    
    public void addUser(String username){
        users.add(username);
        addData(username, users.size()-1);
    }
    @Override
    protected void indexOutgoingData() {
       //Connect;username; 
       for(int i = 0; i<count;i++){
           addData(users.get(i),i);
        }
    }
    public String[] getAllUsers(){
        return users.toArray(new String[count]);
    }
}
