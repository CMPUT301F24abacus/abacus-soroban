package com.example.soroban;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Intent;
import android.os.Bundle;

import androidx.test.core.app.ActivityScenario;

import com.example.soroban.activity.UserEventActivity;
import com.example.soroban.model.Event;
import com.example.soroban.model.Facility;
import com.example.soroban.model.User;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Date;

@RunWith(JUnit4.class)
public class TestAdminViewProfileActivity {

    private User mockUser(){
        return new User("testId");
    }

    private Event mockEvent(User owner){
        return new Event(owner, owner.createFacility(), "mockEvent", new Date(System.currentTimeMillis() + 86400000), new Date(System.currentTimeMillis() + 86400000), 5);
    }

    private Facility mockFacility(User owner){
        return new Facility(owner);
    }

    private static Intent createAdminViewProfileActivityIntent(User appUser, User selectedUser) {
        selectedUser.setFirstName("Touma");
        selectedUser.setLastName("Kamijou");
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setClassName("com.example.soroban", "com.example.soroban.activity.AdminViewProfileActivity");
        Bundle newArgs = new Bundle();
        newArgs.putSerializable("selectedUser", selectedUser);
        newArgs.putSerializable("appUser", appUser);
        intent.putExtras(newArgs);
        return intent;
    }

    @Test
    public void testAdminProfileViewDisplayed() {
        User appUser = mockUser();
        User mockUser = new User("Read A Certain Magical Index");
        ActivityScenario<UserEventActivity> scenario = ActivityScenario.launch(createAdminViewProfileActivityIntent(appUser, mockUser));

        // Check if the delete profile button is displayed
        onView(withId(R.id.delete_profile_btn)).check(matches(isDisplayed()));

        // Check if details are correctly displayed
        onView(withId(R.id.user_FirstNameField)).check(matches(withText(mockUser.getFirstName())));
        onView(withId(R.id.user_LastNameField)).check(matches(withText(mockUser.getLastName())));
    }
}
