/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import static com.sun.org.apache.bcel.internal.Repository.instanceOf;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.DBJavaChat;
import model.pakets.ConnectPacket;
import model.pakets.DisconnectPacket;
import model.pakets.ListOfUsersPacket;
import model.pakets.MessagePacket;
import model.pakets.Packet;
import model.pakets.PacketDictionary;
import model.pakets.PacketListener;
import model.pakets.PacketType;

/**
 *
 * @author itirium
 */
public class NetworkServer implements PacketListener{
    
    private ServerSocket serverSocket;
    private boolean isRunning = false;
    private int serverPort;
    public int serverID = -1;    
    
    private List<PacketListener> packetListeners = new ArrayList<>();
    private Map<String, Socket> connectedClientMap = new HashMap<>();
    private final ChatServer chat;
    
    //Задання порту сервера
    public NetworkServer(ChatServer chat, int port){
        this.serverPort = port;
        this.chat = chat;
        addPacketListener(this);        
    }

    public void startServer(String url){
        //Створення сокету сервера
        if(!isRunning)
        {
        try {
            serverSocket = new ServerSocket(serverPort);  
            ChatServer.getInstance().Log("Server socket started at port "+serverPort);
            DBJavaChat db = new DBJavaChat();
            db.Connect(url);            
            if (db.checkServerExist("127.0.0.1")==-1) {
                db.addServer("Local", "127.0.0.1", Integer.toString(serverPort));
            }
            serverID = db.checkServerExist("127.0.0.1");
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        
        //Початок прослуховування порту сервера
        new Thread(new Runnable(){
            @Override
            public void run() {
               ChatServer.getInstance().Log("Waiting for Clients...");
               while(isRunning){
                   try {
                       Socket client = serverSocket.accept();
                       ChatServer.getInstance().Log("New Client has connected! "+client.getRemoteSocketAddress());
                       
                       //Читаємо дані кожного клієнта
                       new Thread(new Runnable() {
                           @Override
                           public void run() {
                               boolean error = false;
                               while(!error && client.isConnected()){
                                   try {
                                       DataInputStream input = new DataInputStream(client.getInputStream());
                                      // String request = input.readUTF();
                                       String rawData = input.readUTF();
                                       String[] data = rawData.trim().split(";");
                                       PacketType type = PacketType.valueOf(data[0]);
                                       Packet packet = null;// = PacketDictionary.translatePacketType(type, data);
                                       
                                       //разбор запроса new user
                                       if(type.equals(PacketType.CONNECT)){
                                           packet = new ConnectPacket(data);
                                           ChatServer.getInstance().Log("Client Login: "+((ConnectPacket)packet).i_username);                                      
                                       }
                                       if(type.equals(PacketType.CHAT)){
                                           packet = new MessagePacket(data);
                                           ChatServer.getInstance().Log("\n MessageClient: "+rawData);                                      
                                       }
                                       
                                      // ChatServer.getInstance().Log("PAcket Type : "+packet.packetType.name());
                                       //broadcast
                                       broadcastPacketRecieved(packet, client);
                                       
                                       //DataOutputStream output = new DataOutputStream(client.getOutputStream());
                                       //String rawCl=packet.getOutgoingData();
                                       //ChatServer.getInstance().Log(rawCl+"\n");                                      
                                       //output.writeUTF(rawCl);
                                       
                                   } catch (EOFException e) {
                                       error = true;
                                       ChatServer.getInstance().Log("Client "+client.getRemoteSocketAddress()+" has disconnected!");
                                       
                                   } catch (IOException e) {
                                       error = true;
                                       e.printStackTrace();
                                   }                                   
                               }
                               try {
                                   removeClient(client);
                               } catch (IOException ex) {
                                   Logger.getLogger(NetworkServer.class.getName()).log(Level.SEVERE, null, ex);
                               }
                           }

                          

                           
                       }).start();
                       
                   } catch (IOException e) {
                       e.printStackTrace();
                   }
               }
            }            
        }).start();
        
        isRunning = true;
        }
    }
    private void broadcastPacketRecieved(Packet packet, Socket client) {
        for(PacketListener packetListener: packetListeners){
            packetListener.packetRecieved(packet, client);   
            
        }
    }
    
    private void broadcastPacketSent(Packet packet, Socket client){
        for(PacketListener packetListener: packetListeners){
            packetListener.packetSent(packet, client);
        }               
    }
    public void stopServer(){
        try {
            serverSocket.close();
             ChatServer.getInstance().Log("Server socket closed!\n\n");
        } catch (IOException ex) {
            Logger.getLogger(NetworkServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        isRunning = false;
    }

    @Override
    public void packetSent(Packet packet, Socket client) {
      
       if(packet instanceof ConnectPacket){
           try {
               connectClient((ConnectPacket)packet, client);
           } catch (IOException ex) {
               Logger.getLogger(NetworkServer.class.getName()).log(Level.SEVERE, null, ex);
           }
       }
       if(packet instanceof MessagePacket){           
           try {
               messageClient((MessagePacket)packet, client);
           } catch (IOException ex) {
               Logger.getLogger(NetworkServer.class.getName()).log(Level.SEVERE, null, ex);
           }
       }
    }

    @Override
    public void packetRecieved(Packet packet, Socket client) {
       if(packet instanceof ConnectPacket){
           try {
               connectClient((ConnectPacket)packet, client);
           } catch (IOException ex) {
               Logger.getLogger(NetworkServer.class.getName()).log(Level.SEVERE, null, ex);
           }
       }
       if(packet instanceof MessagePacket){          
           try {
               messageClient((MessagePacket)packet, client);
               
           } catch (IOException ex) {
               Logger.getLogger(NetworkServer.class.getName()).log(Level.SEVERE, null, ex);
           }
       }
       //if regpacket or disconnect
    }

    private void connectClient(ConnectPacket packet, Socket client) throws IOException {
        if(connectedClientMap.get(packet.i_username) != null)
        {
            return;
        }
        connectedClientMap.put(packet.i_username, client);
        chat.updateView();
      
        for(String nickname: connectedClientMap.keySet()){
            //Якщо хтось підключився - відіслати це усім
             Socket socket = connectedClientMap.get(nickname);
             
             if(!socket.equals(client)){  
                 ConnectPacket pack = new ConnectPacket(packet.i_username);
                 sendPacket(pack, socket);   
                 }            
                sendAllListTo(socket); 
             }          
    }
    
     private void messageClient(MessagePacket packet, Socket client) throws IOException {
        for(String nickname: connectedClientMap.keySet()){
            //Якщо хтось підключився - відіслати це усім
             Socket socket = connectedClientMap.get(nickname);             
             MessagePacket pack = new MessagePacket(packet.i_username,packet.message);
                sendPacket(pack, socket);          
             }          
    }
    
    private void removeClient(Socket client) throws IOException {
        String disc_client = null;
         for(String nickname: connectedClientMap.keySet()){
             Socket socket = connectedClientMap.get(nickname);
             if(socket.equals(client)){
                 disc_client=nickname;
                 connectedClientMap.remove(nickname);
                 //TODO send disconnect
                  ChatServer.getInstance().Log("Client Disconnected: "+nickname+"\n");                  
             }
         }
         for(String nickname: connectedClientMap.keySet()){
            //Якщо хтось відключився - відіслати це усім
             Socket socket = connectedClientMap.get(nickname);               
             DisconnectPacket pack = new DisconnectPacket(disc_client);
                sendPacket(pack, socket);
                sendAllListTo(socket);
             }  
         chat.updateView();
    }
    public Map<String, Socket> getConnectedClientMap(){
        return connectedClientMap;
    }

    private void addPacketListener(PacketListener listener) {
      packetListeners.add(listener);
    }
    private void sendPacket(Packet packet, Socket socket) throws IOException{
        DataOutputStream output = new DataOutputStream(socket.getOutputStream());
        output.writeUTF(packet.getOutgoingData());
    }    
    
    private void sendAllListTo(Socket client) throws IOException{       
        String[] usrs = connectedClientMap.keySet().toArray(new String[connectedClientMap.keySet().size()]);
       
        ListOfUsersPacket pack = new ListOfUsersPacket(usrs);             
            // if(!socket.equals(client)){}
            sendPacket(pack, client);                 
    }
    
    private void sendAllListToAllConnected() throws IOException{   
        
        ListOfUsersPacket pack = new ListOfUsersPacket((String[]) connectedClientMap.keySet().toArray());
             
        for(String nickname: connectedClientMap.keySet()){
            //Якщо хтось підключився - відіслати це усім
             Socket socket = connectedClientMap.get(nickname);             
            // if(!socket.equals(client)){}
            sendPacket(pack, socket);
             }     
    }
    private void sendClientDisconnectedMessage(){
        
    }
    private void sendMessage(){
        
    }
}
