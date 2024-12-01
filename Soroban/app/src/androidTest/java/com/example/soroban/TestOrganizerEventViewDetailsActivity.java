package com.example.soroban;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;

import android.content.Intent;
import android.support.test.rule.GrantPermissionRule;
import android.view.View;

import androidx.test.core.app.ActivityScenario;
import androidx.test.filters.LargeTest;

import com.example.soroban.activity.OrganizerEventViewDetailsActivity;
import com.example.soroban.model.Event;
import com.example.soroban.model.Facility;
import com.example.soroban.model.User;


import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Date;

@RunWith(JUnit4.class)
@LargeTest
public class TestOrganizerEventViewDetailsActivity {

    private User mockUser(){
        return new User("testId");
    }

    private Event mockEvent(User owner){
        return new Event(owner, owner.createFacility(), "mockEvent", new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()), 5);
    }

    private Facility mockFacility(User owner){
        return new Facility(owner);
    }

    private static Intent createOrganizerEventViewActivityIntent(User appUser, Event mockEvent) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setClassName("com.example.soroban", "com.example.soroban.activity.OrganizerEventViewDetailsActivity");;
        intent.putExtra("selectedEvent", mockEvent);
        intent.putExtra("appUser", appUser);
        return intent;
    }

    @Test
    public void testOrganizerEventViewActivityDisplayed() {
        User appUser = mockUser();
        Event mockEvent = mockEvent(appUser);
        appUser.addHostedEvent(mockEvent);
        ActivityScenario<OrganizerEventViewDetailsActivity> scenario = ActivityScenario.launch(createOrganizerEventViewActivityIntent(appUser, mockEvent));
        // Check if the button is displayed
        onView(withId(R.id.eventNameTitle)).check(matches(isDisplayed()));
    }

    @Test
    public void testEditEventBtn() {
        User appUser = mockUser();
        Event mockEvent = mockEvent(appUser);
        appUser.addHostedEvent(mockEvent);
        ActivityScenario<OrganizerEventViewDetailsActivity> scenario = ActivityScenario.launch(createOrganizerEventViewActivityIntent(appUser, mockEvent));

        onView(withId(R.id.buttonEditEventDetails)).check(matches(isDisplayed()));
        onView(withId(R.id.buttonEditEventDetails)).perform(click());
        onView(withId(R.id.eventEditTitle)).check(matches(isDisplayed()));
    }

    @Test
    public void testViewEntrantsBtn() {
        User appUser = mockUser();
        Event mockEvent = mockEvent(appUser);
        appUser.addHostedEvent(mockEvent);
        ActivityScenario<OrganizerEventViewDetailsActivity> scenario = ActivityScenario.launch(createOrganizerEventViewActivityIntent(appUser, mockEvent));

        onView(withId(R.id.buttonViewEntrants)).check(matches(isDisplayed()));
        onView(withId(R.id.buttonViewEntrants)).perform(click());
        onView(withId(R.id.buttonWaitlistedUsers)).check(matches(isDisplayed()));
    }

    @Test
    public void testNoGeolocationBtn() {
        User appUser = mockUser();
        Event mockEvent = mockEvent(appUser);

        appUser.addHostedEvent(mockEvent);
        ActivityScenario<OrganizerEventViewDetailsActivity> scenario = ActivityScenario.launch(createOrganizerEventViewActivityIntent(appUser, mockEvent));

        onView(withId(R.id.buttonEventGeolocation)).check(matches(isDisplayed()));

        // Event without geolocation requirement
        onView(withId(R.id.buttonEventGeolocation)).perform(click());
        onView(withId(R.id.eventNameTitle)).check(matches(isDisplayed()));
    }

    /* Ideally, permission for Coarse Location would be automatically granted with the following:
        @Rule
            public GrantPermissionRule mRuntimePermissionRule = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION);

        Reference: https://stackoverflow.com/questions/50403128/how-to-grant-permissions-to-android-instrumented-tests

       However, an error is thrown when this rule is applied, causing all tests to fail when otherwise they succeed.
       As such, for now, the step to grant permission to location is manual.
     */

    @Test
    public void testGeolocationBtn() {
        User appUser = mockUser();
        Event mockEvent = mockEvent(appUser);
        appUser.addHostedEvent(mockEvent);
        mockEvent.setRequiresGeolocation(true);
        ActivityScenario<OrganizerEventViewDetailsActivity> scenario = ActivityScenario.launch(createOrganizerEventViewActivityIntent(appUser, mockEvent));

        onView(withId(R.id.buttonEventGeolocation)).check(matches(isDisplayed()));

        // Event with geolocation requirement
        onView(withId(R.id.buttonEventGeolocation)).perform(click());
        onView(withId(R.id.map_view)).check(matches(isDisplayed()));
    }

    // Currently no means to display QR codes as they are retrieved from Firebase.

}
