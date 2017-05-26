/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.pakets;

import java.net.Socket;

/**
 *
 * @author itirium
 */
public interface PacketListener {
    public void packetSent(Packet packet, Socket client);
    public void packetRecieved(Packet packet, Socket client);
}
