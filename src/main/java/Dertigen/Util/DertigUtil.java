package Dertigen.Util;

import Dertigen.DertigGame;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.util.HashMap;

public class DertigUtil {

    private static final HashMap<MessageChannel, DertigGame> games = new HashMap<>();

    public static void setGame(MessageChannel messageChannel, DertigGame game) {
        games.put(messageChannel, game);
    }

    public static boolean hasGame(MessageChannel messageChannel) {
        return games.containsKey(messageChannel);
    }

    public static DertigGame getGame(MessageChannel messageChannel) {
        return games.get(messageChannel);
    }

    public static void removeGame(MessageChannel messageChannel) {
        games.remove(messageChannel);
    }
}
