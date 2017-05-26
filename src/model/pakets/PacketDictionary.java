/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.pakets;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author itirium
 * JAVA REFLECTION!!!!!!!!!!!
 */
public class PacketDictionary {
    private static final Map<PacketType, Class<? extends Packet>> PACKET_DICTIONARY = 
            new HashMap<PacketType, Class<? extends Packet>>();
    static{
        PACKET_DICTIONARY.put(PacketType.CONNECT, ConnectPacket.class);
    }
    public static Packet translatePacketType(PacketType type, String[] data){
        Class clazz = PACKET_DICTIONARY.get(type);
        if(clazz != null){
            try {
                  return (Packet) clazz.getConstructor(String[].class).newInstance((Object)data);
                } catch (InstantiationException ex) {
                    Logger.getLogger(PacketDictionary.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalAccessException ex) {
                    Logger.getLogger(PacketDictionary.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InvocationTargetException ex) {
                    Logger.getLogger(PacketDictionary.class.getName()).log(Level.SEVERE, null, ex);
                } catch (NoSuchMethodException ex) {           
                    Logger.getLogger(PacketDictionary.class.getName()).log(Level.SEVERE, null, ex);
                } catch (SecurityException ex) {
                    Logger.getLogger(PacketDictionary.class.getName()).log(Level.SEVERE, null, ex);
                }           
            }
        return null;
    }


}
