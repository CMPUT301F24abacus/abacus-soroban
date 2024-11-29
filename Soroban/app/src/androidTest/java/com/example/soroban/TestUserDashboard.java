package com.example.soroban;

import androidx.annotation.NonNull;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

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

import com.example.soroban.activity.UserDashboardActivity;
import com.example.soroban.adapter.EventArrayAdapter;
import com.example.soroban.model.Event;
import com.example.soroban.model.EventList;
import com.example.soroban.model.User;

import java.util.Date;

@RunWith(AndroidJUnit4.class)
public class TestUserDashboard {
    private User mockUser(){
        return new User("testId");
    }

    private Event mockEvent(User owner){
        return new Event(owner, owner.createFacility(), "mockEvent", new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()), 5);
    }

    //@Rule
    //public ActivityScenarioRule<UserDashboardActivity> activityScenarioRule =
    //new ActivityScenarioRule<>(UserDashboardActivity.class);

    private static Intent createUserDashboardIntent(User appUser) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setClassName("com.example.soroban", "com.example.soroban.activity.UserDashboardActivity");
        intent.putExtra("appUser", appUser);
        return intent;
    }

    @Test
    public void testDashboardTitleIsDisplayed() {
        User appUser = mockUser();
        ActivityScenario<UserDashboardActivity> scenario = ActivityScenario.launch(createUserDashboardIntent(appUser));
        // Check if the dashboard title "Dashboard" is displayed
        onView(withId(R.id.tv_dashboard)).check(matches(isDisplayed()));
        onView(withId(R.id.tv_dashboard)).check(matches(withText("Dashboard")));
    }

    @Test
    public void testScanQrButtonIsDisplayedAndClickable() {
        User appUser = mockUser();
        ActivityScenario<UserDashboardActivity> scenario = ActivityScenario.launch(createUserDashboardIntent(appUser));
        // Check if the Scan QR Code button is displayed and clickable
        onView(withId(R.id.btn_scan_qr_code)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_scan_qr_code)).perform(click());
        onView(withId(R.id.layout_result)).check(matches(isDisplayed()));
        // Add more checks based on what happens after clicking
    }

    @Test
    public void testProfileIconBtn() {
        User appUser = mockUser();
        ActivityScenario<UserDashboardActivity> scenario = ActivityScenario.launch(createUserDashboardIntent(appUser));

        onView(withId(R.id.icon_profile)).check(matches(isDisplayed()));
        onView(withId(R.id.icon_profile)).perform(click());
        onView(withId(R.id.floatingEditButton)).check(matches(isDisplayed()));
    }

    @Test
    public void testNotificationIconBtn() {
        User appUser = mockUser();
        ActivityScenario<UserDashboardActivity> scenario = ActivityScenario.launch(createUserDashboardIntent(appUser));

        onView(withId(R.id.icon_notifications)).check(matches(isDisplayed()));
        onView(withId(R.id.icon_notifications)).perform(click());
        onView(withId(R.id.tv_invitations)).check(matches(isDisplayed()));
    }

    @Test
    public void testWaitListItem() {
        User appUser = mockUser();
        Event mockEvent = mockEvent(appUser);
        appUser.addToWaitlist(mockEvent);

        ActivityScenario<UserDashboardActivity> scenario = ActivityScenario.launch(createUserDashboardIntent(appUser));

        EventList mockWaitList = appUser.getWaitList();
        scenario.onActivity( activity -> {
            EventArrayAdapter mockEventAdapter = new EventArrayAdapter(activity, mockWaitList);
            ListView listView = activity.findViewById(R.id.list_waitlisted_events);
            listView.setAdapter(mockEventAdapter);
        });

        onView(withId(R.id.list_waitlisted_events)).check(matches(isDisplayed()));
        onData(is(instanceOf(Event.class))).inAdapterView(withId(R.id.list_waitlisted_events))
                .atPosition(0)
                .onChildView(withId(R.id.tv_event_name))
                .check(matches((withText(mockEvent.getEventName()))));
    }

    @Test
    public void testConfirmedItem() {
        User appUser = mockUser();
        Event mockEvent = mockEvent(appUser);
        appUser.addRegisteredEvent(mockEvent);
        ActivityScenario<UserDashboardActivity> scenario = ActivityScenario.launch(createUserDashboardIntent(appUser));

        EventList mockRegistered = appUser.getRegisteredEvents();
        scenario.onActivity( activity -> {
            EventArrayAdapter mockEventAdapter = new EventArrayAdapter(activity, mockRegistered);
            ListView listView = activity.findViewById(R.id.list_confirmed_events);
            listView.setAdapter(mockEventAdapter);
        });

        onView(withId(R.id.list_confirmed_events)).check(matches(isDisplayed()));
        onData(is(instanceOf(Event.class))).inAdapterView(withId(R.id.list_confirmed_events))
                .atPosition(0)
                .onChildView(withId(R.id.tv_event_name))
                .check(matches((withText(mockEvent.getEventName()))));
    }

    @Test
    public void testWaitingEventClick(){
        User appUser = mockUser();
        Event mockEvent = mockEvent(appUser);
        appUser.addToWaitlist(mockEvent);
        ActivityScenario<UserDashboardActivity> scenario = ActivityScenario.launch(createUserDashboardIntent(appUser));

        EventList mockWaitList = appUser.getWaitList();
        scenario.onActivity( activity -> {
            EventArrayAdapter mockEventAdapter = new EventArrayAdapter(activity, mockWaitList);
            ListView listView = activity.findViewById(R.id.list_waitlisted_events);
            listView.setAdapter(mockEventAdapter);

            listView.performItemClick(mockEventAdapter.getView(0,null,null), 0, mockEventAdapter.getItemId(0));
        });

        onView(withId(R.id.activity_event_details)).check(matches(isDisplayed()));
    }


    @Test
    public void testRegisteredEventClick(){
        User appUser = mockUser();
        Event mockEvent = mockEvent(appUser);
        appUser.addRegisteredEvent(mockEvent);
        ActivityScenario<UserDashboardActivity> scenario = ActivityScenario.launch(createUserDashboardIntent(appUser));

        EventList mockRegisteredList = appUser.getRegisteredEvents();
        scenario.onActivity( activity -> {
            EventArrayAdapter mockEventAdapter = new EventArrayAdapter(activity, mockRegisteredList);
            ListView listView = activity.findViewById(R.id.list_confirmed_events);
            listView.setAdapter(mockEventAdapter);

            listView.performItemClick(mockEventAdapter.getView(0,null,null), 0, mockEventAdapter.getItemId(0));
        });

        onView(withId(R.id.activity_event_details)).check(matches(isDisplayed()));
    }
}
