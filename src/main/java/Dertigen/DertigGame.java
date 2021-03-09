package Dertigen;

import General.Util.GameList;
import General.Util.Builders;
import General.Util.Game;
import General.Util.UserList;
import General.DieGames.*;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;

public class DertigGame extends Game {

    //for when everybody had a turn
    boolean gameDone;

    //int to determine what number should be thrown in the bonus rounds
    public int throwNumber;

    //Variables for the end rounds
    boolean startEndGame;
    public DertigEndGame dertigEndGame = new DertigEndGame();

    //a list of all the reactions a message needs
    public ArrayList<String> reactions;

    //the size of the playing field
    int height = 5;
    int width = 17;

    //dice arrays and how many dice are left
    Dice dice = new Dice();
    public Die[] diceRolls;
    public Die[] savedDice;
    int diceLeft;

    //Standard embed texts
    String gameFooter = "Selecteer welke getallen je wilt houden en klik op \"✅\" klik bij een verkeerde selectie op \"🔄\"";
    String gameTitle = "Dertigen";

    public DertigGame(TextChannel channel, String type) {
        super(channel, type);
    }

    //Start a new player's turn
    public void startTurn() {
        dertigEndGame.endGameStarted = false;
        collecting = false;
        diceLeft = 6;
        gameDone = false;
        startEndGame = false;
        reactions = new ArrayList<>();
        reactions.add("🔄");
        diceRolls = dice.roll(diceLeft, width, height);
        savedDice = dice.initSaved();
        String message = printGame();
        for (Die diceRoll : diceRolls) {
            if (!reactions.contains(diceRoll.outcome)) {
                reactions.add(diceRoll.outcome);
            }
        }
        reactions.add("✅");
        String[] reactionsArray = new String[reactions.size()];
        Builders.sendEmbed(channel,
                gameTitle,
                message,
                gameFooter,
                sortedReactions(reactions.toArray(reactionsArray)),
                true,
                false,
                false);
    }


    public void updateTurn(){
        reactions = new ArrayList<>();
        reactions.add("🔄");
        diceRolls = dice.roll(diceLeft, width, height);
        String message = printGame();
        for (Die diceRoll : diceRolls) {
            if (!reactions.contains(diceRoll.outcome)) {
                reactions.add(diceRoll.outcome);
            }
        }
        reactions.add("✅");
        String[] reactionsArray = new String[reactions.size()];
        channel.retrieveMessageById(gameMessageID).queue( msg ->{
            msg.clearReactions().queue();
            Builders.updateGameEmbed(msg,
                    gameTitle,
                    message,
                    gameFooter,
                    sortedReactions(reactions.toArray(reactionsArray)));});
    }

    //a method to update the round if the player reacts with a dice without confirming it
    public void updateRound(String reaction) {
        saveDie(reaction);
        String finalMessage = printGame();
        channel.retrieveMessageById(gameMessageID).queue(msg -> Builders.updateGameEmbed(msg,
                gameTitle,
                finalMessage,
                gameFooter,
                null));
    }

    //Called whenever there are no dice left to pick after a round
    public void finishRound(Die[] finalDice) {
        int score = 0;
        for (Die die : finalDice) {
            switch (die.outcome) {
                case "1️⃣": score += 1; break;
                case "2️⃣": score += 2; break;
                case "3️⃣": score += 3; break;
                case "4️⃣": score += 4; break;
                case "5️⃣": score += 5; break;
                case "6️⃣": score += 6; break;
            }
        }
        if(score <= 10){
            Builders.sendEmbed(channel,
                    "Je eindscore is: " + score,
                    " dus alle andere spelers moeten een adtje trekken.",
                    "Gebruik het commando gooi om nog eens te spelen",
                    null,
                    false,
                    false,
                    false);
            startNextTurn();
        }else if (score < 30) {
            int slokken = 30 - score;
            if(slokken != 1) {
                Builders.sendEmbed(channel,
                        "Je eindscore is: " + score,
                        " dus je moet " + slokken + " slokken drinken.",
                        "",
                        null,
                        false,
                        false,
                        false);
            }else{
                Builders.sendEmbed(channel,
                        "Je eindscore is: " + score,
                        " dus je moet " + slokken + " slok drinken.",
                        "",
                        null,
                        false,
                        false,
                        false);
            }
            startNextTurn();
        } else if (score > 30) {
            throwNumber = score - 30;
            dertigEndGame.setThrowString(throwNumber);
            startEndGame = true;
            Builders.sendEmbed(channel,
                    "Je eindscore is: " + score,
                    " dus je moet " + dertigEndGame.throwString + "en gaan gooien.",
                    "Klik op \"✅\" om verder te gaan",
                    continueButton,
                    false,
                    false,
                    false);
        } else {
            Builders.sendEmbed(channel,
                    "Je eindscore is: " + score,
                    "Niemand drinkt.",
                    "",
                    null,
                    false,
                    false,
                    false);
            startNextTurn();
        }
    }

    //A method to put all dice back on the table if the player hits reset
    public void resetRound(){
        for (Die die : savedDice) {
            if (!die.outcome.equals(Die.box) && !die.saved) {
                for (Die diceRoll : diceRolls) {
                    if (diceRoll.outcome.equals(Die.table)) {
                        diceRoll.outcome = die.outcome;
                        die.outcome = Die.box;
                        String finalMessage = printGame();
                        channel.retrieveMessageById(gameMessageID).queue(msg -> Builders.updateGameEmbed(msg,
                                gameTitle,
                                finalMessage,
                                gameFooter,
                                null));
                        break;
                    }
                }
            }
        }
    }

