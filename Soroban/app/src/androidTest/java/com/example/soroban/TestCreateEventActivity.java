package com.example.soroban;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
//import static androidx.test.espresso.intent.Intents.intended;
//import static androidx.test.espresso.intent.Intents.intending;
//import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
//import static androidx.test.espresso.intent.matcher.IntentMatchers.hasData;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.not;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.PickerActions;
//import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.soroban.activity.CreateEventActivity;
import com.example.soroban.activity.OrganizerDashboardActivity;
import com.example.soroban.fragment.DatePickerFragment;
import com.example.soroban.model.Event;
import com.example.soroban.model.Facility;
import com.example.soroban.model.User;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class TestCreateEventActivity {
    private User mockUser(){
        return new User("testId");
    }

    private Event mockEvent(User owner){
        return new Event(owner, owner.createFacility(), "mockEvent", new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()), 5);
    }

    private static Intent createCreateEventIntent(User appUser) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setClassName("com.example.soroban", "com.example.soroban.activity.CreateEventActivity");
        intent.putExtra("appUser", appUser);
        return intent;
    }

    @Test
    public void testEventCreation() {
        User appUser = mockUser();
        appUser.createFacility();
        ActivityScenario<OrganizerDashboardActivity> scenario = ActivityScenario.launch(createCreateEventIntent(appUser));

        // Check if the activity title "Create Event" is displayed
        onView(withId(R.id.eventNameEditText)).perform(ViewActions.typeText("MockEventName"));

        onView(withId(R.id.eventDateSelectButton)).perform(click());
        onView(withClassName(endsWith("DatePicker"))).perform(PickerActions.setDate(2024, 12, 31));
        onView(withText("OK")).perform(click());

        onView(withId(R.id.drawDateSelectButton)).perform(click());
        onView(withClassName(endsWith("DatePicker"))).perform(PickerActions.setDate(2024, 12, 1));
        onView(withText("OK")).perform(click());

        onView(withId(R.id.sampleSizeEditText)).perform(ViewActions.typeText("6"));

        /*
        Resources resources = InstrumentationRegistry.getInstrumentation().getTargetContext().getResources();
        Uri uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                resources.getResourcePackageName(R.mipmap.ic_launcher) + '/' +
                resources.getResourceTypeName(R.mipmap.ic_launcher) + '/' +
                resources.getResourceEntryName(R.mipmap.ic_launcher));

        Intent resultData = new Intent();
        resultData.setData(uri);
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData);

        Matcher<Intent> expectedIntent = allOf(hasAction(Intent.ACTION_GET_CONTENT));
        Intents.init();

        intending(expectedIntent).respondWith(result);


        //Click select button
        onView(withId(R.id.buttonUploadPoster)).perform(click());
        intended(expectedIntent);
        Intents.release();

        onView(withId(R.id.saveEventButton)).perform(click());
        onView(withId(R.id.eventsListView)).check(matches(isDisplayed()));
        onData(is(instanceOf(Event.class))).inAdapterView(withId(R.id.eventsListView))
                .atPosition(0)
                .onChildView(withId(R.id.tv_event_name))
                .check(matches((withText("MockEventName"))));

        onData(is(instanceOf(Event.class))).inAdapterView(withId(R.id.eventsListView))
                .atPosition(0)
                .perform(click());        */

    }
}
