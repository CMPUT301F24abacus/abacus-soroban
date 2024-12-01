package com.example.soroban;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;

import android.app.AlertDialog;
import android.app.Instrumentation;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.filters.LargeTest;
import com.example.soroban.activity.EventEntrantsListActivity;
import com.example.soroban.activity.EventUsersInvitedActivity;
import com.example.soroban.activity.OrganizerEventViewDetailsActivity;
import com.example.soroban.adapter.EventArrayAdapter;
import com.example.soroban.adapter.UserArrayAdapter;
import com.example.soroban.model.Event;
import com.example.soroban.model.EventList;
import com.example.soroban.model.User;
import com.example.soroban.model.UserList;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Date;

@RunWith(JUnit4.class)
public class TestEventUsersInvitedActivity {
    private User mockUser(){
        return new User("testId");
    }

    private Event mockEvent(User owner){
        return new Event(owner, owner.createFacility(), "mockEvent", new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()), 5);
    }

    private static Intent createEventUsersInvitedActivityIntent(User appUser, Event mockEvent) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setClassName("com.example.soroban", "com.example.soroban.activity.EventUsersInvitedActivity");
        intent.putExtra("selectedEvent", mockEvent);
        intent.putExtra("appUser", appUser);
        return intent;
    }

    @Test
    public void testConfirmedDisplayed() {
        User appUser = mockUser();
        Event mockEvent = mockEvent(appUser);
        appUser.addHostedEvent(mockEvent);

        User mockEntrant = new User("mockEntrantId");
        mockEntrant.setFirstName("Spock");
        mockEvent.addInvited(mockEntrant);
        ActivityScenario<EventUsersInvitedActivity> scenario = ActivityScenario.launch(createEventUsersInvitedActivityIntent(appUser, mockEvent));

        UserList mockList = mockEvent.getInvitedEntrants();
        scenario.onActivity( activity -> {
            UserArrayAdapter mockUserAdapter = new UserArrayAdapter(activity, mockList);
            ListView listView = activity.findViewById(R.id.usersInvitedView);
            listView.setAdapter(mockUserAdapter);
        });

        onView(withId(R.id.usersInvitedView)).check(matches(isDisplayed()));
        onData(is(instanceOf(User.class))).inAdapterView(withId(R.id.usersInvitedView))
                .atPosition(0)
                .onChildView(withId(R.id.userUsername))
                .check(matches((withText(mockEntrant.getFirstName()))));

    }

    @Test
    public void testSendMessage() {
        User appUser = mockUser();
        Event mockEvent = mockEvent(appUser);
        appUser.addHostedEvent(mockEvent);

        User mockEntrant = new User("mockEntrantId");
        mockEntrant.setFirstName("Spock");
        mockEvent.addInvited(mockEntrant);
        ActivityScenario<EventUsersInvitedActivity> scenario = ActivityScenario.launch(createEventUsersInvitedActivityIntent(appUser, mockEvent));

        onView(withId(R.id.buttonSendMessageToUsers)).check(matches(isDisplayed()));
        onView(withId(R.id.buttonSendMessageToUsers)).perform(click());

        onView(withId(R.id.organizerMessageEdit)).perform(ViewActions.typeText("MockEvent Message"));
        onView(withId(R.id.organizerMessageEdit)).check(matches(isDisplayed()));

        onView(withId(android.R.id.button1)).perform(click());
    }

    @Test
    public void testCancelSendMessage() {
        User appUser = mockUser();
        Event mockEvent = mockEvent(appUser);
        appUser.addHostedEvent(mockEvent);

        User mockEntrant = new User("mockEntrantId");
        mockEntrant.setFirstName("Spock");
        mockEvent.addInvited(mockEntrant);
        ActivityScenario<EventUsersInvitedActivity> scenario = ActivityScenario.launch(createEventUsersInvitedActivityIntent(appUser, mockEvent));

        onView(withId(R.id.buttonSendMessageToUsers)).check(matches(isDisplayed()));
        onView(withId(R.id.buttonSendMessageToUsers)).perform(click());
        onView(withId(R.id.organizerMessageEdit)).check(matches(isDisplayed()));
        onView(withId(R.id.organizerMessageEdit)).check(matches(isDisplayed()));

        onView(withId(android.R.id.button2)).perform(click());
    }

    @Test
    public void testWithDrawInvites() throws InterruptedException {
        User appUser = mockUser();
        Event mockEvent = mockEvent(appUser);
        appUser.addHostedEvent(mockEvent);

        User mockEntrant = new User("mockEntrantId");
        mockEntrant.setFirstName("Spock");
        mockEvent.addInvited(mockEntrant);

        User mockEntrant2 = new User("mockEntrantId2");
        mockEntrant.setFirstName("Kirk");
        mockEvent.addToWaitingEntrants(mockEntrant2);
        ActivityScenario<EventUsersInvitedActivity> scenario = ActivityScenario.launch(createEventUsersInvitedActivityIntent(appUser, mockEvent));

        onView(withId(R.id.buttonWithdrawUsersInvite)).check(matches(isDisplayed()));

        UserList mockList = mockEvent.getInvitedEntrants();
        scenario.onActivity( activity -> {
            UserArrayAdapter mockUserAdapter = new UserArrayAdapter(activity, mockList);
            ListView listView = activity.findViewById(R.id.usersInvitedView);
            listView.setAdapter(mockUserAdapter);
        });

        onView(withId(R.id.buttonWithdrawUsersInvite)).perform(click());

        onView(withId(R.id.usersInvitedView)).check(matches(isDisplayed()));
        onData(is(instanceOf(User.class))).inAdapterView(withId(R.id.usersInvitedView))
                .atPosition(0)
                .onChildView(withId(R.id.userUsername))
                .check(matches(not(withText(mockEntrant.getFirstName()))));

        onView(withId(R.id.usersInvitedView)).check(matches(isDisplayed()));
        onData(is(instanceOf(User.class))).inAdapterView(withId(R.id.usersInvitedView))
                .atPosition(0)
                .onChildView(withId(R.id.userUsername))
                .check(matches((withText(mockEntrant2.getFirstName()))));

    }
}
