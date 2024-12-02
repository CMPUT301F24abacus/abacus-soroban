package com.example.soroban;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.allOf;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.filters.LargeTest;
import com.example.soroban.activity.EventEntrantsListActivity;
import com.example.soroban.activity.OrganizerEventViewDetailsActivity;
import com.example.soroban.adapter.EventArrayAdapter;
import com.example.soroban.adapter.UserArrayAdapter;
import com.example.soroban.model.Event;
import com.example.soroban.model.EventList;
import com.example.soroban.model.User;
import com.example.soroban.model.UserList;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Date;

@RunWith(JUnit4.class)
@LargeTest
public class TestPreferencesActivity {
    private User mockUser(){
        return new User("testId");
    }

    private Event mockEvent(User owner){
        return new Event(owner, owner.createFacility(), "mockEvent", new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()), 5);
    }

    private static Intent createPreferencesActivityIntent(User appUser) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setClassName("com.example.soroban", "com.example.soroban.activity.PreferencesActivity");;
        intent.putExtra("appUser", appUser);
        return intent;
    }

    @Test
    public void testEnableNotificationsBtn() {
        User appUser = mockUser();
        ActivityScenario<EventEntrantsListActivity> scenario = ActivityScenario.launch(createPreferencesActivityIntent(appUser));
        // Click on the button
        onView(withId(R.id.notifications_switch)).perform(click());
    }

    /* Unfortunately, due to issues with granting/revoking permissions with the following method:
        @Rule
            public GrantPermissionRule mRuntimePermissionRule = GrantPermissionRule.grant(android.Manifest.permission.POST_NOTIFICATIONS);

        Reference: https://stackoverflow.com/questions/50403128/how-to-grant-permissions-to-android-instrumented-tests

       There is no obvious way to test the functionality of this activity.
       Test cases would be as follows:
            1) With notifications enabled, disable them via the settings screen
            2) With notifications disabled, enable them via the settings screen
            3) With notifications enabled, keep them enabled via the settings screen
            4) With notifications disabled, keep them disabled via the settings screen
     */


}
