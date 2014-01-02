package valtech.command;

import org.joda.time.DateTime;
import org.testng.Assert;
import org.testng.annotations.Test;
import valtech.store.Message;
import valtech.store.MessageStore;
import valtech.store.User;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: peter
 * Date: 02/01/14
 * Time: 15:25
 * To change this template use File | Settings | File Templates.
 */
public class CommandParserTest {

    private static final String VALID_POST_WITH_WHITESPACE = "Alice -> A Valid Message from Alice ";
    private static final String VALID_POST_WITHOUT_WHITESPACE = "Bob->A Valid Message from Bob";
    private static final String VALID_FOLLOWS_WITH_WHITESPACE = "Alice follows Bob";

    @Test
    public void testPost() throws Exception {
        Map<String, User> users = new HashMap<String, User>();
        MessageStore store = new MessageStore();
        CommandParser.handleCommand(users, store, VALID_POST_WITH_WHITESPACE);
        Assert.assertTrue(users.containsKey("Alice"), "User Alice has not been added to the users map");
        Assert.assertTrue(store.getMessages().size() == 1);
        Assert.assertTrue(((Message) store.getMessages().get(0)).getMessage().equals("A Valid Message from Alice"));
        Assert.assertTrue(((Message) store.getMessages().get(0)).getUser().getId().equals("Alice"));
        CommandParser.handleCommand(users, store, VALID_POST_WITHOUT_WHITESPACE);
        Assert.assertTrue(users.containsKey("Bob"), "User Bob has not been added to the users map");
        Assert.assertTrue(store.getMessages().size() == 2);
        Assert.assertTrue(((Message) store.getMessages().get(0)).getMessage().equals("A Valid Message from Bob"));
        Assert.assertTrue(((Message) store.getMessages().get(0)).getUser().getId().equals("Bob"));
    }

    @Test
    public void testFollows() throws Exception {
        Map<String, User> users = new HashMap<String, User>();
        MessageStore store = new MessageStore();
        CommandParser.handleCommand(users, store, VALID_POST_WITH_WHITESPACE);
        Assert.assertTrue(users.containsKey("Alice"), "User Alice has not been added to the users map");
        CommandParser.handleCommand(users, store, VALID_POST_WITHOUT_WHITESPACE);
        Assert.assertTrue(users.containsKey("Bob"), "User Bob has not been added to the users map");
        CommandParser.handleCommand(users, store, VALID_FOLLOWS_WITH_WHITESPACE);
        Boolean followed = CommandParser.posterIsFollowed("Alice", users, "Bob");
        Assert.assertTrue(followed, "Bob has not been added to Alice's follows set.");
    }


    @Test
    public void testWall() throws Exception {
        Map<String, User> users = new HashMap<String, User>();
        MessageStore store = new MessageStore();
        CommandParser.handleCommand(users, store, VALID_POST_WITH_WHITESPACE);
        Assert.assertTrue(users.containsKey("Alice"), "User Alice has not been added to the users map");
        CommandParser.handleCommand(users, store, VALID_POST_WITHOUT_WHITESPACE);
        Assert.assertTrue(users.containsKey("Bob"), "User Bob has not been added to the users map");
        CommandParser.handleCommand(users, store, VALID_FOLLOWS_WITH_WHITESPACE);
        Assert.assertEquals(CommandParser.getUserMessages(users, store, "Alice", true).size(), 2, "There should be 2 messages in the list as both messages should appear");
        Assert.assertEquals(CommandParser.getUserMessages(users, store, "Bob", true).size(), 1, "There should be 1 message in the list as only Bobs messages should appear");
    }

    @Test
    public void testRead() throws Exception {
        Map<String, User> users = new HashMap<String, User>();
        MessageStore store = new MessageStore();
        CommandParser.handleCommand(users, store, VALID_POST_WITH_WHITESPACE);
        Assert.assertTrue(users.containsKey("Alice"), "User Alice has not been added to the users map");
        CommandParser.handleCommand(users, store, VALID_POST_WITHOUT_WHITESPACE);
        Assert.assertTrue(users.containsKey("Bob"), "User Bob has not been added to the users map");
        CommandParser.handleCommand(users, store, VALID_FOLLOWS_WITH_WHITESPACE);
        Assert.assertEquals(CommandParser.getUserMessages(users, store, "Alice", false).size(), 1, "There should be 2 messages in the list as both messages should appear");
        Assert.assertEquals(CommandParser.getUserMessages(users, store, "Bob", false).size(), 1, "There should be 1 message in the list as only Bobs messages should appear");
    }

    @Test
    public void testFormatInterval() throws Exception {
        Assert.assertTrue(CommandParser.formatInterval(1L, CommandParser.UNIT_SECOND).equals(" ( 1 second ago )"));
        Assert.assertTrue(CommandParser.formatInterval(1L, CommandParser.UNIT_MINUTE).equals(" ( 1 minute ago )"));
        Assert.assertTrue(CommandParser.formatInterval(1L, CommandParser.UNIT_HOUR).equals(" ( 1 hour ago )"));
        Assert.assertTrue(CommandParser.formatInterval(5L, CommandParser.UNIT_SECOND).equals(" ( 5 seconds ago )"));
        Assert.assertTrue(CommandParser.formatInterval(10L, CommandParser.UNIT_MINUTE).equals(" ( 10 minutes ago )"));
        Assert.assertTrue(CommandParser.formatInterval(3L, CommandParser.UNIT_HOUR).equals(" ( 3 hours ago )"));
        Assert.assertTrue(CommandParser.formatInterval(200L, CommandParser.UNIT_HOUR).equals(" ( 200 hours ago )"));
    }

    @Test
    public void testFormatTimeInterval() throws Exception {
        Assert.assertTrue(CommandParser.formatTime(DateTime.now().minusSeconds(30)).endsWith(" seconds ago )"));
        Assert.assertTrue(CommandParser.formatTime(DateTime.now().minusMinutes(7)).equals(" ( 7 minutes ago )"));
        Assert.assertTrue(CommandParser.formatTime(DateTime.now().minusHours(23)).equals(" ( 23 hours ago )"));
        Assert.assertTrue(CommandParser.formatTime(DateTime.now().minusHours(1)).equals(" ( 1 hour ago )"));
        Assert.assertTrue(CommandParser.formatTime(DateTime.now().minusHours(30)).equals(" ( 30 hours ago )"));
    }
}
