package astraeus.net.packet.in.impl;

import astraeus.game.model.entity.mob.player.Player;
import astraeus.net.packet.IncomingPacket;
import astraeus.net.packet.in.IncomingPacketListener;
import main.astraeus.content.clicking.magic.MagicOnItem;
import astraeus.net.codec.ByteModification;
import astraeus.net.codec.game.GamePacketReader;

/**
 * The {@link IncomingPacket} responsible for using magic on inventory items.
 * 
 * @author SeVen
 */
@IncomingPacket.IncomingPacketOpcode(IncomingPacket.MAGIC_ON_ITEMS)
public class MagicOnItemPacket implements IncomingPacketListener {

	@Override
	public void handlePacket(Player player, IncomingPacket packet) {
		GamePacketReader reader = packet.getReader();

		final int slot = reader.readShort();
		final int itemId = reader.readShort(ByteModification.ADDITION);
		final int childId = reader.readShort();
		final int spellId = reader.readShort(ByteModification.ADDITION);

		new MagicOnItem(player, itemId, slot, childId, spellId).handleAction();
	}

}
