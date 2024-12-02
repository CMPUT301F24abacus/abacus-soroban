package com.example.soroban;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.soroban.activity.AdminDashboardActivity;
import com.example.soroban.activity.UserDashboardActivity;
import com.example.soroban.model.User;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class TestAdminDashboardActivity {
    private User mockUser(){
        return new User("testId");
    }

    private static Intent createAdminDashboardIntent(User appUser) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setClassName("com.example.soroban", "com.example.soroban.activity.AdminDashboardActivity");
        intent.putExtra("appUser", appUser);
        return intent;
    }

    @Test
    public void testAdminDashboardIsDisplayed() {
        User appUser = mockUser();
        ActivityScenario<AdminDashboardActivity> scenario = ActivityScenario.launch(createAdminDashboardIntent(appUser));
        // Check if the dashboard is displayed through the buttons
        onView(withId(R.id.browse_event_btn)).check(matches(isDisplayed()));
        onView(withId(R.id.browse_event_btn)).check(matches(withText("Browse Events")));
        onView(withId(R.id.browse_event_btn)).check(matches(isDisplayed()));
    }

    @Test
    public void testBrowseEventBtnClickable() {
        User appUser = mockUser();
        ActivityScenario<AdminDashboardActivity> scenario = ActivityScenario.launch(createAdminDashboardIntent(appUser));

        // Check if the Browse Event Button is Clickable
        onView(withId(R.id.browse_event_btn)).check(matches(isDisplayed()));
        onView(withId(R.id.browse_event_btn)).perform(click());
        onView(withId(R.id.browse_event_text)).check(matches(isDisplayed()));
    }

    @Test
    public void testBrowseProfileBtnClickable() {
        User appUser = mockUser();
        ActivityScenario<AdminDashboardActivity> scenario = ActivityScenario.launch(createAdminDashboardIntent(appUser));

        // Check if the Browse Profile Button is Clickable
        onView(withId(R.id.browse_profile_btn)).check(matches(isDisplayed()));
        onView(withId(R.id.browse_profile_btn)).perform(click());
        onView(withId(R.id.browse_profile_text)).check(matches(isDisplayed()));
    }

    @Test
    public void testBrowseFacilityBtnClickable() {
        User appUser = mockUser();
        ActivityScenario<AdminDashboardActivity> scenario = ActivityScenario.launch(createAdminDashboardIntent(appUser));

        // Check if the Browse Facility Button is Clickable
        onView(withId(R.id.browse_facility_btn)).check(matches(isDisplayed()));
        onView(withId(R.id.browse_facility_btn)).perform(click());
        onView(withId(R.id.browse_facility_text)).check(matches(isDisplayed()));
    }

    @Test
    public void testBrowseImagesBtnClickable() {
        User appUser = mockUser();
        ActivityScenario<AdminDashboardActivity> scenario = ActivityScenario.launch(createAdminDashboardIntent(appUser));

        // Check if the Browse Images Button is Clickable
        onView(withId(R.id.browse_image_btn)).check(matches(isDisplayed()));
        onView(withId(R.id.browse_image_btn)).perform(click());
        onView(withId(R.id.browse_image_text)).check(matches(isDisplayed()));
    }
}
