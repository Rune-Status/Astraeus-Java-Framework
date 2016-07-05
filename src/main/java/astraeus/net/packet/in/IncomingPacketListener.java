package astraeus.net.packet.in;

import astraeus.game.model.entity.mob.player.Player;
import astraeus.net.packet.IncomingPacket;

/**
 * The interface that allows any implementing {@Packet}s. The ability to be
 * intercepted as an incoming packets.
 * 
 * @author SeVen
 */
public interface IncomingPacketListener {

    /**
     * Handles the packet that has just been received.
     * 
     * @param player
     *            The player receiving this packet.
     * 
     * @param packet
     *            The packet that has been received.
     */
    public void handlePacket(Player player, IncomingPacket packet);
}
