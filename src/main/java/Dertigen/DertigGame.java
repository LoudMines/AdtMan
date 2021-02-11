package Dertigen;

import Dertigen.Util.DertigUtil;
import General.Bot;
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
        reactions = new ArrayList<String>();
        reactions.add("🔄");
        diceRolls = dice.roll(diceLeft, width, height);
        savedDice = dice.initSaved();
        String message = printGame();
        for (int i = 0; i < diceRolls.length; i++) {
            if (!reactions.contains(diceRolls[i].outcome)){
                reactions.add(diceRolls[i].outcome);
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
        reactions = new ArrayList<String>();
        reactions.add("🔄");
        diceRolls = dice.roll(diceLeft, width, height);
        String message = printGame();
        for (int i = 0; i < diceRolls.length; i++) {
            if (!reactions.contains(diceRolls[i].outcome)){
                reactions.add(diceRolls[i].outcome);
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
                for (int k = 0; k < diceRolls.length; k++) {
                    if (j == diceRolls[k].x && i == diceRolls[k].y) {
                        newChar = diceRolls[k].outcome;
                    }
                }
                for (int k = 0; k < savedDice.length; k++) {
                    if (j == savedDice[k].x && i == savedDice[k].y) {
                        newChar = savedDice[k].outcome;
                    }
                }
                message += newChar;
            }
            message += "\n";
        }
        return message;
    }

    public void resetRound(){
        for (int i = 0; i < savedDice.length; i++) {
            if (!savedDice[i].outcome.equals(Die.box) && !savedDice[i].saved) {
                    for (int j = 0; j < diceRolls.length; j++) {
                        if (diceRolls[j].outcome.equals(Die.table)) {
                            diceRolls[j].outcome = savedDice[i].outcome;
                            savedDice[i].outcome = Die.box;
                            String finalMessage = printGame();
                            channel.retrieveMessageById(gameMessageID).queue( msg ->Builders.updateGameEmbed(msg,
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
        for(int i = 0; i < diceRolls.length; i++){
            if(diceRolls[i].outcome.equals(number)){
                for (int j = 0; j < savedDice.length; j++) {
                    if (savedDice[j].outcome.equals(Die.box)) {
                        savedDice[j].outcome = diceRolls[i].outcome;
                        break;
                    }
                }
                diceRolls[i].outcome = Die.table;
                break;
            }
        }
    }

    public void confirm(){
        if (!startEndGame) {
            boolean saved = false;
            for (int i = 0; i < savedDice.length; i++) {
                if (!savedDice[i].saved &&
                        !savedDice[i].outcome.equals(Die.box)) {
                    saved = true;
                    savedDice[i].saved = true;
                }
            }
            if (!saved) {
                Builders.sendTempError(channel,
                                "- Selecteer minstens 1 dobbelsteen om te bewaren\n",
                        10);
            } else {
                diceLeft = 0;
                for (int i = 0; i < savedDice.length; i++) {
                    if (savedDice[i].outcome.equals(Die.box)) {
                        diceLeft++;
                    }
                }
                if (diceLeft == 0) {
                    endGame(savedDice);
                } else {
                    updateGame();
                }
            }
        }else if (!dertigEndGame.endGameStarted && startEndGame){
            dertigEndGame.startEndGame(this);
        }else if (dertigEndGame.endGameStarted && startEndGame){
            dertigEndGame.updateEndGame();
        }
    }

    public void endGame(Die[] finalDice) {
        int score = 0;
        for (int i = 0; i < finalDice.length; i++) {
            if (finalDice[i].outcome.equals("1️⃣")) {
                score += 1;
            } else if (finalDice[i].outcome.equals("2️⃣")) {
                score += 2;
            } else if (finalDice[i].outcome.equals("3️⃣")) {
                score += 3;
            } else if (finalDice[i].outcome.equals("4️⃣")) {
                score += 4;
            } else if (finalDice[i].outcome.equals("5️⃣")) {
                score += 5;
            } else if (finalDice[i].outcome.equals("6️⃣")) {
                score += 6;
            }
        }
        if (score < 30) {
            int slokken = 30 - score;
            Builders.sendEmbed(channel,
                    "Je eindscore is: " + Integer.toString(score),
                    " dus je moet " + Integer.toString(slokken) + " slokken drinken.",
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
                    "Je eindscore is: " + Integer.toString(score),
                    " dus je moet " + dertigEndGame.throwString + "en gaan gooien.",
                    "Klik op \"✅\" om verder te gaan",
                    continueButton,
                    false,
                    false,
                    false);
        } else {
            Builders.sendEmbed(channel,
                    "Je eindscore is: " + Integer.toString(score),
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