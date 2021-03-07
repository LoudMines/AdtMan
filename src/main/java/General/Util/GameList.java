package General.Util;

import net.dv8tion.jda.api.entities.MessageChannel;

import java.util.HashMap;

public class GameList {

    private static final HashMap<MessageChannel, Game> games = new HashMap<>();

    public static void setGame(MessageChannel messageChannel, Game game) {
        games.put(messageChannel, game);
    }

    public static boolean hasGame(MessageChannel messageChannel) {
        return games.containsKey(messageChannel);
    }

    public static Game getGame(MessageChannel messageChannel) {
        return games.get(messageChannel);
    }

    public static void removeGame(MessageChannel messageChannel) {
        games.remove(messageChannel);
    }
}
