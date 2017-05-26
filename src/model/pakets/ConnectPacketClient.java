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
public class ConnectPacketClient extends Packet{
   
    //incomming constructor
    public String i_username;
    public ConnectPacketClient(String[] rawData){
        super(rawData);
        i_username = getData(1);
    }
    //outgoing constructor
    private String o_username;
    public ConnectPacketClient(String username){
        super(PacketType.CONNECT);
        this.o_username=username;
    }
    @Override
    protected void indexOutgoingData() {
       //Connect;username;
       addData(o_username);
    }
    
}
