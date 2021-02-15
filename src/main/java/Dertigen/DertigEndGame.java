package Dertigen;

import Dertigen.Util.DertigUtil;
import General.Util.Builders;
import net.dv8tion.jda.api.entities.TextChannel;

public class DertigEndGame {

    //Variables from the game this endGame got started from
    DertigGame game;
    int height;
    int width;
    TextChannel channel;
    String[] continueButton = {"✅"};

    //Standard embed strings
    String endGameTitle = "Dertigen";

    //dice arrays and how many dice are left
    Dice dice = new Dice();
    public Die[] diceRolls;
    public Die[] savedDice;
    int diceLeft = 6;

    //variables to determine where in the end game we are currently
    boolean endGameStarted = false;
    String throwString;
    int throwNumber;
    int vorigeSlokken = 0;
    //boolean roundDone = false;
    int correctDice = 0;

    //variables for the current used message
    public long endGameMessageID;

    public void initEndGame(DertigGame dertigGame){
        //initiate all the required values to those of the game this was started from.
        game = dertigGame;
        width = game.width;
        height = game.height;
        channel = game.channel;

        //initiate the dice to all the required values.
        savedDice = dice.initSaved();
        diceLeft = 6;
        diceRolls = dice.roll(diceLeft, width, height);

        //send the endGame message for the first time and set it's id
        Builders.sendEmbed(channel,
                endGameTitle,
                printEndGame(),
                "Je moet " + throwString + "en gooien. Klik op \"✅\" om door te gaan",
                continueButton,
                false,
                true,
                false);
        endGameStarted = true;
    }

    public void updateEndGame() {
        if (checkDice() != 0){
            saveDice(throwString);
            channel.retrieveMessageById(endGameMessageID).queue(msg -> Builders.updateGameEmbed(
                    msg,
                    endGameTitle,
                    printEndGame(),
                    "Je moest " + throwString + "en gooien. Gebruik \"✅\" om door te gaan",
                    continueButton
            ));
            if(diceLeft == 0){
                roundDone();
            }
            diceRolls = dice.roll(diceLeft, width, height);
        }else{
            completeGame();
        }

    }

    public void saveDice(String number) {
        for (Die diceRoll : diceRolls) {
            if (diceRoll.outcome.equals(number)) {
                for (Die die : savedDice) {
                    if (die.outcome.equals(Die.box)) {
                        die.outcome = diceRoll.outcome;
                        diceLeft--;
                        break;
                    }
                }
                diceRoll.outcome = Die.table;
            }
        }
    }

    public void roundDone(){
        vorigeSlokken = vorigeSlokken + 6 * throwNumber;
        throwNumber --;
        setThrowString(throwNumber);
        diceLeft = 6;
        savedDice = dice.initSaved();
    }

    public int checkDice(){
        correctDice = 0;
        for (Die die : diceRolls) {
            if (die.outcome.equals(throwString)) {
                correctDice++;
            }
        }
        return correctDice;
    }

    public int checkSavedDice(){
        correctDice = 0;
        for (Die die : savedDice) {
            if (die.outcome.equals(throwString)) {
                correctDice++;
            }
        }
        return correctDice;
    }

    public void completeGame(){
        int slokken = checkSavedDice() * throwNumber + vorigeSlokken;
        Builders.sendEmbed(channel,
                "De speler na jou neemt: " + slokken + " slokken",
                "En is daarna aan de beurt", "Gebruik het commando gooi om nog eens te spelen",
                null,
                false,
                false,
                false);
        DertigUtil.removeGame(channel);
    }

    public String printEndGame() {
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

    public void setThrowString(int previousThrowNumber) {
        throwNumber = previousThrowNumber;
        switch (throwNumber) {
            case 1 -> throwString = "1️⃣";
            case 2 -> throwString = "2️⃣";
            case 3 -> throwString = "3️⃣";
            case 4 -> throwString = "4️⃣";
            case 5 -> throwString = "5️⃣";
            case 6 -> throwString = "6️⃣";
        }
    }
}
