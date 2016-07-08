package plugin.commands

import astraeus.game.event.impl.CommandEvent
import astraeus.game.event.EventSubscriber
import astraeus.game.event.EventContext

import astraeus.game.model.entity.mob.player.Player
import astraeus.game.model.entity.mob.player.PlayerRights

abstract class Command : EventSubscriber<CommandEvent> {

	override fun subscribe(context: EventContext, player: Player, event: CommandEvent) {
		
		if (player.rights.less(rights)) {
			return
		}
		
		execute(player, event.parser)
		
	}
	
	abstract fun execute(player : Player, parser : CommandParser)
	
	abstract val rights : PlayerRights

}