    //A method called whenever the confirm button is used
    public void confirm(User user){
        if(gameDone && UserList.contains(channel, user)){
            channel.retrieveMessageById(gameMessageID).queue(msg -> msg.delete().queue());
            currentUser = startUser;
            startGame();
        }else if (!startEndGame) {
            boolean saved = false;
            for (Die die : savedDice) {
                if (!die.saved &&
                        !die.outcome.equals(Die.box)) {
                    saved = true;
                    die.saved = true;
                }
            }
            if (!saved) {
                Builders.sendTempError(channel,
                        "- Selecteer minstens 1 dobbelsteen om te bewaren\n",
                        6);
            } else {
                diceLeft = 0;
                for (Die die : savedDice) {
                    if (die.outcome.equals(Die.box)) {
                        diceLeft++;
                    }
                }
                if (diceLeft == 0) {
                    finishRound(savedDice);
                } else {
                    updateTurn();
                }
            }
        }else if (!dertigEndGame.endGameStarted){
            dertigEndGame.initEndGame(this);
        }else{
            dertigEndGame.updateEndGame();
        }
    }

    //util
    public void saveDie(String number) {
        for (Die diceRoll : diceRolls) {
            if (diceRoll.outcome.equals(number)) {
                for (Die die : savedDice) {
                    if (die.outcome.equals(Die.box)) {
                        die.outcome = diceRoll.outcome;
                        break;
                    }
                }
                diceRoll.outcome = Die.table;
                break;
            }
        }
    }

    public String printGame() {
        StringBuilder message = new StringBuilder();
        String newChar;
        message.append(currentUser.getAsMention()).append(" is aan de beurt\n\n");
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (j == 4) {
                    newChar = "🍺";
                } else {
                    newChar = "⬛";
                }
                for (Die diceRoll : diceRolls) {
                    if (j == diceRoll.x && i == diceRoll.y) {
                        newChar = diceRoll.outcome;
                    }
                }
                for (Die die : savedDice) {
                    if (j == die.x && i == die.y) {
                        newChar = die.outcome;
                    }
                }
                message.append(newChar);
            }
            message.append("\n");
        }
        return message.toString();
    }

    public void startNextTurn() {
        if (UserList.getNextUser(channel, currentUser).equals(startUser)) {
            gameDone();
        } else {
            currentUser = UserList.getNextUser(channel, currentUser);
            startGame();
        }
    }

    public void gameDone(){
        gameDone = true;
        StringBuilder users = new StringBuilder();
        for(User user : UserList.getUserList(channel)){
            users.append(user.getAsMention()).append("\n");
        }
        Builders.sendEmbed(channel,
                gameTitle,
                "Iedereen is geweest. Nieuwe ronde starten? Momenteel doen de volgende spelers mee:\n" + users.toString(),
                "Reageer met \"✅\" om met deze groep een nieuwe ronde te starten. Reageer met \"❌\" om het spel te beëindigen",
                newRoundReactions,
                true,
                false,
                false
        );
    }

    public void processReaction(String reaction, User user){
        switch (reaction) {
            case "1️⃣":
            case "2️⃣":
            case "3️⃣":
            case "4️⃣":
            case "5️⃣":
            case "6️⃣":
                if (user.equals(currentUser)) {
                    updateRound(reaction);
                }
                break;
            //add player button
            case "➕":
                if (!UserList.getUserList(channel).contains(user)) {
                    GameList.getGame(channel).addPlayer(user);
                }
                break;
            //confirm players button
            case "🍻":
                if (GameList.getGame(channel).collecting && user.equals(startUser)) {
                    startGame();
                } else {
                    Builders.sendTempMessage(channel, "Proost! 🍻", 5);
                }
                break;

            //reset button
            case "🔄":
                if (user.equals(currentUser)) {
                    resetRound();
                }
                break;
            //confirm button
            case "✅":
                if (UserList.contains(channel, user)) {
                    confirm(user);
                }
                break;
            //stop button
            case "🛑":
                if (GameList.getGame(channel).stopMessageID != null && UserList.contains(channel, user)) {
                    channel.retrieveMessageById(GameList.getGame(channel).stopMessageID).queue(msg -> {
                        msg.clearReactions().queue();
                        Builders.updateGameEmbed(msg,
                                "Gestopt!",
                                "De ronde die in dit kanaal bezig was is succesvol gestopt!",
                                "Gebruik -gooi om een nieuwe ronde te starten",
                                null);
                    });
                    UserList.removeUserList(channel);
                    GameList.removeGame(channel);
                }
                break;
            //dont continue button
            case "❌":
                if(gameDone && UserList.contains(channel, user)){
                    channel.retrieveMessageById(gameMessageID).queue(msg -> {
                        msg.clearReactions().queue();
                        Builders.updateGameEmbed(msg,
                                "Gestopt!",
                                "De ronde die in dit kanaal bezig was is succesvol gestopt!",
                                "Gebruik -gooi om een nieuwe ronde te starten",
                                null);
                        UserList.removeUserList(channel);
                        GameList.removeGame(channel);
                    });
                }
            //cancel button
            case "✖":
                if (GameList.getGame(channel).stopMessageID != null && UserList.contains(channel, user)) {
                    channel.retrieveMessageById(GameList.getGame(channel).stopMessageID).queue(msg -> msg.delete().queue());
                }
                break;
        }
    }

    public void setEndGameMessageID(Message endGameMessage) {dertigEndGame.endGameMessageID = endGameMessage.getIdLong();}

}