package com.example.soroban;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.soroban.activity.InvitationActivity;
import com.example.soroban.adapter.NotificationAdapter;
import com.example.soroban.model.Notification;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RunWith(JUnit4.class)
public class TestNotificationActivity {

    private Notification mockNotification() { return new Notification("mockNotification", new Date(System.currentTimeMillis()), "mockEvent", Notification.NotificationType.BAD_NEWS); }

    @Rule
    public ActivityScenarioRule<InvitationActivity> activityScenarioRule =
    new ActivityScenarioRule<>(InvitationActivity.class);

    @Test
    public void testNotificationTitleIsDisplayed() {
        // Check if the notification title "Notification" is displayed
        onView(withId(R.id.tv_invitations)).check(matches(isDisplayed()));
    }

    // This test and below run into animation(?) errors, redo later...
    @Test
    public void testNotificationListDisplayed() {
        // Check if list view displays notifications
        Notification mockNotification = mockNotification();
        onView(withId(R.id.tv_invitations)).check(matches(isDisplayed()));

        List<Notification> mockList = new ArrayList<>();
        mockList.add(mockNotification);

        activityScenarioRule.getScenario().onActivity(activity -> {
            NotificationAdapter mockAdapter = new NotificationAdapter(mockList);
            RecyclerView mockView = activity.findViewById(R.id.notifications_recycler_view);
            mockView.setLayoutManager(new LinearLayoutManager(activity));
            mockView.setAdapter(mockAdapter);
        });

        onView(withId(R.id.notifications_recycler_view)).check(matches(isDisplayed()));
        onData(is(instanceOf(Notification.class))).inAdapterView(withId(R.id.notifications_recycler_view))
                .atPosition(0)
                .onChildView(withId(R.id.notification_title))
                .check(matches(withText(mockNotification.getTitle())));
    }

    @Test
    public void testMuteAllBtn() {
        Notification mockNotification = mockNotification();
        onView(withId(R.id.tv_invitations)).check(matches(isDisplayed()));

        List<Notification> mockList = new ArrayList<>();
        mockList.add(mockNotification);

        activityScenarioRule.getScenario().onActivity(activity -> {
            NotificationAdapter mockAdapter = new NotificationAdapter(mockList);
            RecyclerView mockView = activity.findViewById(R.id.notifications_recycler_view);
            mockView.setLayoutManager(new LinearLayoutManager(activity));
            mockView.setAdapter(mockAdapter);

            onView(withId(R.id.btn_mute_all)).perform(click());
            for (Notification notification : mockList) {
                assertTrue("Notification not muted", notification.isMuted());
            }
        });
    }

    @Test
    public void testClearAllBtn() {
        Notification mockNotification = mockNotification();
        onView(withId(R.id.tv_invitations)).check(matches(isDisplayed()));

        List<Notification> mockList = new ArrayList<>();
        mockList.add(mockNotification);

        activityScenarioRule.getScenario().onActivity(activity -> {
            NotificationAdapter mockAdapter = new NotificationAdapter(mockList);
            RecyclerView mockView = activity.findViewById(R.id.notifications_recycler_view);
            mockView.setLayoutManager(new LinearLayoutManager(activity));
            mockView.setAdapter(mockAdapter);

            onView(withId(R.id.btn_clear_all)).perform(click());
            assertEquals("Notifications not cleared", 0, mockAdapter.getItemCount());
        });
    }

}
