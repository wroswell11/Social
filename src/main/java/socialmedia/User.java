package socialmedia;

import java.util.List;
import java.util.Map;

public class User {
    public String name;
    public List<String> following;
    public Map<String, String> timeline;

    public List<String> getFollowing() { return following; }
}
