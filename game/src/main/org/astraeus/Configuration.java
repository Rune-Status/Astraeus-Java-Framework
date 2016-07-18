package astraeus;

import com.moandjiezana.toml.Toml;

import java.io.File;

/**
 * The class that contains the main configuration-related constants.
 *
 * @author Seven
 */
public class Configuration {

    static {
        Toml parser = new Toml().read(new File("./settings.toml"));

        try {
            SERVER_NAME = parser.getString("game.server_name");
            GAME_PORT = Math.toIntExact(parser.getLong("network.game_port"));
            ADMIN_CAN_TRADE = parser.getBoolean("game.admin_can_trade");
        } catch(Exception e){
            throw new ExceptionInInitializerError(e);
        }
    }

    /**
     * The name of the server
     */
    public static final String SERVER_NAME;

    /**
     * The port of the server.
     */
    public static final int GAME_PORT;

    /**
     * The npc bits for the server which can handle 6755 npcs.
     */
    public static final int NPC_BITS = 12;

    /**
     * Location of the data directory.
     */
    public static final String DATA_DIR = "./Data/";

    /**
     * Toggles the ability for admins to trade
     */
    public static final boolean ADMIN_CAN_TRADE;

    /**
     * Cheap hack for a worlds list fix
     */
    public static final boolean WORLD_LIST_FIX = false;
}