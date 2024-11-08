package com.example.soroban;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.action.ViewActions;

import com.example.soroban.activity.OrganizerDashboardActivity;
import com.example.soroban.model.Facility;
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
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setClassName("com.example.soroban", "com.example.soroban.activity.FacilityDisplayActivity");
        intent.putExtra("appUser", appUser);
        return intent;
    }


    @Test
    public void testFacilityNameDisplayed() {
        User appUser = mockUser();

        ActivityScenario<OrganizerDashboardActivity> scenario = ActivityScenario.launch(createFacilityDisplayActivityIntent(appUser));
        // Check if the facility name is displayed
        onView(withId(R.id.tv_facility_name)).check(matches(withText("MockFacility")));
    }


    @Test
    public void testFacilityEditBtn() {
        User appUser = mockUser();

        ActivityScenario<OrganizerDashboardActivity> scenario = ActivityScenario.launch(createFacilityDisplayActivityIntent(appUser));
        // Check if the facility edit button works
        onView(withId(R.id.tv_edit_facility)).perform(click());
        onView(withId(R.id.buttonCreateFacility)).check(matches(withText("Save Changes")));
    }

}
