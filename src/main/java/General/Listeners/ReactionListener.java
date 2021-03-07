package General.Listeners;

import Dertigen.DertigGame;
import General.Util.GameList;
import General.Util.Builders;
import General.Util.UserList;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ReactionListener extends ListenerAdapter {


    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {
        User user = event.getUser();

        if (user.isBot()) {
            return;
        }

        TextChannel channel = event.getChannel();

        channel.retrieveMessageById(event.getMessageId()).queue(message -> {
            //check if the message was sent by a bot
            if (message.getAuthor().getId().equals(event.getJDA().getSelfUser().getId())) {
                //if the channel has no game
                if (!GameList.hasGame(channel)) {
                    //if the reaction emote is that of dertigen, start a game otherwise send an error.
                    if (event.getReactionEmote().toString().equals("RE:U+1f37b")) {
                        DertigGame game = new DertigGame(channel, "dertigen");
                        GameList.setGame(channel, game);
                        GameList.getGame(channel).setPlayerList(event.getUser());
                    //add more game emotes here using elif statements
                    } else {
                        Builders.sendTempError(channel,
                                "- Er is geen actieve ronde in dit textkanaal, Start een nieuwe ronde met \"-gooi\".\n",
                                6);
                    }
                } else {
                    GameList.getGame(channel).processReaction(event.getReactionEmote().getEmoji(), event.getUser());
                    event.getReaction().removeReaction(event.getUser()).queue();
                }
            }
        });
    }
}