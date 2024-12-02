package com.example.soroban;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.soroban.activity.AdminBrowseEventActivity;
import com.example.soroban.activity.AdminBrowseFacilityActivity;
import com.example.soroban.model.Event;
import com.example.soroban.model.User;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Date;

@RunWith(AndroidJUnit4.class)
public class TestAdminBrowseFacilityActivity {

    private User mockUser(){
        return new User("testId");
    }

    private Event mockEvent(User owner){
        return new Event(owner, owner.createFacility(), "mockEvent", new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()), 5);
    }

    private static Intent createAdminBrowseFacilityIntent(User appUser) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setClassName("com.example.soroban", "com.example.soroban.activity.AdminBrowseFacilityActivity");
        intent.putExtra("appUser", appUser);
        return intent;
    }


    // currently, testing with recyclerview is impossible...

    @Test
    public void TestBrowseFacilityDisplayed() {
        User appUser = mockUser();

        ActivityScenario<AdminBrowseFacilityActivity> scenario = ActivityScenario.launch(createAdminBrowseFacilityIntent(appUser));

        onView(withId(R.id.browse_facility_text)).check(matches(isDisplayed()));

    }

}
