package Dertigen;

import java.util.concurrent.ThreadLocalRandom;

public class Die {

    //required values
    public int x;
    public int y;
    public String outcome;
    public boolean saved;

    //possible dice values
    public static String box = "🔳";
    public static String table = "⬛";
    String[] diceOutcomes = {"1️⃣", "2️⃣", "3️⃣", "4️⃣", "5️⃣", "6️⃣"};

    public Die(int x, int y, String outcome){
        this.x = x;
        this.y = y;
        this.outcome = outcome;
    }
}
