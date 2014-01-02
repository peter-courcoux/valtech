package valtech;

import valtech.command.CommandParser;
import valtech.store.MessageStore;
import valtech.store.User;

import java.io.Console;
import java.util.HashMap;
import java.util.Map;

/**
 * Valtech 'twitter' app.
 *
 *
 */
public class App 
{
    private MessageStore store = new MessageStore();

    public static void main( String[] args )
    {
        System.out.println( "Valtech" );
        Console c = System.console();
        if (c == null) {
            System.err.println("System Console not available, exiting...");
            // exit with error
            System.exit(1);
        }

        MessageStore store = new MessageStore();
        Map<String, User> users = new HashMap<String, User>();

        while(true){
            // loop forever
            System.out.println("Enter command ('enter' for usage details): ");
            String command = c.readLine();
            CommandParser.handleCommand(users, store, command);
        }
    }
}
