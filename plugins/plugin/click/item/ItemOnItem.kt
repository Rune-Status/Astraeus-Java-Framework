package plugin.click.item

import astraeus.game.event.EventContext
import astraeus.game.event.EventSubscriber
import astraeus.game.event.SubscribesTo
import astraeus.game.event.impl.ItemOnItemEvent
import astraeus.game.model.entity.mob.player.Player
import astraeus.game.model.entity.mob.player.PlayerRights
import astraeus.game.model.entity.mob.player.attribute.Attribute

import astraeus.net.packet.out.SetWidgetConfigPacket
import astraeus.net.packet.out.ServerMessagePacket

@SubscribesTo(ItemOnItemEvent::class)
class ItemOnItem : EventSubscriber<ItemOnItemEvent> {	

	override fun subscribe(context: EventContext, player: Player, event: ItemOnItemEvent) {
		
        if (player.rights.greaterOrEqual(PlayerRights.DEVELOPER) && player.attr().get(Attribute.DEBUG)) {
            player.send(ServerMessagePacket("[ItemOnItem] - itemUsed: ${event.used.id} usedWith: ${event.usedWith.id}}"));
        }	
		
		
	}
	
}