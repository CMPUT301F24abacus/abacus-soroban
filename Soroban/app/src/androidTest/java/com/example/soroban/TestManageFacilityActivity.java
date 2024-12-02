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
public class TestManageFacilityActivity {

    private User mockUser(){
        return new User("testId");
    }

    private static Intent createManageFacilityActivityIntent(User appUser) {
        appUser.createFacility();
        appUser.getFacility().setName("MockFacility");
        appUser.getFacility().setDetails("Mock Facility Test Details");
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setClassName("com.example.soroban", "com.example.soroban.activity.ManageFacilityActivity");
        Bundle newArgs = new Bundle();
        newArgs.putSerializable("appUser",appUser);
        intent.putExtras(newArgs);
        return intent;
    }

    @Test
    public void testManageFacilityActivityDisplayed() {
        User appUser = mockUser();
        ActivityScenario<OrganizerDashboardActivity> scenario = ActivityScenario.launch(createManageFacilityActivityIntent(appUser));

        // Check if the button and details are displayed
        onView(withId(R.id.buttonCreateFacility)).check(matches(isDisplayed()));
        onView(withId(R.id.buttonCreateFacility)).check(matches(withText("Confirm Changes")));
        onView(withId(R.id.editTextFacilityName)).check(matches(withText(appUser.getFacility().getName())));
        onView(withId(R.id.editTextFacilityDetails)).check(matches(withText(appUser.getFacility().getDetails().toString())));
    }

    @Test
    public void testFacilityManage() {
        User appUser = mockUser();
        ActivityScenario<OrganizerDashboardActivity> scenario = ActivityScenario.launch(createManageFacilityActivityIntent(appUser));

        // Perform facility changes
        onView(withId(R.id.editTextFacilityName)).perform(ViewActions.clearText());
        onView(withId(R.id.editTextFacilityName)).perform(ViewActions.typeText("New MockFacility"));
        onView(withId(R.id.editTextFacilityDetails)).perform(ViewActions.clearText());
        onView(withId(R.id.editTextFacilityDetails)).perform(ViewActions.typeText("Read A Certain Magical Index"));

        // Click to confirm facility changes
        onView(withId(R.id.buttonCreateFacility)).perform(click());

        // Click to view facility changes
        onView(withId(R.id.btn_go_to_facility)).perform(click());

        //onView(withId(R))
        onView(withId(R.id.tv_facility_name)).check(matches(withText("New MockFacility")));
        onView(withId(R.id.tv_facility_details)).check(matches(withText("Read A Certain Magical Index")));
    }


}