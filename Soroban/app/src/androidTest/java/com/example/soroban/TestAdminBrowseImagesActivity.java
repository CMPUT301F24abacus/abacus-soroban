package com.example.soroban;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;

import com.example.soroban.activity.AdminBrowseEventActivity;
import com.example.soroban.model.Event;
import com.example.soroban.model.User;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Date;

@RunWith(JUnit4.class)
public class TestAdminBrowseImagesActivity {

    private User mockUser(){
        return new User("testId");
    }

    private Event mockEvent(User owner){
        return new Event(owner, owner.createFacility(), "mockEvent", new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()), 5);
    }

    private static Intent createAdminBrowseImagesIntent(User appUser) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setClassName("com.example.soroban", "com.example.soroban.activity.AdminBrowseImagesActivity");
        intent.putExtra("appUser", appUser);
        return intent;
    }


    // currently, testing with recyclerview is impossible...

    @Test
    public void TestBrowseImagesDisplayed() {
        User appUser = mockUser();

        ActivityScenario<AdminBrowseEventActivity> scenario = ActivityScenario.launch(createAdminBrowseImagesIntent(appUser));

        onView(withId(R.id.browse_image_text)).check(matches(isDisplayed()));

    }

}
