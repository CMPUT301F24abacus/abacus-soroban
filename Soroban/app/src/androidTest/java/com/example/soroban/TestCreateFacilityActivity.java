package com.example.soroban;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.endsWith;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.PickerActions;

import com.example.soroban.activity.OrganizerDashboardActivity;
import com.example.soroban.model.Event;
import com.example.soroban.model.Facility;
import com.example.soroban.model.User;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Date;

@RunWith(JUnit4.class)
public class TestCreateFacilityActivity {

    private User mockUser(){
        return new User("testId");
    }

    private Event mockEvent(User owner){
        return new Event(owner, owner.createFacility(), "mockEvent", new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()), 5);
    }

    private Facility mockFacility(User owner){
        return new Facility(owner);
    }

    private static Intent createFacilityActivityIntent(User appUser) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setClassName("com.example.soroban", "com.example.soroban.activity.CreateFacilityActivity");
        intent.putExtra("appUser", appUser);
        return intent;
    }

    @Test
    public void testCreateFacilityActivityDisplayed() {
        User appUser = mockUser();
        ActivityScenario<OrganizerDashboardActivity> scenario = ActivityScenario.launch(createFacilityActivityIntent(appUser));
        // Check if the button is displayed
        onView(withId(R.id.buttonCreateFacility)).check(matches(isDisplayed()));
    }

    // Doesn't work for me, problem with firebase
    @Test
    public void testFacilityCreation() {
        User appUser = mockUser();
        ActivityScenario<OrganizerDashboardActivity> scenario = ActivityScenario.launch(createFacilityActivityIntent(appUser));
        // Check if the facility is created
        onView(withId(R.id.editTextFacilityName)).perform(ViewActions.typeText("MockFacilityName"));

        onView(withId(R.id.editTextFacilityDetails)).perform(ViewActions.typeText("Read A Certain Magical Index"));

        onView(withId(R.id.buttonCreateFacility)).perform(click());
        onView(withId(R.id.facilityNameTextView)).check(matches(withText("MockFacilityName")));
    }


}
