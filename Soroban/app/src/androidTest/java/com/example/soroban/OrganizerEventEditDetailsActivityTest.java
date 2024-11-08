package com.example.soroban;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.filters.LargeTest;

import com.example.soroban.activity.OrganizerDashboardActivity;
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
public class OrganizerEventEditDetailsActivityTest {

    private User mockUser(){
        return new User("testId");
    }

    private Event mockEvent(User owner){
        return new Event(owner, owner.createFacility(), "mockEvent", new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()), 5);
    }

    private Facility mockFacility(User owner){
        return new Facility(owner);
    }

    @Rule
    public ActivityScenarioRule<OrganizerEventEditDetailsActivity> scenario = new
            ActivityScenarioRule<OrganizerEventEditDetailsActivity>(OrganizerEventEditDetailsActivity.class);

    @Test
    public void testOrganizerEventEditActivityDisplayed() {
        // Check if the button is displayed
        onView(withId(R.id.eventEditTitle)).check(matches(isDisplayed()));
    }



}
