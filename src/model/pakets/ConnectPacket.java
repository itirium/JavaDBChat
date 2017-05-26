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
public class ConnectPacket extends Packet{
   
    //incomming constructor
    public String i_username;
    public ConnectPacket(String[] rawData){
        super(rawData);
        i_username = getData(1);
    }
    //outgoing constructor
   // private String i_username;
    public ConnectPacket(String username){
        super(PacketType.CONNECT);
        this.i_username=username;
    }
    @Override
    protected void indexOutgoingData() {
       //Connect;username;
       addData(i_username);
    }
    
}
