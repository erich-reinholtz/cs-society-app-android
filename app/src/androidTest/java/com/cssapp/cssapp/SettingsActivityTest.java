package com.cssapp.cssapp;

import android.app.Activity;
import android.app.Instrumentation;

import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static junit.framework.TestCase.assertNotNull;

public class SettingsActivityTest {
    @Rule
    public ActivityTestRule<SettingsActivity> evActivity = new ActivityTestRule<>(SettingsActivity.class);

    Instrumentation.ActivityMonitor monitor = getInstrumentation().addMonitor(LoginActivity.class.getName(),null,false);

    @Test
    public void testLogout(){

        onView(withId(R.id.logOutBtn)).perform(click());
        Activity lg = getInstrumentation().waitForMonitorWithTimeout(monitor, 5000);
        assertNotNull(lg);

    }

}