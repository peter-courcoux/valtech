package valtech.command;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import valtech.store.Message;
import valtech.store.MessageStore;
import valtech.store.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Class to parse and handle commands submitted to the application
 * <p/>
 * Created with IntelliJ IDEA.
 * User: peter
 * Date: 02/01/14
 * Time: 10:44
 * To change this template use File | Settings | File Templates.
 */
public class CommandParser {

    // Command constants
    private static final String POST_COMMAND = "->";
    private static final String WALL_COMMAND = " wall";
    private static final String FOLLOWS_COMMAND = " follows";
    private static final String QUIT_COMMAND = "quit";
    // Interval constants
    static final String UNIT_SECOND = "second";
    static final String UNIT_MINUTE = "minute";
    static final String UNIT_HOUR = "hour";
    private static final Long ONE_MINUTE = (60 * 1000l);
    private static final Long ONE_HOUR = (ONE_MINUTE * 60);
    private static final Long SECONDS_FACTOR = 1000l;
    private static final Long MINUTES_FACTOR = SECONDS_FACTOR * 60;
    private static final Long HOURS_FACTOR = MINUTES_FACTOR * 60;


    /**
     * <p>Single Entry Point for handling submitted commands.
     * </p>
     *
     * @param users map of users keyed on userid
     * @param store message store
     * @param line  command input
     */
    public static void handleCommand(Map<String, User> users, MessageStore store, String line) {
        if (line.contains(QUIT_COMMAND)) {
            // exit no error
            System.exit(0);
        } else if (line.contains(POST_COMMAND)) {
            handlePost(users, store, line);
        } else if (line.contains(WALL_COMMAND)) {
            handleWall(users, store, line);
        } else if (line.contains(FOLLOWS_COMMAND)) {
            handleFollows(users, line);
        } else {
            handleRead(users, store, line);
        }
    }

    /**
     * TODO add help for commands here
     */
    private static void displayHelp() {
        System.out.println("Commands:-");
        System.out.println("to exit the system: 'quit'");
        System.out.println("to post a message: <userid> -> <message>");
        System.out.println("to follow a user: <your userid> follows <other userid>");
        System.out.println("to read a users messages: <userId>");
        System.out.println("to see a wall of messages: <userId> wall");
    }

    /**
     * <p>Method called if no other command is found. If the command is null or empty help is displayed, if
     * the command consists of a user id found in the users map then a read is performed, otherwise and error
     * is notified.</p>
     *
     * @param users map of users keyed on userid
     * @param store message store
     * @param line  command input
     */
    private static void handleRead(Map<String, User> users, MessageStore store, String line) {
        if (StringUtils.isEmpty(line)) {
            System.out.println("No command entered.");
            displayHelp();
            return;
        }
        String userId = StringUtils.strip(line);
        if (!users.containsKey(userId)) {
            System.out.println("Unknown user id entered. Please try again.");
            return;
        }
        for (Message message : getUserMessages(users, store, userId, false)) {
                System.out.println(message.getMessage() + formatTime(message.getTimestamp()));
        }
    }

    /**
     * returns a list of valid messages for the user, including messages from followed users if includeFollowed set to true.
     * @param users map of users keyed on userid
     * @param store message store
     * @param userId user id of user for which messages are required
     * @param includeFollowed true if messages should include those posted by followed users
     * @return list of valid messages
     */
    static List<Message> getUserMessages(Map<String, User> users, MessageStore store, String userId, boolean includeFollowed){
        List<Message> messages = new ArrayList<Message>();
        for (Message message : store.getMessages()) {
            if (message.getUser().getId().equals(userId)) {
                messages.add(message);
            }
            if (includeFollowed){
                if(posterIsFollowed(userId, users, message.getUser().getId())){
                    messages.add(message);
                }
            }
        }
        return messages;
    }

