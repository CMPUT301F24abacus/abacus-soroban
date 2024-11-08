package com.example.soroban;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.filters.LargeTest;

import com.example.soroban.activity.EventUsersWaitlistedActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
@LargeTest
public class TestEventUsersWaitlistedActivity {

    @Rule
    public ActivityScenarioRule<EventUsersWaitlistedActivity> scenario = new
            ActivityScenarioRule<EventUsersWaitlistedActivity>(EventUsersWaitlistedActivity.class);

    @Test
    public void testTitleDisplayed() {
        // Check if screen is displayed
        onView(withId(R.id.usersWaitlistedTitle)).check(matches(isDisplayed()));
    }

}
