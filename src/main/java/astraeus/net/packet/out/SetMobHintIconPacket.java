package astraeus.net.packet.out;

import astraeus.game.model.entity.mob.Mob;
import astraeus.game.model.entity.mob.player.Player;
import astraeus.net.codec.game.GamePacketBuilder;
import astraeus.net.packet.OutgoingPacket;

/**
 * The {@link OutgoingPacket} that sends a hint arrow on an {@code entity}.
 * 
 * @author SeVen
 */
public final class SetMobHintIconPacket extends OutgoingPacket {

	/**
	 * The entity to display the arrow on.
	 */
	private final Mob entity;
	
	private final boolean reset;
	
	/**
	 * Creates a new {@link SetMobHintIconPacket}.
	 * 
	 * @param entity
	 * 		The entity to display the arrow on.
	 */
	public SetMobHintIconPacket(Mob entity) {
		this(entity, false);
	}

	/**
	 * Creates a new {@link SetMobHintIconPacket}.
	 * 
	 * @param entity
	 * 		The entity to display the arrow on.
	 */
	public SetMobHintIconPacket(Mob entity, boolean reset) {
		super(254);
		this.entity = entity;
		this.reset = reset;
	}

	@Override
	public GamePacketBuilder writePacket(Player player) {
		builder.write(entity.isPlayer() ? reset ? -1 : 10 : reset ? -1 : 1)
		.writeShort(entity.getSlot())
		.write(0 >> 16)
		.write(0 >> 8)
		.write(0);
		return builder;
	}

}
