package valtech.store;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Represents an ordered list of messages. Messages are typically added at index 0 so the latest message is first
 * in the list and it is ordered by posting time.</p>
 *
 * Created with IntelliJ IDEA.
 * User: peter
 * Date: 02/01/14
 * Time: 10:39
 * To change this template use File | Settings | File Templates.
 */
public class MessageStore {

    private List<Message> messages = new ArrayList<Message>();

    public List<Message> getMessages() {
        return messages;
    }

    @SuppressWarnings("unused")
    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
}
