package com.example.soroban;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.filters.LargeTest;

import com.example.soroban.activity.EventEntrantsListActivity;
import com.example.soroban.activity.EventUsersCancelledActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
@LargeTest
public class TestEventUsersCancelledActivity {

    @Rule
    public ActivityScenarioRule<EventUsersCancelledActivity> scenario = new
            ActivityScenarioRule<EventUsersCancelledActivity>(EventUsersCancelledActivity.class);

    @Test
    public void testTitleDisplayed() {
        // Check if screen is displayed
        onView(withId(R.id.usersCancelledTitle)).check(matches(isDisplayed()));
    }

}
