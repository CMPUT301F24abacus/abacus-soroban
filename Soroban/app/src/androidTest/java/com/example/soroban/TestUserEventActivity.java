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
import android.content.Intent;
import android.os.Bundle;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.filters.LargeTest;

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

@RunWith(JUnit4.class)
@LargeTest
public class TestUserEventActivity {

    private User mockUser(){
        return new User("testId");
    }

    private Event mockEvent(User owner){
        return new Event(owner, owner.createFacility(), "mockEvent", new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()), 5);
    }

    private Facility mockFacility(User owner){
        return new Facility(owner);
    }

    private static Intent createUserEventActivityIntent(User appUser, Event mockEvent, String listType) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setClassName("com.example.soroban", "com.example.soroban.activity.UserEventActivity");
        Bundle newArgs = new Bundle();
        newArgs.putSerializable("selectedEvent", mockEvent);
        newArgs.putSerializable("appUser", appUser);
        newArgs.putString("listType", listType);
        intent.putExtras(newArgs);
        return intent;
    }

    // Doesn't work, event is null for some reason
    @Test
    public void testUserEventActivityDisplayed() {
        User appUser = mockUser();
        Event mockEvent = mockEvent(appUser);
        ActivityScenario<UserEventActivity> scenario = ActivityScenario.launch(createUserEventActivityIntent(appUser, mockEvent, "registeredEvents"));
        // Check if the unregister button is displayed
        onView(withId(R.id.btn_register)).check(matches(withText("Unregister")));
    }

    // WHAT IS GOING ON??!!!!
    @Test
    public void testEventDetailsDisplayed() {
        User appUser = mockUser();
        Event mockEvent = mockEvent(appUser);
        mockEvent.setEventDetails("Read A Certain Magical Index");
        ActivityScenario<UserEventActivity> scenario = ActivityScenario.launch(createUserEventActivityIntent(appUser, mockEvent, "registeredEvents"));
        // Check if details are correctly displayed
        onView(withId(R.id.event_name)).check(matches(withText(mockEvent.getEventName())));
        String eventDetails = "Event Date: " + mockEvent.getEventDate().toString() + "\nEvent Details: " + mockEvent.getEventDetails();
        onView(withId(R.id.event_details)).check(matches(withText(eventDetails)));
    }

    // Another Firebase Error, my computer doesn't work :(
    @Test
    public void testUnregisterBtnWaitList() {
        User appUser = mockUser();
        Event mockEvent = mockEvent(appUser);
        appUser.addToWaitlist(mockEvent);
        mockEvent.setEventDetails("Read A Certain Magical Index");
        ActivityScenario<UserEventActivity> scenario = ActivityScenario.launch(createUserEventActivityIntent(appUser, mockEvent, "waitList"));

        onView(withId(R.id.btn_register)).perform(click());
        onView(withText("MockEvent")).check(doesNotExist());

    }


}
