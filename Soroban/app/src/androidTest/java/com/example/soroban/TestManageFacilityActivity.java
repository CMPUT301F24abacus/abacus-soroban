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
import com.example.soroban.model.Event;
import com.example.soroban.model.Facility;
import com.example.soroban.model.User;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Date;

@RunWith(JUnit4.class)
public class TestManageFacilityActivity {

    private User mockUser(){
        return new User("testId");
    }

    private static Intent createManageFacilityActivityIntent(User appUser) {
        appUser.createFacility();
        appUser.getFacility().setName("MockFacility");
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setClassName("com.example.soroban", "com.example.soroban.activity.ManageFacilityActivity");
        intent.putExtra("appUser", appUser);
        return intent;
    }

    // Facility.getName() invokes null object for some reason...
    @Test
    public void testManageFacilityActivityDisplayed() {
        User appUser = mockUser();

        ActivityScenario<OrganizerDashboardActivity> scenario = ActivityScenario.launch(createManageFacilityActivityIntent(appUser));
        // Check if the button is displayed
        onView(withId(R.id.buttonCreateFacility)).check(matches(isDisplayed()));
        onView(withId(R.id.buttonCreateFacility)).check(matches(withText("Save Changes")));
    }

    // Doesn't work for me, problem with firebase
    @Test
    public void testFacilityManage() {
        User appUser = mockUser();
        appUser.createFacility();
        ActivityScenario<OrganizerDashboardActivity> scenario = ActivityScenario.launch(createManageFacilityActivityIntent(appUser));
        // Check if the facility is changed
        onView(withId(R.id.editTextFacilityName)).perform(ViewActions.typeText("MockFacilityName"));

        onView(withId(R.id.editTextFacilityDetails)).perform(ViewActions.typeText("Read A Certain Magical Index"));

        onView(withId(R.id.buttonCreateFacility)).perform(click());
        onView(withId(R.id.facilityNameTextView)).check(matches(withText("MockFacilityName")));
    }


}
