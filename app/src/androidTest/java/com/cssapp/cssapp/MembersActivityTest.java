package com.cssapp.cssapp;

import android.app.Activity;
import android.app.Instrumentation;
import android.view.Gravity;
import android.widget.TextView;

import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerMatchers.isClosed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static junit.framework.TestCase.assertNotNull;
import static org.hamcrest.core.IsNull.notNullValue;

public class MembersActivityTest {

    @Rule
    public ActivityTestRule<MembersActivity> evActivity = new ActivityTestRule<>(MembersActivity.class);

    Instrumentation.ActivityMonitor monitor = getInstrumentation().addMonitor(ProfileActivity.class.getName(),null,false);

    @Test
    public void testMember(){
        onView(withId(R.id.drawerLayout))
                .check(matches(isClosed(Gravity.LEFT))) // Left Drawer should be closed.
                .perform(DrawerActions.open()); // Open Drawer
        onView(withId(R.id.navigationView))
                .perform(NavigationViewActions.navigateTo(R.id.item_profile));
        Activity ev = getInstrumentation().waitForMonitorWithTimeout(monitor, 5000);
        assertNotNull(ev);
        TextView tv = ev.findViewById(R.id.FirstName_textView);
        String name = tv.getText().toString();
        onView(withText(name)).check(matches(notNullValue()));
    }

}