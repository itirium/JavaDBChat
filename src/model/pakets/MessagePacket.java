/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.pakets;

/**
 *
 * @author itirium
 */
public class MessagePacket extends Packet{
   
    //incomming constructor
    public String i_username;
    public String message;
    
    public MessagePacket(String[] rawData){
        super(rawData);
        i_username = getData(1);
        message = getData(2);
    }
    //outgoing constructor
   // private String i_username;
    public MessagePacket(String username, String message){
        super(PacketType.CHAT);
        this.i_username=username;
        this.message = message;
    }
    @Override
    protected void indexOutgoingData() {
       //Connect;username;
       addData(i_username,0);
       addData(message,1);
    }
}
