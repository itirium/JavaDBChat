/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;


import java.awt.Color;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import model.pakets.ConnectPacket;
import model.pakets.ConnectPacketClient;
import model.pakets.DisconnectPacket;
import model.pakets.ListOfUsersPacket;
import model.pakets.MessagePacket;
import model.pakets.Packet;
import model.pakets.PacketDictionary;
import model.pakets.PacketType;

/**
 *
 * @author itirium
 */
public class NetworkClient {
   
    private Socket socket;
    private String ipAdress;
    private int serverPort;
    private final ChatClient chat;
    private String nickname;    
    private String[] connectedUsers = null;
    public boolean isConnected = false;
    public int userId = -1;
    
    
    public NetworkClient(ChatClient chat,String ipAdress, int serverPort){
        this.ipAdress = ipAdress;
        this.serverPort = serverPort;
        this.chat = chat;
    }
    
    public void connectToServer(String nick, int userID){
        
        //ChatClient.getInstance().dia
        nickname = nick;
        userId = userID;
                
        if((nickname!=null)&&(!nickname.isEmpty())){
           
        try {
            socket = new Socket(ipAdress, serverPort);
             ChatClient.getInstance().Log(" Connected to server\n");
             isConnected = true;
         //   DataOutputStream output = new DataOutputStream(socket.getOutputStream());
         //   output.writeUTF("Hello Server");
        } catch (IOException e) {
            e.printStackTrace();
           return;
        }
        try {
            sendPacket(new ConnectPacket(nickname));
        } catch (IOException ex) {
            Logger.getLogger(NetworkClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        //client listens for server request/responses
        
        new Thread((Runnable)()-> {            
                while(true){
                    try {
                        DataInputStream input = new DataInputStream(socket.getInputStream());
                        String rawData = input.readUTF();
                        String[] data = rawData.trim().split(";");
                        PacketType type = PacketType.valueOf(data[0]);
                        Packet packet = PacketDictionary.translatePacketType(type, data);
                        //debug
                        //ChatClient.getInstance().Log("Recieved: "+rawData+"\n");
                        //-----
                        
                        switch(type){                       
                            case LOGIN:
                                break;
                            case REGISTER:
                                break;
                            case CONNECT:
                                 ChatClient.getInstance().Log(" Client: "+((ConnectPacket)packet).i_username+" connected.\n"); 
                                break;
                            case DISCONNECT:
                                 ChatClient.getInstance().Log("Client "+data[1]+" disconnected.\n");
                                break;
                            case CHAT:
                                 ChatClient.getInstance().Log("  "+data[1]+"  :  ",data[2]+"\n",Color.BLUE);
                                break;
                            case LOGOUT:
                                break;
                            case LIST_OF_USERS:
                                 LoadUserList(data);
                                break;
                            default:
                                throw new AssertionError(type.name());
                                                
                        }                       
                        
                    } catch (IOException ex) {
                        ChatClient.getInstance().Log(ex.getLocalizedMessage());
                        ex.printStackTrace();
                    }
                }            
        }).start();
        
        }
        else{
            ChatClient.getInstance().Log("Enter Your Name.\n");
        }
    }
    
    private void sendPacket(Packet packet) throws IOException{
        DataOutputStream output = new DataOutputStream(socket.getOutputStream());
        output.writeUTF(packet.getOutgoingData());
    }
    public void sendMessage(String message){
        MessagePacket mess = new MessagePacket(nickname, message);
        try {
            sendPacket(mess);
        } catch (IOException ex) {
            Logger.getLogger(NetworkClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void LoadUserList(String[] users){
        if(users.length>1){
             
        connectedUsers = new String[users.length-1];
        for(int i=1;i<users.length;i++){
         connectedUsers[i-1] = users[i];    
        }       
         chat.updateView(connectedUsers);
    
        }
    }
}
