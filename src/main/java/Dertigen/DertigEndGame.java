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
    String gameTitle = "Dertigen";

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
    boolean gameDone = false;
    boolean roundDone = false;

    //variables for the current used message
    public long endGameMessageID;

    public void startEndGame(DertigGame dertigGame) {
        //initiate all values using the game this came from
        game = dertigGame;
        width = game.width;
        height = game.height;
        channel = game.channel;

        savedDice = dice.initSaved();
        diceLeft = 6;
        diceRolls = dice.roll(diceLeft, width, height);
        Builders.sendEmbed(channel,
                gameTitle,
                printEndGame(),
                "Je moet " + throwString + "en gooien. Klik op \"✅\" om door te gaan",
                continueButton,
                false,
                true,
                false);
        endGameStarted = true;
    }

    public void updateEndGame() {
        if (roundDone) {
            savedDice = dice.initSaved();
            diceLeft = 6;
            roundDone = false;
        }
        checkWin();
        saveDice(throwString);
        diceRolls = dice.roll(diceLeft, width, height);
        channel.retrieveMessageById(endGameMessageID).queue(msg -> Builders.updateGameEmbed(
                msg,
                gameTitle,
                printEndGame(),
                "Je moet" + throwString + "en gooien. Klik op \"✅\" om door te gaan",
                continueButton));
    }

    public void saveDice(String number) {
        for (int i = 0; i < diceRolls.length; i++) {
            if (diceRolls[i].outcome.equals(number)) {
                for (int j = 0; j < savedDice.length; j++) {
                    if (savedDice[j].outcome.equals(Die.box)) {
                        savedDice[j].outcome = diceRolls[i].outcome;
                        diceLeft--;
                        break;
                    }
                }
                diceRolls[i].outcome = Die.table;
            }
        }
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

    public void checkWin() {
        int correctDice = 0;
        int spaceLeft = 0;
        for (Die die : diceRolls) {
            if (die.outcome.equals(throwString)) {
                correctDice++;
            }
        }
        for (Die die : savedDice) {
            if (die.outcome.equals(Die.box)) {
                spaceLeft++;
            }
        }

        int slokken = throwNumber * (6 - spaceLeft) + vorigeSlokken;

        //check if there are no correct dicethrows
        if (correctDice == 0 && spaceLeft != 0) {
            gameDone = true;
        } else if (correctDice != 0) {
            //check if there are exactly as many dicethrows as there are places left to sve dice
            if (correctDice == spaceLeft && throwNumber != 1) {
                vorigeSlokken = slokken;
                throwNumber--;
                setThrowString(throwNumber);
                roundDone = true;
            }
        }
        if (gameDone) {
            Builders.sendEmbed(channel,
                    "De speler na jou neemt: " + Integer.toString(slokken) + " slokken",
                    "En is daarna aan de beurt", "Gebruik het commando gooi om nog eens te spelen",
                    null,
                    false,
                    false,
                    false);
            DertigUtil.removeGame(channel);
        }
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
