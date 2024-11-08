package com.example.soroban;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.app.Activity;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.soroban.activity.EventRegistrationActivity;
import com.example.soroban.activity.OrganizerDashboardActivity;
import com.example.soroban.model.User;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class TestEventRegistrationActivity {

    private User mockUser(){
        return new User("testId");
    }

    @Rule
    public ActivityScenarioRule<EventRegistrationActivity> scenario = new
            ActivityScenarioRule<EventRegistrationActivity>(EventRegistrationActivity.class);

    @Test
    public void testRegisterBtn() {
        onView(withId(R.id.btn_register)).perform(click());
        // Check if the unregister button is displayed
        onView(withId(R.id.btn_unregister)).check(matches(isDisplayed()));
    }

    // for some reason error on this one, cause animations or transitions are enabled or something
    @Test
    public void testUnregisterBtn() {
        onView(withId(R.id.btn_unregister)).perform(click());
        // Check if the unregister button is displayed
        onView(withId(R.id.btn_register)).check(matches(isDisplayed()));
    }



}
