package com.example.soroban;
import androidx.annotation.NonNull;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;

import android.content.Intent;
import android.widget.ListView;

import com.example.soroban.activity.InvitationActivity;
import com.example.soroban.activity.UserDashboardActivity;
import com.example.soroban.adapter.EventArrayAdapter;
import com.example.soroban.model.Event;
import com.example.soroban.model.EventList;
import com.example.soroban.model.User;

import java.util.Date;

@RunWith(AndroidJUnit4.class)

public class TestInvitationActivity {
    private User mockUser(){
        return new User("testId");
    }

    private Event mockEvent(User owner){
        return new Event(owner, owner.createFacility(), "mockEvent", new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()), 5);
    }

    private static Intent createInvitationIntent(User appUser) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setClassName("com.example.soroban", "com.example.soroban.activity.InvitationActivity");
        intent.putExtra("appUser", appUser);
        return intent;
    }

    @Test
    public void testInvitationsDisplayed() {
        User mockOwner = mockUser();
        Event mockEvent = mockEvent(mockOwner);
        mockOwner.addHostedEvent(mockEvent);

        User appUser = new User("testId2");
        mockEvent.addInvited(appUser);
        ActivityScenario<InvitationActivity> scenario = ActivityScenario.launch(createInvitationIntent(appUser));

        // Check if the invitations title is displayed
        onView(withId(R.id.tv_invitations)).check(matches(isDisplayed()));
        onView(withId(R.id.tv_invitations)).check(matches(withText("Invitations")));

        // Check if the invite is displayed
        EventList mockList = appUser.getInvitedEvents();
        scenario.onActivity( activity -> {
            EventArrayAdapter mockEventAdapter = new EventArrayAdapter(activity, mockList);
            ListView listView = activity.findViewById(R.id.notifications_view);
            listView.setAdapter(mockEventAdapter);
        });

        onView(withId(R.id.notifications_view)).check(matches(isDisplayed()));
        onData(is(instanceOf(Event.class))).inAdapterView(withId(R.id.notifications_view))
                .atPosition(0)
                .onChildView(withId(R.id.tv_event_name))
                .check(matches((withText(mockEvent.getEventName()))));
    }

    @Test
    public void testAcceptInvitations() {
        User mockOwner = mockUser();
        Event mockEvent = mockEvent(mockOwner);
        mockOwner.addHostedEvent(mockEvent);

        User appUser = new User("testId2");
        mockEvent.addInvited(appUser);
        ActivityScenario<InvitationActivity> scenario = ActivityScenario.launch(createInvitationIntent(appUser));


        onView(withId(R.id.notifications_view)).check(matches(isDisplayed()));
        onData(is(instanceOf(Event.class))).inAdapterView(withId(R.id.notifications_view))
                .atPosition(0)
                .onChildView(withId(R.id.tv_event_name))
                .perform(click());

        onView(withId(R.id.closeInvitation)).check(matches(isDisplayed()));
        onView(withId(android.R.id.button1)).perform(click());
    }

    @Test
    public void testDeclineInvitations() {
        User mockOwner = mockUser();
        Event mockEvent = mockEvent(mockOwner);
        mockOwner.addHostedEvent(mockEvent);

        User appUser = new User("testId2");
        mockEvent.addInvited(appUser);
        ActivityScenario<InvitationActivity> scenario = ActivityScenario.launch(createInvitationIntent(appUser));


        onView(withId(R.id.notifications_view)).check(matches(isDisplayed()));
        onData(is(instanceOf(Event.class))).inAdapterView(withId(R.id.notifications_view))
                .atPosition(0)
                .onChildView(withId(R.id.tv_event_name))
                .perform(click());

        onView(withId(R.id.closeInvitation)).check(matches(isDisplayed()));
        onView(withId(android.R.id.button2)).perform(click());
    }


    @Test
    public void testCloseInvitations() {
        User mockOwner = mockUser();
        Event mockEvent = mockEvent(mockOwner);
        mockOwner.addHostedEvent(mockEvent);

        User appUser = new User("testId2");
        mockEvent.addInvited(appUser);
        ActivityScenario<InvitationActivity> scenario = ActivityScenario.launch(createInvitationIntent(appUser));


        onView(withId(R.id.notifications_view)).check(matches(isDisplayed()));
        onData(is(instanceOf(Event.class))).inAdapterView(withId(R.id.notifications_view))
                .atPosition(0)
                .onChildView(withId(R.id.tv_event_name))
                .perform(click());

        onView(withId(R.id.closeInvitation)).check(matches(isDisplayed()));
        onView(withId(R.id.closeInvitation)).perform(click());
    }

}
