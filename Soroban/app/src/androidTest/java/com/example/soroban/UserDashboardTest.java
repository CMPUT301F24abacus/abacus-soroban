package com.example.soroban;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import com.example.soroban.activity.UserDashboardActivity;

@RunWith(AndroidJUnit4.class)
public class UserDashboardTest {

    @Rule
    public ActivityScenarioRule<UserDashboardActivity> activityScenarioRule =
            new ActivityScenarioRule<>(UserDashboardActivity.class);

    @Test
    public void testDashboardTitleIsDisplayed() {
        // Check if the dashboard title "Dashboard" is displayed
        onView(withId(R.id.tv_dashboard)).check(matches(isDisplayed()));
        onView(withId(R.id.tv_dashboard)).check(matches(withText("Dashboard")));
    }

    @Test
    public void testScanQrButtonIsDisplayedAndClickable() {
        // Check if the Scan QR Code button is displayed and clickable
        onView(withId(R.id.btn_scan_qr_code)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_scan_qr_code)).perform(click());
        // Add more checks based on what happens after clicking
    }

    @Test
    public void testJoinWaitingListButtonIsDisplayedAndClickable() {
        // Check if the Join Waiting List button is displayed and clickable
        onView(withId(R.id.btn_join_waiting_list)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_join_waiting_list)).perform(click());
        // Add more checks based on what happens after clicking
    }

    @Test
    public void testConfirmedEventsCardIsDisplayed() {
        // Check if the "Confirmed Events" section is displayed correctly
        onView(withId(R.id.btn_manage_facility)).check(matches(isDisplayed()));
    }
}
