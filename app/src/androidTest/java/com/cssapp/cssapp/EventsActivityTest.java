package com.cssapp.cssapp;

import android.app.Activity;
import android.app.Instrumentation;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static junit.framework.TestCase.assertNotNull;
import static org.hamcrest.core.IsNull.notNullValue;

public class EventsActivityTest {

    @Rule
    public ActivityTestRule<EventsActivity> evActivity = new ActivityTestRule<>(EventsActivity.class);

    EventsActivity eventAct = null;

    Instrumentation.ActivityMonitor monitor = getInstrumentation().addMonitor(CreateEventActivity.class.getName(),null,false);

    @Before
    public void setUp() throws Exception{

        eventAct = evActivity.getActivity();

    }

    @Test
    public void testCreateNewEvent(){

        assertNotNull(eventAct.findViewById(R.id.Save_button));
        onView(withId(R.id.Save_button)).perform(click());
        Activity createEvent =  getInstrumentation().waitForMonitorWithTimeout(monitor, 5000);
        assertNotNull(createEvent);

    }

    @Test
    public void testMarkedEvents(){
        ArrayList<Event> events = eventAct.returnmarked();
        for(Event event : events){
            String name = event.getName();
            onView(withText(name)).check(matches(notNullValue()));
        }
    }

    @Test
    public void testCreatedEvents(){
        ArrayList<Event> events = eventAct.returncreated();
        for(Event event : events){
            String name = event.getName();
            onView(withText(name)).check(matches(notNullValue()));
        }
    }

    @After
    public void tearDown() throws Exception{

        eventAct = null;

    }
}