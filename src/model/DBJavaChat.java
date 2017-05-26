/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
    public void Connect(){        
            try {
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                String url = "jdbc:sqlserver://notesite.database.windows.net;databaseName=JavaChat;user=itirium;password=Dctdcfl159357";
                con = DriverManager.getConnection(url);
                isConnected = true;
            } catch (ClassNotFoundException | SQLException ex) {
                Logger.getLogger(DBJavaChat.class.getName()).log(Level.SEVERE, null, ex);
            }            
        }
    public void addServer(String servername, String ip, String port){
        if(isConnected){
        try {
//            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
//                String url = "jdbc:sqlserver://notesite.database.windows.net;databaseName=JavaChat;user=itirium;password=Dctdcfl159357";
//                con = DriverManager.getConnection(url);
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
    public boolean checkServerExist(String ip){
        if(isConnected){
        try {
//            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
//                String url = "jdbc:sqlserver://notesite.database.windows.net;databaseName=JavaChat;user=itirium;password=Dctdcfl159357";
//                con = DriverManager.getConnection(url);
            String sql = "select * from tblChatServers where ChatServerIp=?";
            pst = con.prepareStatement(sql);
            pst.setString(1, ip);
            rs = pst.executeQuery();
            if(rs.next()){
                //server exist
                return true;
            }
            else
            {
                //need new one
                return false;
            }
        } catch (SQLException ex) {
            Logger.getLogger(DBJavaChat.class.getName()).log(Level.SEVERE, null, ex);
        }                  
        }
        return false;
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
}