    /**
     * <p>returns true if the targetUserId identifies a user which is followed.</p>
     *
     * @param userId id of user to check the list of users who they are following
     * @param users map of users keyed on userid
     * @param targetUserId potentially followed user
     * @return true if followed otherwise false.
     */
    static boolean posterIsFollowed(String userId, Map<String, User> users, String targetUserId) {
        boolean followed = false;
        User user = users.get(userId);
        for (String followedId : user.getFollows()) {
            if (followedId.equals(targetUserId)) {
                followed = true;
            }
        }
        return followed;
    }

    /**
     * <p>Formats the elapsed time from the message timestamp to the time of display.</p>
     *
     * @param timestamp message timestamp to calculate elapsed time
     * @return elapsed time suitably formatted
     */
    static String formatTime(DateTime timestamp) {
        String formattedInterval;
        long elapsed = new DateTime().getMillis() - timestamp.getMillis();
        Long interval;
        if (elapsed < ONE_MINUTE) {
            interval = (elapsed / SECONDS_FACTOR);
            formattedInterval = formatInterval(interval, UNIT_SECOND);
        } else if (elapsed < ONE_HOUR) {
            interval = (elapsed / MINUTES_FACTOR);
            formattedInterval = formatInterval(interval, UNIT_MINUTE);
        } else {
            interval = (elapsed / HOURS_FACTOR);
            formattedInterval = formatInterval(interval, UNIT_HOUR);
        }

        return formattedInterval;
    }

    /**
     * <p>Formats the units of time accounting for single units and units of second, minute and hour</p>
     *
     * @param count number of units
     * @param unit type of unit
     * @return suitably formatted string
     */
    static String formatInterval(Long count, String unit) {
        StringBuilder formattedInterval = new StringBuilder();
        formattedInterval.append(" ( ");
        formattedInterval.append(count.toString());
        formattedInterval.append(" ");
        formattedInterval.append(unit);
        if (count > 1) {
            formattedInterval.append("s");
        }
        formattedInterval.append(" ago )");
        return formattedInterval.toString();
    }

    /**
     * <p>handle the follows command</p>
     *
     * @param users map of users keyed on userid
     * @param line  command input
     */
    private static void handleFollows(Map<String, User> users, String line) {
        String userId = StringUtils.strip(line.substring(0, line.indexOf(FOLLOWS_COMMAND)));
        String targetUser = StringUtils.strip(line.substring(line.indexOf(FOLLOWS_COMMAND) + FOLLOWS_COMMAND.length()));
        if (!(StringUtils.isNotEmpty(userId) &&
                StringUtils.isNotEmpty(targetUser) &&
                users.containsKey(userId) &&
                users.containsKey(targetUser))) {
            System.out.println("Unable to find user(s) in user list.");
            return;
        }
        users.get(userId).getFollows().add(targetUser);
    }

    /**
     * <p>handles the wall command.</p>
     *
     * @param users map of users keyed on userid
     * @param store message store
     * @param line  command input
     */
    private static void handleWall(Map<String, User> users, MessageStore store, String line) {
        String userId = StringUtils.strip(line.substring(0, line.indexOf(WALL_COMMAND)));
        for (Message message : getUserMessages(users, store, userId, true)) {
            System.out.println(message.getUser().getId() + " - " + message.getMessage() + formatTime(message.getTimestamp()));
        }
    }

    /**
     * <p>handles the post (->) command.</p>
     *
     * @param users map of users keyed on userid
     * @param store message store
     * @param line  command input
     */
    private static void handlePost(Map<String, User> users, MessageStore store, String line) {
        String userId = StringUtils.strip(line.substring(0, line.indexOf(POST_COMMAND)));
        String message = StringUtils.strip(line.substring(line.indexOf(POST_COMMAND) + POST_COMMAND.length()));
        // we only want to handle this if we have both a userid and a message
        if (StringUtils.isNotEmpty(userId) && StringUtils.isNotEmpty(message)) {
            User user;
            if (users.containsKey(userId)) {
                user = users.get(userId);
            } else {
                user = new User();
                user.setId(userId);
                users.put(userId, user);
            }
            Message m = new Message();
            m.setUser(user);
            m.setMessage(message);
            m.setTimestamp(new DateTime());
            store.getMessages().add(0, m);
        }
    }

}
