package General.DieGames;

public class Die {
    //required values
    public int x;
    public int y;
    public String outcome;
    public boolean saved;

    //possible dice values
    public static String box = "🔳";
    public static String table = "⬛";

    public Die(int x, int y, String outcome){
        this.x = x;
        this.y = y;
        this.outcome = outcome;
    }
}
