/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.awt.Color;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author itirium
 */
public class DBJavaChat {
    public static Connection con = null;
    ResultSet rs = null;
    PreparedStatement pst =  null;
    boolean isConnected = false;
    public void Connect(String url){        
            try {
                String tempURL=url;
                //Driver d = (Driver)Class.forName("com.microsoft.jdbc.sqlserver.SQLServerDriver").newInstance();
               Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                //String url = "jdbc:sqlserver://notesite.database.windows.net;databaseName=JavaChat;user=itirium;password=Dctdcfl159357";
                if((tempURL==null)||(tempURL.isEmpty())){ tempURL = "jdbc:sqlserver://notesite.database.windows.net;databaseName=JavaChat;user=itirium;password=Dctdcfl159357";}
                con = DriverManager.getConnection(tempURL);
                isConnected = true;
            } catch (ClassNotFoundException | SQLException ex) {
                Logger.getLogger(DBJavaChat.class.getName()).log(Level.SEVERE, null, ex);
            }            
        }
    public void CloseConnection(){
        try {
            con.close();
            isConnected=false;
        } catch (SQLException ex) {
            Logger.getLogger(DBJavaChat.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
    public void addServer(String servername, String ip, String port){
        if(isConnected){
        try {              
            String sql = "insert into tblChatServers(ChatServerName, ChatServerIp, ChatServerPort) values (?,?,?)";
            pst = con.prepareStatement(sql);
            pst.setString(1, servername);
            pst.setString(2, ip);
            pst.setString(3, port);
            pst.execute();
        } catch (SQLException ex) {
            Logger.getLogger(DBJavaChat.class.getName()).log(Level.SEVERE, null, ex);
        }
        }
    }
    public int checkServerExist(String ip){
        if(isConnected){
        try {
            String sql = "select id from tblChatServers where ChatServerIp=?";
            pst = con.prepareStatement(sql);
            pst.setString(1, ip);
            rs = pst.executeQuery();
            if(rs.next()){
                //server exist
                return rs.getInt("id");
            }
            else
            {
                //need new one
                return -1;
            }
        } catch (SQLException ex) {
            Logger.getLogger(DBJavaChat.class.getName()).log(Level.SEVERE, null, ex);
        }                  
        }
        return -1;
    }
    public List<ServerClass> getServerList(){
        if(isConnected){
        try {
            List<ServerClass> servers = new ArrayList<ServerClass>();
            String sql = "select * from tblChatServers";
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();
            while(rs.next()){
                 //server exist
                ServerClass tmp = new ServerClass(rs.getString("ChatServerName"), rs.getString("ChatServerIp"), rs.getString("ChatServerPort"));
                servers.add(tmp);               
            }
            return servers;
        } catch (SQLException ex) {
            Logger.getLogger(DBJavaChat.class.getName()).log(Level.SEVERE, null, ex);
        }      
        }
        return  null;    
    }
    public void addServerMessage(int serverId, String message){
        if(isConnected){
        try {              
            String sql = "insert into tblChatServersMessages(ChatServerId, MessageDT, ChatServerMessage) values (?,?,?)";
            pst = con.prepareStatement(sql);
            pst.setString(1, Integer.toString(serverId));
            pst.setString(2, new Date().toString());            
            pst.setString(3, message);
            pst.execute();
        } catch (SQLException ex) {
            Logger.getLogger(DBJavaChat.class.getName()).log(Level.SEVERE, null, ex);
        }
        }   
    }
    public void addUserMessage(int serverId,int userId, String message){
        if(isConnected){
        try {              
            String sql = "insert into tblChatMessages(UserId,ServerId, MessageDT, MessageText) values (?,?,?,?)";
            pst = con.prepareStatement(sql);
            pst.setString(1, Integer.toString(userId));
            pst.setString(2, Integer.toString(serverId));
            pst.setString(3, new Date().toString());            
            pst.setString(4, message);
            pst.execute();
        } catch (SQLException ex) {
            Logger.getLogger(DBJavaChat.class.getName()).log(Level.SEVERE, null, ex);
        }
        } 
    }
    public void addNewUser(String username, String password, Color color){
        if(isConnected){
        try {              
            String sql = "insert into tblChatUsers(UserName, UserPassword, UserSelectedColor) values (?,?,?)";
            pst = con.prepareStatement(sql);
            pst.setString(1, username);
            pst.setString(2, password);            
            pst.setString(3, Integer.toString(color.getRGB()));
            pst.execute();
        } catch (SQLException ex) {
            Logger.getLogger(DBJavaChat.class.getName()).log(Level.SEVERE, null, ex);
        }
        }        
    }
    public int getUserColor(String username, String password){
        if(isConnected){
        try {
            String sql = "select UserSelectedColor from tblChatUsers where UserName=? and UserPassword=?";
            pst = con.prepareStatement(sql);
            pst.setString(1, username);
            pst.setString(2, password);
            rs = pst.executeQuery();
            if(rs.next()){
                //user exist or right password
                return rs.getInt("UserSelectedColor");
            }
            else
            {
                //need new one
                return -1;
            }
        } catch (SQLException ex) {
            Logger.getLogger(DBJavaChat.class.getName()).log(Level.SEVERE, null, ex);
        }                  
        }
        return -1;
    }
    public int getUserId(String username, String password){
        if(isConnected){
        try {
            String sql = "select id from tblChatUsers where UserName=? and UserPassword=?";
            pst = con.prepareStatement(sql);
            pst.setString(1, username);
            pst.setString(2, password);
            rs = pst.executeQuery();
            if(rs.next()){
                //user exist or right password
                return rs.getInt("id");
            }
            else
            {
                //need new one
                return -1;
            }
        } catch (SQLException ex) {
            Logger.getLogger(DBJavaChat.class.getName()).log(Level.SEVERE, null, ex);
        }                  
        }
        return -1;
    }

    public int ifUserExist(String username) {
       if(isConnected){
        try {
            String sql = "select id from tblChatUsers where UserName=?";
            pst = con.prepareStatement(sql);
            pst.setString(1, username);
            rs = pst.executeQuery();
            if(rs.next()){
                //user exist or right password
                return rs.getInt("id");
            }
            else
            {
                //need new one
                return -1;
            }
        } catch (SQLException ex) {
            Logger.getLogger(DBJavaChat.class.getName()).log(Level.SEVERE, null, ex);
        }                  
        }
        return -1;}
    
    
}

