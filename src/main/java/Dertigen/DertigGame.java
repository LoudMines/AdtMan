package Dertigen;

import Dertigen.Util.DertigUtil;
import General.Util.Builders;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.ArrayList;

public class DertigGame {

    //int to determine what number should be thrown in the bonus rounds and an array with the continue button to snd to the builders
    public int throwNumber;
    String[] continueButton = {"✅"};

    //Variables for the end rounds
    boolean startEndGame = false;
    public DertigEndGame dertigEndGame = new DertigEndGame();

    //the channel where all the messages for this game will be sent and the message ID
    public TextChannel channel;
    public long gameMessageID;
    public Long stopMessageID;

    //a list of all the reactions a message needs
    public ArrayList<String> reactions;

    //the size of the playing field
    int height = 5;
    int width = 17;

    //dice arrays and how many dice are left
    Dice dice = new Dice();
    public Die[] diceRolls;
    public Die[] savedDice;
    int diceLeft = 6;

    //Standard embed texts
    String gameFooter = "Selecteer welke getallen je wilt houden en klik op \"✅\" klik bij een verkeerde selectie op \"🔄\"";
    String gameTitle = "Dertigen";

    public DertigGame(TextChannel channel){
        this.channel = channel;
    }

    public void startGame(){
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
                reactions.toArray(reactionsArray),
                true,
                false,
                false);
    }

    public void updateRound(String reaction){
        saveDie(reaction);
        String finalMessage = printGame();
        channel.retrieveMessageById(gameMessageID).queue( msg ->Builders.updateGameEmbed(msg,
                gameTitle,
                finalMessage,
                gameFooter,
                null));
    }

    public void updateGame(){
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
                reactions.toArray(reactionsArray));});
    }

    public String printGame(){
        String message = "";
        String newChar;
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
                message += newChar;
            }
            message += "\n";
        }
        return message;
    }

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

    public void saveDie(String number){
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

    public void confirm(){
        if (!startEndGame) {
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
                    endGame(savedDice);
                } else {
                    updateGame();
                }
            }
        }else if (!dertigEndGame.endGameStarted){
            dertigEndGame.initEndGame(this);
        }else{
            dertigEndGame.updateEndGame();
        }
    }

    public void endGame(Die[] finalDice) {
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
        if (score < 30) {
            int slokken = 30 - score;
            Builders.sendEmbed(channel,
                    "Je eindscore is: " + score,
                    " dus je moet " + slokken + " slokken drinken.",
                    "Gebruik het commando gooi om nog eens te spelen",
                    null,
                    false,
                    false,
                    false);
            DertigUtil.removeGame(channel);
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
                    "Gebruik het commando gooi om nog eens te spelen",
                    null,
                    false,
                    false,
                    false);
            DertigUtil.removeGame(channel);
        }
    }

    public void setGameMessageID(Message gameMessage) {
        gameMessageID = gameMessage.getIdLong();
    }

    public void setEndGameMessageID(Message endGameMessage){
        dertigEndGame.endGameMessageID = endGameMessage.getIdLong();
    }

    public void setStopMessageID(Message stopMessage) {
        stopMessageID = stopMessage.getIdLong();
    }

}