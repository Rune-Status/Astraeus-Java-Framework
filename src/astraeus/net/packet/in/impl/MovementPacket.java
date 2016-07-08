package astraeus.net.packet.in.impl;

import astraeus.game.model.Location;
import astraeus.game.model.entity.mob.Mob;
import astraeus.game.model.entity.mob.player.Player;
import astraeus.net.codec.ByteModification;
import astraeus.net.codec.ByteOrder;
import astraeus.net.codec.game.GamePacketReader;
import astraeus.net.packet.IncomingPacket;
import astraeus.net.packet.in.IncomingPacketListener;

/**
 * The {@link IncomingPacket}'s responsible for player movement.
 * 
 * @author SeVen
 */
@IncomingPacket.IncomingPacketOpcode({ IncomingPacket.WALK_ON_COMMAND, IncomingPacket.REGULAR_WALK, IncomingPacket.MAP_WALK })
public class MovementPacket implements IncomingPacketListener {

	@Override
	public void handlePacket(Player player, IncomingPacket packet) {

		int size = packet.getLength();

		if (packet.getOpcode() == IncomingPacket.MAP_WALK) {
			// this is an anti-cheat
			size -= 14;
		}
		
		if (player.getInteractingEntity() != null) {
			Mob other = player.getInteractingEntity();
			other.setInteractingEntity(null);
			player.setInteractingEntity(null);
		}
		
//		if (player.getDialogue().isPresent()) {
//			player.send(new RemoveWidgetPacket());
//			player.getDialogueFactory().clear();
//		}
		
		GamePacketReader reader = packet.getReader();

		int steps = (size - 5) / 2;
		int[][] path = new int[steps][2];
		int firstStepX = reader.readShort(ByteOrder.LITTLE, ByteModification.ADDITION);

		for (int i = 0; i < steps; i++) {
			path[i][0] = reader.readByte();
			path[i][1] = reader.readByte();
		}

		int firstStepY = reader.readShort(ByteOrder.LITTLE);
		player.getMovement().reset();
		player.getMovement().setRunningQueueEnabled(reader.readByte(ByteModification.NEGATION) == 1);
		player.getMovement().addToPath(new Location(firstStepX, firstStepY));

		for (int i = 0; i < steps; i++) {
			path[i][0] += firstStepX;
			path[i][1] += firstStepY;
			player.getMovement().addToPath(new Location(path[i][0], path[i][1]));
		}
		player.getMovement().finish();
	}
}
