
package astraeus.game.model.entity.mob.player;

import astraeus.game.model.entity.item.Item;
import astraeus.game.model.entity.mob.MobAnimation;
import astraeus.net.packet.out.DisplayChatBoxWidgetPacket;
import astraeus.net.packet.out.SetItemOnInterfaceSlotPacket;
import astraeus.net.packet.out.SetSideBarWidgetPacket;
import astraeus.net.packet.out.SetWidgetStringPacket;

/**
 * A static-utility class that contains useful methods for {@link Player}s.
 * 
 * @author SeVen
 */
public class Players {

      private static final int ATTACK_TAB = 0;
      private static final int SKILL_TAB = 1;
      private static final int QUEST_TAB = 2;
      private static final int INVENTORY_TAB = 3;
      private static final int EQUIPMENT_TAB = 4;
      private static final int PRAYER_TAB = 5;
      private static final int MAGIC_TAB = 6;
      private static final int CLAN_TAB = 7;
      private static final int FRIENDS_TAB = 8;
      private static final int IGNORE_TAB = 9;
      private static final int LOGOUT_TAB = 10;
      private static final int WRENCH_TAB = 11;
      private static final int EMOTE_TAB = 12;
      private static final int MUSIC_TAB = 13;

      /**
       * Creates all the side-bar interfaces for a {@code player}.
       * 
       * @param player
       *    The player to create the side-bar interfaces for.
       *    
       * @param enable
       *    The flag that denotes to toggle these side-bar interfaces. 
       */
      public static void createSideBarInterfaces(Player player, boolean enable) {
            if (enable) {
                  player.send(new SetSideBarWidgetPacket(ATTACK_TAB, 2423));
                  player.send(new SetSideBarWidgetPacket(SKILL_TAB, 3917));
                  player.send(new SetSideBarWidgetPacket(QUEST_TAB, 638));
                  player.send(new SetSideBarWidgetPacket(INVENTORY_TAB, 3213));
                  player.send(new SetSideBarWidgetPacket(EQUIPMENT_TAB, 1644));
                  player.send(new SetSideBarWidgetPacket(PRAYER_TAB, 5608));
                  player.send(new SetSideBarWidgetPacket(MAGIC_TAB, 1151));
                  player.send(new SetSideBarWidgetPacket(CLAN_TAB, -1));
                  player.send(new SetSideBarWidgetPacket(FRIENDS_TAB, 5065));
                  player.send(new SetSideBarWidgetPacket(IGNORE_TAB, 5715));
                  player.send(new SetSideBarWidgetPacket(LOGOUT_TAB, 2449));
                  player.send(new SetSideBarWidgetPacket(WRENCH_TAB, 904));
                  player.send(new SetSideBarWidgetPacket(EMOTE_TAB, 147));
                  player.send(new SetSideBarWidgetPacket(MUSIC_TAB, 962));
            }
      }

      /**
       * Resets animations for a {@code player}.
       * 
       * @param player
       *    The player who's animations to reset.
       */
      public static void resetPlayerAnimation(Player player) {
            player.getMobAnimation().setWalk(MobAnimation.PLAYER_WALK);
            player.getMobAnimation().setStand(MobAnimation.PLAYER_STAND);
            player.getMobAnimation().setTurn(MobAnimation.PLAYER_TURN);
            player.getMobAnimation().setTurn180(MobAnimation.PLAYER_TURN_180);
            player.getMobAnimation().setTurn90CCW(MobAnimation.PLAYER_TURN_90_CCW);
            player.getMobAnimation().setTurn90CW(MobAnimation.PLAYER_TURN_90_CW);
            player.getMobAnimation().setRun(MobAnimation.PLAYER_RUN);
      }

      /**
       * Sends the destroy Item interface to a {@code player}.
       * 
       * @param player
       *    The player to destroy the item for.
       *    
       * @param item
       *    The item being destroyed.
       */
      public static void destroyItem(Player player, Item item) {
            player.send(new SetItemOnInterfaceSlotPacket(14171, item, 0));
            player.send(new SetWidgetStringPacket("Are you sure you want to drop this item?", 14174));
            player.send(new SetWidgetStringPacket("Yes.", 14175));
            player.send(new SetWidgetStringPacket("No.", 14176));
            player.send(new SetWidgetStringPacket("", 14177));
            player.send(new SetWidgetStringPacket("This item is valuable, you will not", 14182));
            player.send(new SetWidgetStringPacket("get it back once lost.", 14183));
            player.send(new SetWidgetStringPacket(item.getName(), 14184));
            player.send(new DisplayChatBoxWidgetPacket(14170));
      }



}
