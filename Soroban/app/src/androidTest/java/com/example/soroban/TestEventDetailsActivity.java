package com.example.soroban;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static com.example.soroban.QRCodeGenerator.generateQRCode;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.soroban.activity.OrganizerDashboardActivity;
import com.example.soroban.activity.UserDashboardActivity;
import com.example.soroban.activity.UserEventActivity;
import com.example.soroban.model.Event;
import com.example.soroban.model.Facility;
import com.example.soroban.model.User;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Date;
import java.util.UUID;

@RunWith(JUnit4.class)
@LargeTest
public class TestEventDetailsActivity {
    private User mockUser(){
        return new User("testId");
    }

    private Event mockEvent(User owner){
        return new Event(owner, owner.createFacility(), "mockEvent", new Date(System.currentTimeMillis() + 86400000), new Date(System.currentTimeMillis() + 86400000), 5);
    }

    private Facility mockFacility(User owner){
        return new Facility(owner);
    }

    private static Intent createEventDetailsActivityIntent(User appUser, Event mockEvent, boolean registered) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setClassName("com.example.soroban", "com.example.soroban.activity.EventDetailsActivity");
        Bundle newArgs = new Bundle();
        newArgs.putSerializable("eventData", mockEvent);
        newArgs.putSerializable("appUser", appUser);
        intent.putExtras(newArgs);
        intent.putExtra("isRegistered", registered);
        return intent;
    }

    @Test
    public void testEventDetailsActivityDisplayed() {
        User appUser = mockUser();
        Event mockEvent = mockEvent(appUser);
        mockEvent.setEventDetails("Read A Certain Magical Index");
        ActivityScenario<UserEventActivity> scenario = ActivityScenario.launch(createEventDetailsActivityIntent(appUser, mockEvent, false));

        // Check if the register button is displayed
        onView(withId(R.id.btn_register)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_register)).check(matches(withText("Register")));

        // Check if details are correctly displayed
        onView(withId(R.id.event_name)).check(matches(withText(mockEvent.getEventName())));
        String eventDetails = "Event Details: " + mockEvent.getEventDetails();
        onView(withId(R.id.event_details)).check(matches(withText(eventDetails)));
        String eventDate = "Event Date: " + mockEvent.getEventDate();
        onView(withId(R.id.event_date)).check(matches(withText(eventDate)));
        String drawDate = "Draw Date: " + mockEvent.getDrawDate();
        onView(withId(R.id.event_draw_date)).check(matches(withText(drawDate)));
    }

    @Test
    public void testUnregisterButton() {
        User appUser = mockUser();
        Event mockEvent = mockEvent(appUser);
        mockEvent.setEventDetails("Read A Certain Magical Index");
        appUser.addToWaitlist(mockEvent);
        ActivityScenario<UserEventActivity> scenario = ActivityScenario.launch(createEventDetailsActivityIntent(appUser, mockEvent, true));

        // Check if the unregister button is displayed
        onView(withId(R.id.btn_unregister)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_unregister)).check(matches(withText("Unregister")));
        onView(withId(R.id.btn_unregister)).perform(click());

        // Check if the register button is displayed
        onView(withId(R.id.btn_register)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_register)).check(matches(withText("Register")));
    }

    @Test
    public void testRegisterButton() {
        User appUser = mockUser();
        Event mockEvent = mockEvent(appUser);
        mockEvent.setEventDetails("Read A Certain Magical Index");
        ActivityScenario<UserEventActivity> scenario = ActivityScenario.launch(createEventDetailsActivityIntent(appUser, mockEvent, false));

        // Check if the register button is displayed
        onView(withId(R.id.btn_register)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_register)).check(matches(withText("Register")));
        onView(withId(R.id.btn_register)).perform(click());

        // Check if the unregister button is displayed
        onView(withId(R.id.btn_unregister)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_unregister)).check(matches(withText("Unregister")));
    }

    // Currently no means to display QR codes as they are retrieved from Firebase.
}
