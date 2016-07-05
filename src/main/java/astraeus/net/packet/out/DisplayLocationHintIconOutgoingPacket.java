package astraeus.net.packet.out;

import astraeus.game.model.entity.mob.player.Player;
import astraeus.net.codec.game.GamePacketBuilder;
import astraeus.net.packet.OutgoingPacket;

public final class DisplayLocationHintIconOutgoingPacket extends OutgoingPacket {

	/**
	 * The x coordinate for this location.
	 */
	private final int x;

	/**
	 * The y coordinate for this location.
	 */
	private final int y;

	/**
	 * The height for this location.
	 */
	private final int height;

	/**
	 * The direction to face.
	 */
	private final int direction;

	/**
	 * Creates a new {@link DisplayLocationHintIconOutgoingPacket}.
	 * 
	 * @param x
	 *            The x coordinate for this icon.
	 * 
	 * @param y
	 *            The y coordinate for this icon.
	 * 
	 * @param height
	 *            The height for this icon.
	 * 
	 * @param direction
	 *            The facing direction of the arrow.
	 * 
	 */
	public DisplayLocationHintIconOutgoingPacket(int x, int y, int height, int direction) {
		super(254);
		this.x = x;
		this.y = y;
		this.height = height;
		this.direction = direction;
	}

	@Override
	public GamePacketBuilder writePacket(Player player) {
		builder.write(direction)
		.writeShort(x)
		.writeShort(y)
		.write(height);
		return builder;
	}

}
