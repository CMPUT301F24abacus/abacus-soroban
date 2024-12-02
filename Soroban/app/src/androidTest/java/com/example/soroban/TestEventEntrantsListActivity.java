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
import android.os.Bundle;
import android.widget.ListView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.filters.LargeTest;
import com.example.soroban.activity.EventEntrantsListActivity;
import com.example.soroban.activity.OrganizerEventViewDetailsActivity;
import com.example.soroban.adapter.EventArrayAdapter;
import com.example.soroban.adapter.UserArrayAdapter;
import com.example.soroban.model.Event;
import com.example.soroban.model.EventList;
import com.example.soroban.model.User;
import com.example.soroban.model.UserList;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Date;

@RunWith(JUnit4.class)
@LargeTest
public class TestEventEntrantsListActivity {
    private User mockUser(){
        return new User("testId");
    }

    private Event mockEvent(User owner){
        return new Event(owner, owner.createFacility(), "mockEvent", new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()), 5);
    }

    private static Intent createEventEntrantsListActivityIntent(User appUser, Event mockEvent) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setClassName("com.example.soroban", "com.example.soroban.activity.EventEntrantsListActivity");;
        intent.putExtra("selectedEvent", mockEvent);
        intent.putExtra("appUser", appUser);
        return intent;
    }

    @Test
    public void testWaitListUserBtn() {
        User appUser = mockUser();
        Event mockEvent = mockEvent(appUser);
        appUser.addHostedEvent(mockEvent);
        ActivityScenario<EventEntrantsListActivity> scenario = ActivityScenario.launch(createEventEntrantsListActivityIntent(appUser, mockEvent));

        // Click on the button
        onView(withId(R.id.buttonWaitlistedUsers)).perform(click());
        // Check if next screen is displayed
        onView(withId(R.id.usersWaitlistedTitle)).check(matches(isDisplayed()));

    }

    @Test
    public void testInvitedUserBtn() {
        User appUser = mockUser();
        Event mockEvent = mockEvent(appUser);
        appUser.addHostedEvent(mockEvent);
        ActivityScenario<EventEntrantsListActivity> scenario = ActivityScenario.launch(createEventEntrantsListActivityIntent(appUser, mockEvent));

        // Click on the button
        onView(withId(R.id.buttonInvitedUsers)).perform(click());
        // Check if next screen is displayed
        onView(withId(R.id.usersInvitedTitle)).check(matches(isDisplayed()));
    }
    @Test
    public void testCancelledUserBtn() {
        User appUser = mockUser();
        Event mockEvent = mockEvent(appUser);
        appUser.addHostedEvent(mockEvent);
        ActivityScenario<EventEntrantsListActivity> scenario = ActivityScenario.launch(createEventEntrantsListActivityIntent(appUser, mockEvent));

        // Click on the button
        onView(withId(R.id.buttonCancelledUsers)).perform(click());
        // Check if next screen is displayed
        onView(withId(R.id.usersCancelledTitle)).check(matches(isDisplayed()));
    }
    @Test
    public void testConfirmedUserBtn() {
        User appUser = mockUser();
        Event mockEvent = mockEvent(appUser);
        appUser.addHostedEvent(mockEvent);
        ActivityScenario<EventEntrantsListActivity> scenario = ActivityScenario.launch(createEventEntrantsListActivityIntent(appUser, mockEvent));

        // Click on the button
        onView(withId(R.id.buttonConfirmedUsers)).perform(click());
        // Check if next screen is displayed
        onView(withId(R.id.usersFinalListTitle)).check(matches(isDisplayed()));
    }

    @Test
    public void testSampleBtnNoEntrants() {
        User appUser = mockUser();
        Event mockEvent = mockEvent(appUser);
        appUser.addHostedEvent(mockEvent);
        ActivityScenario<EventEntrantsListActivity> scenario = ActivityScenario.launch(createEventEntrantsListActivityIntent(appUser, mockEvent));

        // Click on the button
        onView(withId(R.id.buttonSampleUsers)).perform(click());
        // Nothing happens as mockEvent has no invites
    }
}