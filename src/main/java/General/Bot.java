package General;

import General.Listener.CommandListener;
import General.Listener.ReactionListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class Bot {

    public static JDA jda;

    public static void main(String[] args) throws Exception {
        String token = null;
        try {
            File tokenFile = Paths.get("token.txt").toFile();
            if (!tokenFile.exists()) {
                System.out.println("[ERROR] Could not find token.txt file");
                System.out.print("Please paste in your bot token: ");
                Scanner s = new Scanner(System.in);
                token = s.nextLine();
                System.out.println();
                System.out.println("[INFO] Creating token.txt - please wait");
                if (!tokenFile.createNewFile()) {
                    System.out.println(
                            "[ERROR] Could not create token.txt - please create this file and paste in your token"
                                    + ".");
                    s.close();
                    return;
                }
                Files.write(tokenFile.toPath(), token.getBytes());
                s.close();
            }
            token = new String(Files.readAllBytes(tokenFile.toPath()));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (token == null) return;

        jda = JDABuilder.createDefault(token).build();
        jda.getPresence().setStatus(OnlineStatus.IDLE);
        jda.getPresence().setActivity(Activity.of(Activity.ActivityType.DEFAULT, "Wie dit leest trekt paip"));
        jda.addEventListener(new ReactionListener(), new CommandListener());

    }

}
