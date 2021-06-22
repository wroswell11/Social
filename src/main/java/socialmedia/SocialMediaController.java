package socialmedia;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

@Controller
public class SocialMediaController {
    @Autowired
    SocialMediaService service;
    boolean isHome = false;

    public SocialMediaController() {
        service = new SocialMediaService();
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    @ResponseBody
    public String login() {
        return "<h1>SocialMedia</h1>" +
                "<h3>Login:</h3>" +
                "<form action=\"profile\" method=\"get\">" +
                "<label for=\"uName\">Username: <p></label>" +
                "<input type=\"text\" id=\"uName\" name=\"uName\">" +
                "<input type=\"submit\" value=\"Submit\">" +
                "</form>";
    }

    @RequestMapping(value = "/profile", method = RequestMethod.GET)
    @ResponseBody
    public String findProfile(@RequestParam("uName") String userName) {
        isHome = false;
        if (service.userInDataMap(userName)) {
            service.setCurrentUser(userName);
            return retrieveProfile(userName);
        }
        return error();
    }

    @RequestMapping(value = "/home", method = RequestMethod.GET)
    @ResponseBody
    public String goHome() {
        isHome = true;
        return sendHome();
    }

    @RequestMapping(value = "/error", method = RequestMethod.GET)
    @ResponseBody
    public String error() { return "Error! 404 Error, Will Robinson!"; }

    @RequestMapping(value = "/publishToTimeline", method = RequestMethod.GET)
    @ResponseBody
    public String publishToTimeline(@RequestParam("item") String item) {
        User currentUser = service.getCurrentUser();
        currentUser.timeline.put(LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss")), item);
        if (!isHome) {
            return findProfile(currentUser.name);
        } else {
            return goHome();
        }
    }

    public String retrieveProfile(String userName) {
        User profile = service.getUser(userName);
        String returnText = assembleProfileHeader(profile);
        returnText += assembleFollowingList(profile);
        returnText += assembleTimeline(profile);
        return returnText;
    }

    String assembleProfileHeader(User profile) {
        User currentUser = service.getCurrentUser();
        String returnText = "<h2>" + profile.name + "</h2>";
        returnText += "<form action=\"profile\" method=\"get\">" +
                "<label for=\"uName\">Search: </label>" +
                "<input type=\"text\" id=\"uName\" name=\"uName\">" +
                "<input type=\"submit\" value=\"Submit\">" +
                "</form>";
        returnText += "<a href=\"http://localhost:8080/home?uName=" + currentUser.name + "\">Home</a>";

        if(!profile.name.equals(currentUser.name)) {
            returnText += " | <a href=\"http://localhost:8080/profile?uName=" + currentUser.name + "\">Profile</a>";
        }

        return returnText;
    }

    public String assembleFollowingList(User profile) {
        String returnText = "<br><h3>Following: </h3><ul>";
        for (String item : profile.getFollowing()) {
            returnText += "<li><a href=\"http://localhost:8080/profile?uName=" + item + "\">" + item + "</a></li>";
        }
        returnText += "</ul>";

        return returnText;
    }

    public String assembleTimeline(User profile) {
        String returnText = "<br><h3>Timeline: </h3>";

        if (profile.name.equals(service.getCurrentUser().name)) {
            returnText += "<form action=\"publishToTimeline\" method=\"get\">" +
                    "<label for=\"item\">Publish: </label>" +
                    "<input type=\"text\" id=\"item\" name=\"item\">" +
                    "<input type=\"submit\" value=\"Submit\">" +
                    "</form>";
        }

        Map<String, String> sortedMap = new TreeMap<>(Collections.reverseOrder());
        sortedMap.putAll(profile.timeline);

        for (Map.Entry<String, String> entry : sortedMap.entrySet()) {
            returnText += entry.getValue() + " (" + service.getTimestamp(entry.getKey()) + ")<br>";
        }

        return returnText;
    }

    public String sendHome() {
        User profile = service.getCurrentUser();
        String returnText = assembleHomeHeader(profile);
        returnText += assembleAggregateTimeline();
        return returnText;
    }

    String assembleHomeHeader(User profile) {
        String returnText = "<h2>" + profile.name + "</h2>";
        returnText += "<form action=\"profile\" method=\"get\">" +
                "<label for=\"uName\">Search: </label>" +
                "<input type=\"text\" id=\"uName\" name=\"uName\">" +
                "<input type=\"submit\" value=\"Submit\">" +
                "</form>";
        returnText += "<a href=\"http://localhost:8080/profile?uName=" + service.getCurrentUser().name + "\">Profile</a>";

        return returnText;
    }

    String assembleAggregateTimeline() {
        String returnText = "<br><h3>Timeline: </h3>";

        returnText += "<form action=\"publishToTimeline\" method=\"get\">" +
                "<label for=\"item\">Publish: </label>" +
                "<input type=\"text\" id=\"item\" name=\"item\">" +
                "<input type=\"submit\" value=\"Submit\">" +
                "</form>";

        Map<String, String> sortedMap = new TreeMap<>(Collections.reverseOrder());
        sortedMap.putAll(service.getAggregateTimelineMap());

        for (Map.Entry<String, String> entry : sortedMap.entrySet()) {
            returnText += entry.getValue() + " (" + service.getTimestamp(entry.getKey()) + ")<br>";
        }

        return returnText;
    }
}
