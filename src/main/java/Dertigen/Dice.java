package Dertigen;

import java.awt.*;
import java.util.LinkedHashSet;
import java.util.concurrent.ThreadLocalRandom;

public class Dice {

    Point[] coordinatesArray;
    String[] diceOutcomes = {"1️⃣", "2️⃣", "3️⃣", "4️⃣", "5️⃣", "6️⃣"};
    //String[] diceOutcomes = {"6️⃣", "6️⃣", "6️⃣", "6️⃣", "6️⃣", "5️⃣"};
    //String[] diceOutcomes = {"1️⃣", "1️⃣", "1️⃣", "1️⃣", "2️⃣", "2️⃣"};
    public Die[] dice;

    public Die[] roll(int diceAmount, int width, int height){
        LinkedHashSet<Point> coordinates = new LinkedHashSet<>();
        dice = new Die[diceAmount];
        coordinatesArray = new Point[diceAmount];
        while(coordinates.size() < diceAmount){
            Point nextPoint = new Point(ThreadLocalRandom.current().nextInt(6, width), ThreadLocalRandom.current().nextInt(0, height));
            coordinates.add(nextPoint);
        }
        coordinatesArray = coordinates.toArray(coordinatesArray);
        for(int i = 0; i < dice.length; i++){
            dice[i] = new Die(coordinatesArray[i].x, coordinatesArray[i].y, diceOutcomes[ThreadLocalRandom.current().nextInt(0, diceOutcomes.length)]);
        }
        return dice;
    }

    public Die[] initSaved(){
        dice = new Die[6];
        int index = 0;
        for(int i = 1; i <= 3; i++) {
            for (int j = 1; j <= 2; j++) {
                dice[index] = new Die(j, i, Die.box);
                index++;
            }
        }
        return dice;
    }
}
