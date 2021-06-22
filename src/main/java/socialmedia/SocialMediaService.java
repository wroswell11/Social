package socialmedia;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.time.temporal.ChronoUnit.*;

@Service
public class SocialMediaService {
    private Map<String, User> dataMap;
    private User currentUser;

    public SocialMediaService() {
        dataMap = new HashMap<>();
        loadPreliminaryData();
    }

    public Map<String, User> getDataMap() { return dataMap; }
    public User getUser(String userId) { return dataMap.get(userId); }
    public User getCurrentUser() { return currentUser; }
    public void setCurrentUser(String userId) {
        if (currentUser == null)
            currentUser = getUser(userId);
    }

    void loadPreliminaryData() {
        try {
            Gson gson = new Gson();
            String jsonString = Files.readString(Path.of("src\\main\\resources\\data.json"));
            Type type = new TypeToken<HashMap<String, User>>(){}.getType();
            dataMap = gson.fromJson(jsonString, type);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    boolean userInDataMap(String userId) {
        return dataMap.containsKey(userId);
    }

    String getTimestamp(String dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss");
        LocalDateTime dt = LocalDateTime.parse(dateTime, formatter);
        LocalDateTime dtNow = LocalDateTime.now();

        long diff = YEARS.between(dt, dtNow);
        if (diff == 0) {
            diff = MONTHS.between(dt, dtNow);
            if (diff == 0) {
                diff = WEEKS.between(dt, dtNow);
                if (diff == 0) {
                    diff = DAYS.between(dt, dtNow);
                    if (diff == 0) {
                        diff = HOURS.between(dt, dtNow);
                        if (diff == 0) {
                            diff = MINUTES.between(dt, dtNow);
                            if (diff == 0) {
                                diff = SECONDS.between(dt, dtNow);
                                return diff + " second(s) ago";
                            } else {
                                return diff + " minute(s) ago";
                            }
                        } else {
                            return diff + " hour(s) ago";
                        }
                    } else {
                        return diff + " day(s) ago";
                    }
                } else {
                    return diff + " week(s) ago";
                }
            } else {
                return diff + " month(s) ago";
            }
        }

        return diff + " year(s) ago";
    }

    Map<String, String> getAggregateTimelineMap() {
        List<String> listOfFollowers = currentUser.following;
        Map<String, String> aggregateMap = new HashMap<>();

        aggregateMap.putAll(setTempMapForLoading(currentUser.name, currentUser.timeline));

        for(String follower : listOfFollowers) {
            aggregateMap.putAll(setTempMapForLoading(follower, dataMap.get(follower).timeline));
        }

        return aggregateMap;
    }

    Map<String, String> setTempMapForLoading(String userName, Map<String, String> timelineMap) {
        Map<String, String> tempMap = new HashMap<>();
        tempMap.putAll(timelineMap);
        tempMap.replaceAll((k,v)->v = userName + " - " + tempMap.get(k));

        return tempMap;
    }
}
