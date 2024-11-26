package com.example.soroban;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.soroban.model.Event;
import com.example.soroban.model.User;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class TestEventRegistrationActivity {

    private User mockUser() {
        return new User("testId");
    }

    private Event mockEvent() {
        Event event = new Event();
        event.setEventName("Test Event");
        event.setEventDetails("This is a test event.");
        event.setEventDate(new java.util.Date());
        event.setDrawDate(new java.util.Date(System.currentTimeMillis() + 86400000)); // Draw date is tomorrow
        return event;
    }

    @Rule
    public ActivityScenarioRule<EventDetailsActivity> scenarioRule =
            new ActivityScenarioRule<>(EventDetailsActivity.class);

    @Test
    public void testRegisterBtn() {
        // Launch the activity with test data
        Intent intent = new Intent();
        intent.putExtra("eventData", mockEvent());
        intent.putExtra("appUser", mockUser());
        intent.putExtra("isRegistered", false);
        ActivityScenario<EventDetailsActivity> scenario = ActivityScenario.launch(intent);

        // Perform click on the register button
        onView(withId(R.id.btn_register)).perform(click());
        // Check if the unregister button is displayed
        onView(withId(R.id.btn_unregister)).check(matches(isDisplayed()));
    }

    // for some reason error on this one, cause animations or transitions are enabled or something
    @Test
    public void testUnregisterBtn() {
        // Launch the activity with test data
        Intent intent = new Intent();
        intent.putExtra("eventData", mockEvent());
        intent.putExtra("appUser", mockUser());
        intent.putExtra("isRegistered", true);
        ActivityScenario<EventDetailsActivity> scenario = ActivityScenario.launch(intent);

        // Perform click on the unregister button
        onView(withId(R.id.btn_unregister)).perform(click());
        // Check if the unregister button is displayed
        onView(withId(R.id.btn_register)).check(matches(isDisplayed()));
    }
}