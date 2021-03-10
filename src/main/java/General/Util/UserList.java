package General.Util;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserList {
    private static final HashMap<MessageChannel, List<User>> userList = new HashMap<>();

    public static void createUserList(MessageChannel messageChannel) {
        List<User> users = new ArrayList<>();
        userList.put(messageChannel, users);
    }

    public static void addUser(MessageChannel messageChannel, User user) {userList.get(messageChannel).add(user);}

    public static User getNextUser(MessageChannel messageChannel, User user) {
        List<User> users = userList.get(messageChannel);
        int currentID = users.indexOf(user);
        if((users.size() - 1) != currentID){
            return users.get(currentID + 1);
        }else{
            return users.get(0);
        }
    }

    public static User getStartUser(MessageChannel messageChannel) {return userList.get(messageChannel).get(0);}

    public static void removeUser(MessageChannel messageChannel, User user) {userList.get(messageChannel).remove(user);}

    public static boolean contains(MessageChannel channel, User user){return userList.get(channel).contains(user);}

    public static boolean hasUserList(MessageChannel messageChannel) {return userList.containsKey(messageChannel);}

    public static List<User> getUserList(MessageChannel messageChannel) {return userList.get(messageChannel);}

    public static void removeUserList(MessageChannel messageChannel) {
        userList.remove(messageChannel);
    }
}
