package General.Listeners;

import Dertigen.DertigGame;
import General.Util.GameList;
import General.Bot;
import General.Util.Builders;
import General.Util.UserList;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static java.lang.Integer.parseInt;

public class CommandListener extends ListenerAdapter {

    //command lists
    String[] info = {"info", "zuipbot", "zuipen", "jo", "hoi", "hallo"};
    List<String> infoList = Arrays.asList(info);
    String[] stop = {"stop", "quit"};
    List<String> stopList = Arrays.asList(stop);
    String[] roll = {"roll", "gooi", "30en", "speel", "doodziekebak"};
    List<String> rollList = Arrays.asList(roll);
    String[] cheers = {"🍺", "🍾", "🍷", "🍸", "🍹", "🥂", "🥃", "🥤", "cheers", "proost"};
    List<String> cheersList = Arrays.asList(cheers);
    String[] feedback = {"feedback", "tip", "verbeterpunt", "fix"};
    List<String> feedbackList = Arrays.asList(feedback);
    String[] help = {"help", "commands", "commandos"};
    List<String> helpList = Arrays.asList(help);
    String[] rules = {"regels", "rules", "uitleg"};
    List<String> rulesList = Arrays.asList(rules);

    String[] prefixes = {"!", "-", "~", "/"};

    String[] stopEmotes = {"🛑", "✖"};
    String[] beerEmote = {"🍻"};

