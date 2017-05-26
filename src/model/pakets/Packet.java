/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.pakets;

import java.util.List;
import java.util.ArrayList;

/**
 *
 * @author itirium
 */
public abstract class Packet {
    
    //username;time;message
    private List<String> dataList = new ArrayList<String>();
    public PacketType packetType; 
    
    //Створення пакету з вхідних данних
    public Packet(String[] rawData){     
      // String[] segments = rawData.split(";");
       for(String segment: rawData){
           dataList.add(segment);
       }
        //username;message
        //username  -> 0
        //message   -> 1
    }
    
    public Packet(PacketType type){
        this.packetType = type;
    }
    
    protected String getData(int index){
     return dataList.get(index);
    }
    
    protected void addData(String data){
        addData(data, 0);        
    }
    protected void addData(String data, int index){
        dataList.add(index, data);
    }
    //request the child packets to create the raw data string to be send to the client
    protected abstract void indexOutgoingData();
    
    public String getOutgoingData(){
        dataList.clear();
        indexOutgoingData();        
        return compileOutgoingData();
    }

    protected String compileOutgoingData() {
      StringBuffer buffer = new StringBuffer(packetType.name());
      buffer.append(";");
      for(int i = 0; i<=dataList.size()-1; i++){
          String data = dataList.get(i);
          buffer.append(data).append(";");
      }
//      for(int i = dataList.size() - 1; i >= 0; i--){
//          String data= dataList.get(i);
//          buffer.append(data).append(";");
//      }
      return buffer.toString();
    }
    
}
