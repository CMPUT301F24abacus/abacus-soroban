package com.example.soroban;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;

import android.content.Intent;
import android.widget.ListView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.soroban.activity.AdminBrowseEventActivity;
//import com.example.soroban.activity.AdminBrowseEventActivity.BrowseEventsAdapter;
import com.example.soroban.adapter.UserArrayAdapter;
import com.example.soroban.model.Event;
import com.example.soroban.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Date;

@RunWith(AndroidJUnit4.class)
public class TestAdminBrowseEventActivity {
    private User mockUser(){
        return new User("testId");
    }

    private Event mockEvent(User owner){
        return new Event(owner, owner.createFacility(), "mockEvent", new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()), 5);
    }

    private static Intent createAdminBrowseEventIntent(User appUser) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setClassName("com.example.soroban", "com.example.soroban.activity.AdminBrowseEventActivity");
        intent.putExtra("appUser", appUser);
        return intent;
    }


    // currently, testing with recyclerview is impossible...

    @Test
    public void TestBrowseEventDisplayed() {
        User appUser = mockUser();
        // User mockEntrant = new User("mockEntrantId");
        // mockEntrant.setFirstName("NEO: The World Ends With You");
        // Event mockEvent = mockEvent(mockEntrant);
        // ArrayList<Event> mockList = new ArrayList<>();
        // mockList.add(mockEvent);


        ActivityScenario<AdminBrowseEventActivity> scenario = ActivityScenario.launch(createAdminBrowseEventIntent(appUser));

       // scenario.onActivity( activity -> {
            //BrowseEventsAdapter mockAdapter = new BrowseEventsAdapter(mockList);
            //RecyclerView recyclerView = activity.findViewById(R.id.event_admin_recycler);
            //recyclerView.setLayoutManager(new LinearLayoutManager(activity));
            //recyclerView.setAdapter(mockAdapter);
        //});

        onView(withId(R.id.browse_event_text)).check(matches(isDisplayed()));
        //onView(withId(R.id.event_admin_recycler))
                //.perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

    }

}
