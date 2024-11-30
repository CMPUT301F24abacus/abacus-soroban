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
public class TestFacilityDisplayActivity {

    private User mockUser(){
        return new User("testId");
    }

    private static Intent createFacilityDisplayActivityIntent(User appUser) {
        appUser.createFacility();
        appUser.getFacility().setName("MockFacility");
        appUser.getFacility().setDetails("Mock Facility Test Details");
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setClassName("com.example.soroban", "com.example.soroban.activity.FacilityDisplayActivity");
        Bundle newArgs = new Bundle();
        newArgs.putSerializable("appUser",appUser);
        newArgs.putSerializable("thisFacility",appUser.getFacility());
        intent.putExtras(newArgs);
        return intent;
    }

    @Test
    public void testFacilityDisplayActivityDisplayed() {
        User appUser = mockUser();
        ActivityScenario<OrganizerDashboardActivity> scenario = ActivityScenario.launch(createFacilityDisplayActivityIntent(appUser));

        // Check if  details are displayed
        onView(withId(R.id.tv_facility_name)).check(matches(withText("MockFacility")));
        onView(withId(R.id.tv_facility_details)).check(matches(withText("Mock Facility Test Details")));
    }


    @Test
    public void testManageFacilityActivityDisplayed() {
        User appUser = mockUser();
        ActivityScenario<OrganizerDashboardActivity> scenario = ActivityScenario.launch(createFacilityDisplayActivityIntent(appUser));

        // Check if
        onView(withId(R.id.tv_edit_facility)).perform(click());
        onView(withId(R.id.activity_manage_facility)).check(matches(isDisplayed()));
    }

}
