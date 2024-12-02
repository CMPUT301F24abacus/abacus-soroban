package com.example.soroban;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Intent;
import android.os.Bundle;

import androidx.test.core.app.ActivityScenario;

import com.example.soroban.activity.AdminViewFacilityActivity;
import com.example.soroban.activity.OrganizerDashboardActivity;
import com.example.soroban.model.User;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class TestAdminViewFacilityActivity {

    private User mockUser(){ return new User("testId"); }

    private static Intent createAdminViewFacilityActivityIntent(User appUser) {
        appUser.createFacility();
        appUser.getFacility().setName("MockFacility");
        appUser.getFacility().setDetails("Mock Facility Test Details");
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setClassName("com.example.soroban", "com.example.soroban.activity.AdminViewFacilityActivity");
        Bundle newArgs = new Bundle();
        newArgs.putSerializable("appUser", appUser);
        newArgs.putSerializable("selectedFacility", appUser.getFacility());
        intent.putExtras(newArgs);
        return intent;
    }

    @Test
    public void testAdminViewFacilityDisplayed() {
        User appUser = mockUser();
        ActivityScenario<AdminViewFacilityActivity> scenario = ActivityScenario.launch(createAdminViewFacilityActivityIntent(appUser));

        // Check if details are displayed
        onView(withId(R.id.tvad_facility_name)).check(matches(withText("MockFacility")));
        onView(withId(R.id.tvad_facility_details)).check(matches(withText("Mock Facility Test Details")));
        onView(withId(R.id.tvad_facility_owner)).check(matches(withText("ID: testId")));
    }



}
