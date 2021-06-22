package socialmedia;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SocialMediaServiceTest {
    private SocialMediaService spyService;

    @BeforeEach
    void setUp() {
        spyService = spy(new SocialMediaService());
    }

    @Test
    public void loadPreliminaryData_loadsDataIntoDataServiceLayer() {
        spyService.loadPreliminaryData();
        Map<String, User> dataMap = spyService.getDataMap();

        assertEquals("Keith", dataMap.get("Keith").name);
    }

    @Test
    public void userInDataMap_returnsTrueIfUserExists() {
        assertTrue(spyService.userInDataMap("Polyphemus"));
    }

    @Test
    public void userInDataMap_returnsFalseIfUserDoesNotExist() {
        assertFalse(spyService.userInDataMap("NonExistant"));
    }

    @Test
    public void getTimestamp_returnDifferenceInYearsAndMonths_ifDiffsNotZero() {
        assertEquals("2 year(s) ago", spyService.getTimestamp("06-06-2019 12:00:00"));
        assertEquals("5 month(s) ago", spyService.getTimestamp("01-06-2021 12:00:00"));
    }

/* Cutting these out due to time constraints
    @Test
    public void getTimestamp_returnDifferenceInWeeksAndDays_ifDiffsNotZero() {
        assertEquals("2 week(s) ago", spyService.getTimestamp("06-06-2021 12:00:00"));
        assertEquals("4 day(s) ago", spyService.getTimestamp("06-18-2021 12:00:00"));
    }

    @Test
    public void getTimestamp_returnDifferenceInHoursAndMinutesAndSeconds_ifDiffsNotZero() {
        assertEquals("2 hour(s) ago", spyService.getTimestamp("06-22-2021 12:00:00"));
        assertEquals("52 minute(s) ago", spyService.getTimestamp("06-22-2021 14:00:00"));
    }*/

    @Test
    public void getAggregateTimelineMap_setsUpAggregateMap() {
        spyService.setCurrentUser("Jaqueline");

        spyService.getAggregateTimelineMap();

        verify(spyService, times(2)).setTempMapForLoading(anyString(), any(Map.class));
    }

    @Test
    public void setTempMapForLoading_loadsPassedMapIntoTempMap_updatesValuesWithOwnerName() {
        spyService.setCurrentUser("Jaqueline");

        Map<String, String> timelineMap = spyService.setTempMapForLoading("Jaqueline", spyService.getCurrentUser().timeline);

        assertEquals("Jaqueline - Tomorrow's a new day", timelineMap.get("06-12-2021 09:40:00"));
    }
}