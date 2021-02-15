package General.Util;
import Dertigen.Util.DertigUtil;
import com.google.inject.internal.Nullable;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.util.concurrent.TimeUnit;

public class Builders {

    static String tasteLogo ="\n" +
            "⬛🟩⬛🟩⬛🟩⬛\n" +
            "🟥🟩🟥🟥🟥🟩🟥\n" +
            "⬛🟩⬛🟩⬛🟩⬛\n" +
            "🟥🟥🟥⬜🟥🟥🟥\n" +
            "⬛🟩⬛🟩⬛🟩⬛\n" +
            "🟥🟩🟥🟥🟥🟩🟥\n" +
            "⬛🟩⬛🟩⬛🟩⬛\n"+
            "⬛⬛⬛🟩⬛⬛⬛";

    public static void sendEmbed(MessageChannel channel, String title, String description, String footer, @Nullable String[] reactions, boolean setGameID, boolean setEndGameID, boolean setStopID) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(title);
        embed.setDescription(description);
        embed.setFooter(footer);
        channel.sendMessage(embed.build()).queue(message -> {
            if (reactions != null) {
                for (String reaction : reactions) {
                    message.addReaction(reaction).queue();
                }
            }
            if (setGameID) {
                DertigUtil.getGame(channel).setGameMessageID(message);
            }
            if (setEndGameID) {
                DertigUtil.getGame(channel).setEndGameMessageID(message);
            }
            if (setStopID) {
                DertigUtil.getGame(channel).setStopMessageID(message);
            }
        });
    }

    public static void sendTasteMessage(MessageChannel channel, String title, String description, String footer, @Nullable String[] reactions) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(title);
        embed.setDescription(description + tasteLogo);
        embed.setFooter(footer);
        channel.sendMessage(embed.build()).queue(message -> {
            if (reactions != null) {
                for (String reaction : reactions) {
                    message.addReaction(reaction).queue();
                }
            }
        });
    }

    public static void sendTempError(MessageChannel channel, String warning, int seconds) {
        channel.sendMessage(
                "```diff\n" +
                warning +
                "```").queue(msg -> msg.delete().queueAfter(seconds, TimeUnit.SECONDS));
    }

    public static void updateGameEmbed(Message message, String title, String game, String footer, @Nullable String[] reactions) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(title);
        embed.setDescription(game);
        embed.setFooter(footer);
        message.editMessage(embed.build()).queue(msg -> {
            if(reactions != null) {
                for (String reaction : reactions) {
                    message.addReaction(reaction).queue();
                }
            }
        });
    }
}
