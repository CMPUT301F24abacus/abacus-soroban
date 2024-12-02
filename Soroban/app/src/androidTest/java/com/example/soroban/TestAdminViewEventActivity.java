package com.example.soroban;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.test.core.app.ActivityScenario;
import androidx.test.filters.LargeTest;

import com.example.soroban.activity.AdminViewEventActivity;
import com.example.soroban.activity.UserEventActivity;
import com.example.soroban.model.Event;
import com.example.soroban.model.User;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@RunWith(JUnit4.class)
@LargeTest
public class TestAdminViewEventActivity {
    private User mockUser(){
        return new User("testId");
    }

    private Event mockEvent(User owner){
        return new Event(owner, owner.createFacility(), "mockEvent", new Date(System.currentTimeMillis() + 86400000), new Date(System.currentTimeMillis() + 86400000), 5);
    }

    private static Intent createAdminViewEventActivityIntent(User appUser, Event mockEvent) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setClassName("com.example.soroban", "com.example.soroban.activity.AdminViewEventActivity");
        Bundle newArgs = new Bundle();
        newArgs.putSerializable("selectedEvent", mockEvent);
        newArgs.putSerializable("appUser", appUser);
        intent.putExtras(newArgs);
        return intent;
    }

    @Test
    public void testAdminEventDetailsActivityDisplayed() {
        User appUser = mockUser();
        Event mockEvent = mockEvent(appUser);
        mockEvent.setEventDetails("Read A Certain Magical Index");
        ActivityScenario<AdminViewEventActivity> scenario = ActivityScenario.launch(createAdminViewEventActivityIntent(appUser, mockEvent));

        // Check if details are correctly displayed
        onView(withId(R.id.eventTitleAdmin)).check(matches(withText(mockEvent.getEventName())));
        String eventDetails = mockEvent.getEventDetails();
        onView(withId(R.id.eventDescriptionAdmin)).check(matches(withText(eventDetails)));
        String eventDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(mockEvent.getEventDate());
        onView(withId(R.id.eventDateAdmin)).check(matches(withText(eventDate)));
        String drawDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(mockEvent.getDrawDate());
        onView(withId(R.id.eventDrawDateTextAdmin)).check(matches(withText(drawDate)));
        onView(withId(R.id.eventSampleSizeTextAdmin)).check(matches(withText(mockEvent.getSampleSize().toString())));
    }



}
