package com.example.soroban;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.filters.LargeTest;

import com.example.soroban.activity.EventEntrantsListActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
@LargeTest
public class TestEventEntrantsListActivity {

    @Rule
    public ActivityScenarioRule<EventEntrantsListActivity> scenario = new
            ActivityScenarioRule<EventEntrantsListActivity>(EventEntrantsListActivity.class);

    @Test
    public void testWaitListUserBtn() {
        // Click on the button
        onView(withId(R.id.buttonWaitlistedUsers)).perform(click());
        // Check if next screen is displayed
        onView(withId(R.id.usersWaitlistedTitle)).check(matches(isDisplayed()));
    }

    @Test
    public void testInvitedUserBtn() {
        // Click on the button
        onView(withId(R.id.buttonInviteUsers)).perform(click());
        // Check if next screen is displayed
        onView(withId(R.id.usersInvitedTitle)).check(matches(isDisplayed()));
    }

    @Test
    public void testCancelledUserBtn() {
        // Click on the button
        onView(withId(R.id.buttonCancelledUsers)).perform(click());
        // Check if next screen is displayed
        onView(withId(R.id.usersCancelledTitle)).check(matches(isDisplayed()));
    }

    @Test
    public void testConfirmedUserBtn() {
        // Click on the button
        onView(withId(R.id.buttonConfirmedUsers)).perform(click());
        // Check if next screen is displayed
        onView(withId(R.id.usersFinalListTitle)).check(matches(isDisplayed()));
    }

}
