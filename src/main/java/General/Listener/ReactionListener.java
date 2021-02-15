package General.Listener;

import Dertigen.DertigGame;
import Dertigen.Util.DertigUtil;
import General.Util.Builders;
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
            if(message.getAuthor().getId().equals(event.getJDA().getSelfUser().getId())) {
                if (!DertigUtil.hasGame(channel)) {
                    if(event.getReactionEmote().toString().equals("RE:U+1f37b")){
                        DertigGame game = new DertigGame(channel);
                        DertigUtil.setGame(channel, game);
                        DertigUtil.getGame(channel).startGame();
                    }else {
                        Builders.sendTempError(channel,
                                "- Er is geen actieve ronde in dit textkanaal, Start een nieuwe ronde met \"-gooi\".\n",
                                6);
                    }
                    } else {
                    switch (event.getReactionEmote().toString()) {
                        case "RE:U+31U+fe0fU+20e3":
                            DertigUtil.getGame(channel).updateRound("1️⃣");
                            break;
                        case "RE:U+32U+fe0fU+20e3":
                            DertigUtil.getGame(channel).updateRound("2️⃣");
                            break;
                        case "RE:U+33U+fe0fU+20e3":
                            DertigUtil.getGame(channel).updateRound("3️⃣");
                            break;
                        case "RE:U+34U+fe0fU+20e3":
                            DertigUtil.getGame(channel).updateRound("4️⃣");
                            break;
                        case "RE:U+35U+fe0fU+20e3":
                            DertigUtil.getGame(channel).updateRound("5️⃣");
                            break;
                        case "RE:U+36U+fe0fU+20e3":
                            DertigUtil.getGame(channel).updateRound("6️⃣");
                            break;
                        //reset button
                        case "RE:U+1f504":
                            DertigUtil.getGame(channel).resetRound();
                            break;
                        //confirm button
                        case "RE:U+2705":
                            DertigUtil.getGame(channel).confirm();
                            break;
                        //stop button
                        case "RE:U+1f6d1":
                            if(DertigUtil.getGame(channel).stopMessageID != null){
                                channel.retrieveMessageById(DertigUtil.getGame(channel).stopMessageID).queue(msg ->{
                                msg.clearReactions().queue();
                                Builders.updateGameEmbed(msg,
                                "Gestopt!",
                                "De ronde die in dit kanaal bezig was is succesvol gestopt!",
                                "Gebruik -gooi om een nieuwe ronde te starten",
                                null);
                                });
                                DertigUtil.removeGame(channel);
                            }
                            break;
                        //cancel button
                        case "RE:U+2716":
                            if(DertigUtil.getGame(channel).stopMessageID != null){
                                channel.retrieveMessageById(DertigUtil.getGame(channel).stopMessageID).queue(msg -> msg.delete().queue());
                            }
                            break;
                    }
                    event.getReaction().removeReaction(event.getUser()).queue();
                }
            }
        });
    }
}