    //feedback variables
    File feedbackUserIDFile = Paths.get("FeedbackUser.txt").toFile();
    Long feedbackUserID;

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {

        //check if there is a feedback user
        try {
            if (!feedbackUserIDFile.exists()) {
                System.out.println("there is no user to send feedback to");
            } else {
                feedbackUserID = Long.parseLong(new String(Files.readAllBytes(feedbackUserIDFile.toPath())));
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        TextChannel channel = event.getChannel();

        Bot.jda.getPresence().setStatus(OnlineStatus.ONLINE);
        String[] args = event.getMessage().getContentRaw().split(" ");
        for (String prefix : prefixes) {

            //check if the message starts with one of the correct prefixes
            if (args[0].startsWith(prefix)) {

                //remove the prefix
                args[0] = args[0].substring(1);

                //set the idle timer
                TimerTask task = new TimerTask() {
                    public void run() {
                        Bot.jda.getPresence().setStatus(OnlineStatus.IDLE);
                    }
                };

                Timer timer = new Timer();

                //wait 2 minutes before going idle again
                long delay = 120000L;
                timer.schedule(task, delay);

                //commands
                if (infoList.contains(args[0].toLowerCase())) {
                    Builders.sendTasteMessage(event.getChannel(),
                            "Welkom bij Adman!",
                            "Adman is een discord bot gemaakt voor drankspellen. " +
                                    "Momenteel is dertigen het enige spel dat gespeeld kan worden, maar in de " +
                                    "toekomst komen er meer spellen aan! Omdat deze bot nog onder ontwikkeling is, " +
                                    "Kunnen we jullie feedback goed gebruiken! Als er iets is dat aan de bot verbeterd " +
                                    "kan worden, of iets dat ontbreekt, laat het dan even weten met -feedback gevolgd door je bericht." +
                                    "Bedankt voor het gebruik van Adtman, en vooral: veel plezier! 🍻",
                            "Gebruik om een ronde dertigen te beginnen het commando -gooi. of reageer met \"🍻\" op dit bericht.",
                            beerEmote);
                }else if (rollList.contains(args[0].toLowerCase())) {
                    if (GameList.hasGame(channel)) {
                        Builders.sendTempError(channel,
                                "- Er is al een ronde bezig in dit textkanaal, ga naar een ander tekstkanaal of stuur \"-stop\".\n",
                                6);
                    } else {
                        if(!UserList.hasUserList(channel)) {
                            DertigGame game = new DertigGame(channel, "dertigen");
                            GameList.setGame(channel, game);
                            GameList.getGame(channel).setPlayerList(event.getAuthor());
                        }
                    }
                }

                if (stopList.contains(args[0].toLowerCase())) {
                    if(GameList.hasGame(channel)) {
                        Builders.sendEmbed(event.getChannel(),
                                "Stoppen",
                                "Weet je zeker dat je de ronde wil stoppen?",
                                "Reageer met \"🛑\" om het spel te stoppen, druk op \"✖\" om het stoppen te annuleren.",
                                stopEmotes,
                                false,
                                false,
                                true);
                    }
                }else if (feedbackList.contains(args[0].toLowerCase())) {
                        Builders.sendEmbed(event.getChannel(),
                                "Bedankt voor de feedback👍",
                                "Bedankt voor je bericht, we gaan kijken wat we ermee kunnen!",
                                "",
                                null,
                                false,
                                false,
                                false);
                        StringBuilder feedback = new StringBuilder();
                        for(int i = 1; i < args.length; i++){
                            feedback.append(args[i]).append(" ");
                        }
                        if(feedbackUserID != null){
                            Bot.jda.retrieveUserById(feedbackUserID).queue(user ->
                                    user.openPrivateChannel().queue(feedbackChannel ->
                                                Builders.sendPrivateEmbed(
                                                        feedbackChannel,
                                                        "Je hebt nieuwe feedback ontvangen!",
                                                        "\" " + feedback.toString() + " \" Deze feedback komt van: " + event.getAuthor().getAsMention(),
                                                        ""
                                                )
                                            )
                            );
                        }
                }else if (helpList.contains(args[0].toLowerCase())) {
                        Builders.sendTasteMessage(event.getChannel(),
                                "Help",
                                "Bij deze een lijst van alle commandos en wat ze doen:\n" +
                                        "-info ➡ Dit commando geeft wat meer info over deze bot.\n" +
                                        "-gooi ➡ Dit commando start in het huidige kanaal een nieuwe ronde dertigen.\n" +
                                        "-help ➡ Dit commando laat deze lijst met commandos zien\n" +
                                        "-feedback ➡ Als er iets is dat niet klopt/ontbreekt aan de bot kan je dat doorgeven door -feedback gevolgd door je bericht te sturen.\n" +
                                        "-stop ➡ Als er een spel bezig is in het textkanaal waarin -stop gestuurd wordt beëindigt dat het spel.\n" +
                                        "-regels ➡ Dit commando heeft de uitleg van hoe dertigen werkt.\n" +
                                        "Veel plezier met AdtMan!",
                                "",
                                null);
                }else if (rulesList.contains(args[0].toLowerCase())) {
                    Builders.sendTasteMessage(event.getChannel(),
                            "Regels",
                            "Over het algemeen vertelt de bot je wat je moet doen en hoeveel je moet drinken, " +
                                    "maar het is toch wel handig om de regels te kennen, dus bij deze de regels van dertigen: \n\n" +
                                    "Dertigen is een spel dat gespeeld wordt met 6 dobbelstenen. In het spel zijn er 2 opties " +
                                    "waar de speler voor kan gaan: \n" +
                                    "1. Zo hoog mogelijk gooien\n" +
                                    "2. Zo laag mogelijk gooien\n\n" +
                                    "Het begin werkt als volgt: de speler gooit alle 6 dobbelstenen, en kiest welke hij wil " +
                                    "bewaren. De speler moet altijd minstens 1 dobbelsteen bewaren en mag daarna de rest opnieuw gooien. " +
                                    "Er zit geen maximum aan het aantal dobbelstenen dat een speler per keer kan bewaren. " +
                                    "Als alle dobbelstenen zijn bewaard komt de puntentelling.\n\n " +
                                    "Bij de puntentelling werkt het als volgt:\n\nAls het totaal aantal ogen van de dobbelstenen lager " +
                                    "is dan 10, moeten alle andere spelers een adtje trekken.\n\n Als het totaal aantal ogen tussen de 10 en 30 " +
                                    "is, moet de speler zelf 30-het aantal ogen drinken (dus als er bijvoorbeeld 27 is gegooid, zijn dat 3 slokken).\n\n" +
                                    "Bij exact 30 gebeurt er niks, en bij hoger dan 30 komt er een volgende fase. \n\nIn deze fase gaat de speler opnieuw gooien, " +
                                    "met als enige doel om het aantal ogen - 30 te gooien (dus als er bijvoorbeeld 32 was gegooid, moet de speler nu tweeën gooien). \n" +
                                    "Dit gaat door totdat het gezochte aantal niet meer gegooid wordt, of totdat alle dobbelstenen de goede waarde hebben. "+
                                    "Als de goede waarde 6x gegooid is wordt deze 1 verlaagd en begint deze fase opnieuw. Anders worden de waardes bij elkaar " +
                                    "opgeteld, en dat is het aantal slokken dat de volgende speler moet nemen.\n\n" +
                                    "Voorbeeld: Een speler komt uit op 32 en gaat dus tweeën gooien. Hij gooit 6 tweeën en gaat dus enen gooien. Dan gooit hij 3 enen en " +
                                    "daarna geen enen meer. De speler na hem moet dan 6 X 2 + 3 X 1 slokken nemen dus 15 slokken.",
                            "Is er iets dat niet werkt of ontbreekt? Dat kan je aangeven met -feedback gevolgd door je bericht.",
                            null);
                }
            }else{
                if(cheersList.contains(args[0].toLowerCase())){
                    Builders.sendTempMessage(channel, "Proost! 🍻", 5);
                }
            }
        }
    }
}
