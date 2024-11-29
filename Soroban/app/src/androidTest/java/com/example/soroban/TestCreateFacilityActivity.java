package com.example.soroban;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Intent;
import android.os.Bundle;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.action.ViewActions;

import com.example.soroban.activity.OrganizerDashboardActivity;
import com.example.soroban.model.User;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class TestCreateFacilityActivity {
    private User mockUser(){
        return new User("testId");
    }

    private static Intent createFacilityActivityIntent(User appUser) {
        appUser.createFacility();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setClassName("com.example.soroban", "com.example.soroban.activity.CreateFacilityActivity");
        Bundle newArgs = new Bundle();
        newArgs.putSerializable("appUser",appUser);
        newArgs.putSerializable("thisFacility",appUser.getFacility());
        intent.putExtras(newArgs);
        return intent;
    }

    @Test
    public void testCreateFacilityActivityDisplayed() {
        User appUser = mockUser();
        ActivityScenario<OrganizerDashboardActivity> scenario = ActivityScenario.launch(createFacilityActivityIntent(appUser));

        // Check if the button and details are displayed
        onView(withId(R.id.buttonCreateFacility)).check(matches(isDisplayed()));
        onView(withId(R.id.buttonCreateFacility)).check(matches(withText("Create My Facility")));
    }

    @Test
    public void testFacilityCreate() {
        User appUser = mockUser();
        ActivityScenario<OrganizerDashboardActivity> scenario = ActivityScenario.launch(createFacilityActivityIntent(appUser));

        // Perform facility changes
        onView(withId(R.id.editTextFacilityName)).perform(ViewActions.clearText());
        onView(withId(R.id.editTextFacilityName)).perform(ViewActions.typeText("New MockFacility"));
        onView(withId(R.id.editTextFacilityDetails)).perform(ViewActions.clearText());
        onView(withId(R.id.editTextFacilityDetails)).perform(ViewActions.typeText("Read A Certain Magical Index"));

        // Click to confirm facility changes
        onView(withId(R.id.buttonCreateFacility)).perform(click());

        // Click to view facility changes
        onView(withId(R.id.btn_go_to_facility)).perform(click());

        onView(withId(R.id.tv_facility_name)).check(matches(withText("New MockFacility")));
        onView(withId(R.id.tv_facility_details)).check(matches(withText("Read A Certain Magical Index")));
    }


}
