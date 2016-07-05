package astraeus.net.packet.out;

import astraeus.game.model.entity.mob.player.Player;
import astraeus.net.codec.ByteModification;
import astraeus.net.codec.game.GamePacketBuilder;
import astraeus.net.packet.OutgoingPacket;

public final class UpdateMapRegion extends OutgoingPacket {

	public UpdateMapRegion() {
		super(73);
	}

	@Override
	public GamePacketBuilder writePacket(Player player) {
		player.getLastLocation().setLocation(player.getLocation());
		builder.writeShort(player.getLocation().getRegionalX() + 6, ByteModification.ADDITION)
		.writeShort(player.getLocation().getRegionalY() + 6);
		return builder;
	}

}
