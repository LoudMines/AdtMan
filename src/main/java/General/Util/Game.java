package General.Util;

import com.google.inject.internal.Nullable;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.util.Arrays;

public abstract class Game {

    //Type of the current game
    String type;

    //button arrays
    protected String[] continueButton = {"✅"};
    protected String[] joinReactions = {"➕", "🍻"};
    protected String[] newRoundReactions = {"❌", "✅"};

    //the channel where all the messages for this game will be sent and the message IDs
    public TextChannel channel;
    public long gameMessageID;
    public Long stopMessageID;

    //variables for collecting players
    public boolean collecting = false;
    public User startUser;
    public User currentUser;

    public Game(TextChannel channel, String type){
        this.channel = channel;
        this.type = type;
    }

    //player tracking methods
    public void setPlayerList(User user){
        collecting = true;
        Builders.sendEmbed(channel,
                "Nieuwe ronde " + type,
                user.getAsMention() + " begint een nieuwe ronde " + type + ".",
                "Reageer met \"➕\" op dit bericht om mee te doen en met \"" + getEmote(type) + "\" als iedereen heeft gereageerd.",
                joinReactions,
                true,
                false,
                false);
        UserList.createUserList(channel);
        startUser = user;
        currentUser = startUser;
        UserList.addUser(channel, user);
    }

    public void addPlayer(User user){
        UserList.addUser(channel, user);
        channel.retrieveMessageById(gameMessageID).queue(msg -> {
            String playerString = "";
            for(User listUser : UserList.getUserList(channel)){
                if(listUser != startUser) {
                    playerString += listUser.getAsMention() + " \n";
                }
            }
            Builders.updateGameEmbed(
                    msg,
                    "Nieuwe ronde " + type,
                    startUser.getAsMention() + " begint een nieuwe ronde " + type + ". Momenteel doen de volgende users ook mee: \n" + playerString,
                    "Reageer met \"➕\" op dit bericht om ook mee te doen en reageer met \"" + getEmote(type) + "\" als alle deelnemers in de lijst staan.",
                    joinReactions
            );});
    }

    public void removePlayer(User user){
        UserList.removeUser(channel, user);
        channel.retrieveMessageById(gameMessageID).queue(msg -> {
            String playerString = "";
            for(User listUser : UserList.getUserList(channel)){
                if(listUser != startUser) {
                    playerString += listUser.getAsMention() + " \n";
                }
            }
            Builders.updateGameEmbed(
                    msg,
                    "Nieuwe ronde " + type,
                    startUser.getAsMention() + " begint een nieuwe ronde " + type + ". Momenteel doen de volgende users ook mee: \n" + playerString,
                    "Reageer met \"➕\" op dit bericht om ook mee te doen en reageer met \"" + getEmote(type) + "\" als alle deelnemers in de lijst staan.",
                    joinReactions
            );});
    }

    public void startGame(){
        collecting = false;
        startTurn();
    }

    //Round/turn voids.
    //A turn is someones turn from start to finish. A round is a single round within such a turn.
    public abstract void startTurn();

    public abstract void updateTurn();

    public abstract void skipTurn(User user);

    public abstract void updateRound(@Nullable String reaction);

    public abstract void processReaction(String reaction, User user);

    public abstract void resetRound();

    public abstract void confirm(User user);

    //for when one player is done with their turn and some values need to be reset.
    public abstract void startNextTurn();

    public abstract void gameDone();
    //util
    public String[] sortedReactions(@Nullable String[] reactions) {
        if (reactions != null) {
            if (reactions.length > 2) {
                String[] numbers = Arrays.copyOfRange(reactions, 1, reactions.length - 1);
                Arrays.sort(numbers);
                System.arraycopy(numbers, 0, reactions, 1, reactions.length - 1 - 1);
            }
        }
        return reactions;
    }

    //getters
    public String getEmote(String type){
        String emote = "";
        switch (type){
            case "dertigen":
                emote = "🍻";
                break;
            case "mexen":
                emote = "🎲";
                break;
        }
        return emote;
    }

    //setters
    public void setGameMessageID(Message gameMessage) {gameMessageID = gameMessage.getIdLong();}

    public void setStopMessageID(Message stopMessage) {stopMessageID = stopMessage.getIdLong();}

}
