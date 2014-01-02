package valtech.store;

import java.util.*;

/**
 * <p>Represents a User in the application/</p>
 *
 * Created with IntelliJ IDEA.
 * User: peter
 * Date: 02/01/14
 * Time: 10:34
 * To change this template use File | Settings | File Templates.
 */
public class User {

    private String id;

    private Set<String> follows = new HashSet<String>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * Set of user ids of followed users.
     * set will not contain duplicates so we don't need to check
     */
    public Set<String> getFollows() {
        return follows;
    }

    public void setFollows(Set<String> follows) {
        this.follows = follows;
    }
}
