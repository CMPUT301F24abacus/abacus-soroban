package com.example.soroban;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.filters.LargeTest;

import com.example.soroban.activity.OrganizerEventViewDetailsActivity;
import com.example.soroban.model.Event;
import com.example.soroban.model.Facility;
import com.example.soroban.model.User;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Date;

@RunWith(JUnit4.class)
@LargeTest
public class OrganizerEventViewDetailsActivityTest {

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

    // null event name again for some reason
    @Test
    public void testOrganizerEventViewActivityDisplayed() {
        User appUser = mockUser();
        Event mockEvent = mockEvent(appUser);
        ActivityScenario<OrganizerEventViewDetailsActivity> scenario = ActivityScenario.launch(createOrganizerEventViewActivityIntent(appUser, mockEvent));
        // Check if the button is displayed
        onView(withId(R.id.eventNameTitle)).check(matches(isDisplayed()));
    }

    @Test
    public void testEditEventBtn() {
        User appUser = mockUser();
        Event mockEvent = mockEvent(appUser);
        ActivityScenario<OrganizerEventViewDetailsActivity> scenario = ActivityScenario.launch(createOrganizerEventViewActivityIntent(appUser, mockEvent));

        onView(withId(R.id.buttonEditEventDetails)).check(matches(isDisplayed()));
        onView(withId(R.id.buttonEditEventDetails)).perform(click());
        onView(withId(R.id.eventEditTitle)).check(matches(isDisplayed()));
    }

    @Test
    public void testViewEntrantsBtn() {
        User appUser = mockUser();
        Event mockEvent = mockEvent(appUser);
        ActivityScenario<OrganizerEventViewDetailsActivity> scenario = ActivityScenario.launch(createOrganizerEventViewActivityIntent(appUser, mockEvent));

        onView(withId(R.id.buttonViewEntrants)).check(matches(isDisplayed()));
        onView(withId(R.id.buttonViewEntrants)).perform(click());
        onView(withId(R.id.buttonWaitlistedUsers)).check(matches(isDisplayed()));
    }



}
