package valtech.store;

import org.joda.time.DateTime;


/**
 * <p>Represents a message consisting of a user, a posting timestamp and a text message</p>
 *
 * Created with IntelliJ IDEA.
 * User: peter
 * Date: 02/01/14
 * Time: 10:33
 * To change this template use File | Settings | File Templates.
 */
public class Message {

    private User user;

    private String message;

    private DateTime timestamp;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public DateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(DateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String toString(){
        return user.getId() + ":" + timestamp.toString() + ":" + message;
    }
}
