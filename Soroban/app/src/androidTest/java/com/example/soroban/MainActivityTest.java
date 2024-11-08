package com.example.soroban;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static org.hamcrest.Matchers.notNullValue;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.test.core.app.ActivityScenario;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.soroban.activity.OrganizerDashboardActivity;
import com.example.soroban.activity.UserDashboardActivity;
import com.example.soroban.model.User;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityTest {

    // This one doesn't work at all :(, Firebase

    private User mockUser(){
        return new User("testId");
    }

    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new
            ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void testOpenDashboardBtn() {
        // Click on Open Dashboard Button
        onView(withId(R.id.btn_open_dashboard)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_open_dashboard)).perform(click());
        // Check if UserDashboard in displayed
        onView(withId(R.id.tv_dashboard)).check(matches(isDisplayed()));

    }

    @Test
    public void testOpenOrganizerDashboardBtn() {
        // Click on Open Organizer Dashboard Button
        onView(withId(R.id.btn_open_organizer_dashboard)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_open_organizer_dashboard)).perform(click());
        // Check if OrganizerUserDashboard in displayed
        onView(withId(R.id.toolbar)).check(matches(isDisplayed()));
    }


}
