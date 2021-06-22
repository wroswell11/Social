package socialmedia;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SocialMediaControllerTest {
    private SocialMediaController spyController;
    private SocialMediaService mockService;
    private User testUser;

    @BeforeEach
    void setUp() {
        mockService = mock(SocialMediaService.class);
        spyController = spy(new SocialMediaController());
        spyController.service = mockService;

        testUser = new User();
        testUser.name = "Test";
        testUser.following = new ArrayList<>();
        testUser.following.add("Test2");
        testUser.following.add("Test3");
        testUser.timeline = new HashMap<>();
        testUser.timeline.put("06-22-2021 12:00:00", "It's a good day to code");
        testUser.timeline.put("06-21-2021 11:00:00", "Testing said code can be rough");
    }

    @Test
    public void login_returnsHTMLWithHeaderAndFormPointingToLoginProcess() {
        assertEquals("<h1>SocialMedia</h1><h3>Login:</h3><form action=\"profile\" method=\"get\"><label for=\"uName\">Username: <p></label><input type=\"text\" id=\"uName\" name=\"uName\"><input type=\"submit\" value=\"Submit\"></form>", spyController.login());
    }

    @Test
    public void findProfile_sendsError_ifUserNotFound() {
        when(mockService.userInDataMap(anyString())).thenReturn(false);

        assertEquals("Error! 404 Error, Will Robinson!", spyController.findProfile("Error"));
        verify(mockService, times(1)).userInDataMap(anyString());
    }

    @Test
    public void findProfile_returnsProfile_ifUserFound() {
        when(mockService.userInDataMap(anyString())).thenReturn(true);
        doNothing().when(mockService).setCurrentUser(anyString());
        doReturn("Test").when(spyController).retrieveProfile(anyString());

        assertEquals("Test", spyController.findProfile("Polyphemus"));
        verify(mockService, times(1)).userInDataMap(anyString());
        verify(spyController, times(1)).retrieveProfile(anyString());
    }

    @Test
    public void goHome_displaysHomepageWithAggregateTimeline() {
        doReturn("Test").when(spyController).sendHome();

        assertEquals("Test", spyController.goHome());
        verify(spyController, times(1)).sendHome();
    }

    @Test
    public void error_returnsErrorMessage() {
        assertEquals("Error! 404 Error, Will Robinson!", spyController.error());
    }

    @Test
    public void retrieveProfile_returnsProfileAsHTML() {
        doCallRealMethod().when(mockService).setCurrentUser(anyString());
        when(mockService.getUser(anyString())).thenReturn(testUser);
        when(mockService.getCurrentUser()).thenReturn(testUser);
        mockService.setCurrentUser("Any");
        doReturn("Test1").when(spyController).assembleProfileHeader(any(User.class));
        doReturn("Test2").when(spyController).assembleFollowingList(any(User.class));
        doReturn("Test3").when(spyController).assembleTimeline(any(User.class));

        assertEquals("Test1Test2Test3", spyController.retrieveProfile("Test"));
        verify(mockService, times(2)).getUser(anyString());
        verify(spyController, times(1)).assembleProfileHeader(any(User.class));
        verify(spyController, times(1)).assembleFollowingList(any(User.class));
        verify(spyController, times(1)).assembleTimeline(any(User.class));
    }

    @Test
    public void assembleProfileHeader_displaysProfileHeaderHTML() {
        when(mockService.getCurrentUser()).thenReturn(testUser);

        assertEquals("<h2>Test</h2><form action=\"profile\" method=\"get\"><label for=\"uName\">Search: </label><input type=\"text\" id=\"uName\" name=\"uName\"><input type=\"submit\" value=\"Submit\"></form><a href=\"http://localhost:8080/home?uName=Test\">Home</a>", spyController.assembleProfileHeader(testUser));
    }

    @Test
    public void assembleFollowingList_displaysFollowingListAsHTML() {
        assertEquals("<br><h3>Following: </h3><ul><li><a href=\"http://localhost:8080/profile?uName=Test2\">Test2</a></li><li><a href=\"http://localhost:8080/profile?uName=Test3\">Test3</a></li></ul>", spyController.assembleFollowingList(testUser));
    }

    @Test
    public void assembleTimeline_displaysTimelineAsHTML() {
        when(mockService.getTimestamp(anyString())).thenReturn("3 hour(s) ago").thenReturn("1 day(s) ago");

        assertEquals("<br><h3>Timeline: </h3>It's a good day to code (3 hour(s) ago)<br>Testing said code can be rough (1 day(s) ago)<br>", spyController.assembleTimeline(testUser));
    }

    @Test
    public void sendHome_displaysUsersHomepageAsHTML() {
        when(mockService.getCurrentUser()).thenReturn(testUser);
        doReturn("Test1").when(spyController).assembleHomeHeader(any(User.class));
        doReturn("Test2").when(spyController).assembleAggregateTimeline();

        assertEquals("Test1Test2", spyController.sendHome());

        verify(spyController, times(1)).assembleHomeHeader(any(User.class));
        verify(spyController, times(1)).assembleAggregateTimeline();
    }

    @Test
    public void assembleHomeHeader_returnsHeaderHTML() {
        when(mockService.getCurrentUser()).thenReturn(testUser);

        assertEquals("<h2>Test</h2><a href=\"http://localhost:8080/profile?uName=Test\">Profile</a>", spyController.assembleHomeHeader(testUser));
    }

    @Test
    public void assembleAggregateTimeline_returnsAggregateTimelineAsHTML() {
        assertEquals("<br><h3>Timeline: </h3>", spyController.assembleAggregateTimeline());
    }
}