package astraeus.net.packet.in.impl;

import astraeus.game.model.entity.mob.player.Player;
import astraeus.net.packet.IncomingPacket;
import astraeus.net.packet.IncomingPacket.IncomingPacketOpcode;
import astraeus.net.packet.in.IncomingPacketListener;

/**
 * The {@link IncomingPacket} responsible for dialogues.
 * 
 * @author SeVen
 */
@IncomingPacketOpcode(40)
public class DialoguePacket implements IncomingPacketListener {

	@Override
	public void handlePacket(Player player, IncomingPacket packet) {

		player.getDialogueFactory().execute();

	}
}
