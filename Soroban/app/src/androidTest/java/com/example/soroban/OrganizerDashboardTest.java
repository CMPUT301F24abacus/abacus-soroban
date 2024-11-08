package com.example.soroban;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;

import android.content.Intent;
import android.widget.ListView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.soroban.activity.OrganizerDashboardActivity;
import com.example.soroban.activity.UserDashboardActivity;
import com.example.soroban.adapter.EventArrayAdapter;
import com.example.soroban.model.Event;
import com.example.soroban.model.EventList;
import com.example.soroban.model.Facility;
import com.example.soroban.model.User;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

@RunWith(AndroidJUnit4.class)
public class OrganizerDashboardTest {

    private User mockUser(){
        return new User("testId");
    }

    private Event mockEvent(User owner){
        return new Event(owner, owner.createFacility(), "mockEvent", new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()), 5);
    }

    private Facility mockFacility(User owner){
        return new Facility(owner);
    }

    private static Intent createOrganizerDashboardIntent(User appUser) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setClassName("com.example.soroban", "com.example.soroban.activity.OrganizerDashboardActivity");
        intent.putExtra("appUser", appUser);
        return intent;
    }

    @Test
    public void testDashboardTitleIsDisplayed() {
        User appUser = mockUser();
        ActivityScenario<OrganizerDashboardActivity> scenario = ActivityScenario.launch(createOrganizerDashboardIntent(appUser));
        // Check if the dashboard title "Organizer Dashboard" is displayed
        onView(withId(R.id.toolbar)).check(matches(isDisplayed()));
        onView(withId(R.id.organizer_dashboard_tv)).check(matches(withText("Organizer Dashboard")));
    }

    @Test
    public void testCreateFacilityBtn() {
        User appUser = mockUser();
        ActivityScenario<OrganizerDashboardActivity> scenario = ActivityScenario.launch(createOrganizerDashboardIntent(appUser));

        onView(withId(R.id.createEventButton)).check(matches(isDisplayed()));
        onView(withId(R.id.createEventButton)).perform(click());
        onView(withId(R.id.editTextFacilityName)).check(matches(isDisplayed()));
    }

    @Test
    public void testCreateEventBtn() {
        User appUser = mockUser();
        appUser.createFacility();
        ActivityScenario<OrganizerDashboardActivity> scenario = ActivityScenario.launch(createOrganizerDashboardIntent(appUser));

        onView(withId(R.id.createEventButton)).check(matches(isDisplayed()));
        onView(withId(R.id.createEventButton)).perform(click());
        onView(withId(R.id.eventCreateTitle)).check(matches(isDisplayed()));
    }

    @Test
    public void goToFacilityBtn() {
        User appUser = mockUser();
        appUser.createFacility();
        appUser.getFacility().setName("MockFacility");
        appUser.getFacility().setDetails("Read A Certain Magical Index");
        ActivityScenario<OrganizerDashboardActivity> scenario = ActivityScenario.launch(createOrganizerDashboardIntent(appUser));

        onView(withId(R.id.btn_go_to_facility)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_go_to_facility)).perform(click());
        onView(withId(R.id.tv_facility_name)).check(matches(withText("MockFacility")));
        onView(withId(R.id.tv_facility_details)).check(matches(withText("Read A Certain Magical Index")));
    }

    @Test
    public void testEventListView() {
        User appUser = mockUser();
        appUser.createFacility();
        Event mockEvent = mockEvent(appUser);
        appUser.addHostedEvent(mockEvent);

        ActivityScenario<OrganizerDashboardActivity> scenario = ActivityScenario.launch(createOrganizerDashboardIntent(appUser));

        EventList mockHostedList = appUser.getHostedEvents();
        scenario.onActivity( activity -> {
            EventArrayAdapter mockEventAdapter = new EventArrayAdapter(activity, mockHostedList);
            ListView listView = activity.findViewById(R.id.eventsListView);
            listView.setAdapter(mockEventAdapter);
        });

        onView(withId(R.id.eventsListView)).check(matches(isDisplayed()));
        onData(is(instanceOf(Event.class))).inAdapterView(withId(R.id.eventsListView))
                .atPosition(0)
                .onChildView(withId(R.id.tv_event_name))
                .check(matches((withText(mockEvent.getEventName()))));
    }

}
