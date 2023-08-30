package com.cssapp.cssapp;

import android.app.Activity;
import android.app.Instrumentation;
import android.view.Gravity;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.test.InstrumentationRegistry;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerMatchers.isClosed;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static junit.framework.TestCase.assertNotNull;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;

public class HomeActivityTest {
    @Rule
    public ActivityTestRule<HomeActivity> evActivity = new ActivityTestRule<>(HomeActivity.class);

    private HomeActivity home = null;

    Instrumentation.ActivityMonitor monitor = getInstrumentation().addMonitor(HomeActivity.class.getName(),null,false);
    Instrumentation.ActivityMonitor monitor1 = getInstrumentation().addMonitor(EventsActivity.class.getName(),null,false);
    Instrumentation.ActivityMonitor monitor2 = getInstrumentation().addMonitor(CalendarActivity.class.getName(),null,false);
    Instrumentation.ActivityMonitor monitor3 = getInstrumentation().addMonitor(ProfileActivity.class.getName(),null,false);
    Instrumentation.ActivityMonitor monitor4 = getInstrumentation().addMonitor(WikiActivity.class.getName(),null,false);
    Instrumentation.ActivityMonitor monitor5 = getInstrumentation().addMonitor(MembersActivity.class.getName(),null,false);
    Instrumentation.ActivityMonitor monitor6 = getInstrumentation().addMonitor(SettingsActivity.class.getName(),null,false);
    Instrumentation.ActivityMonitor monitor7 = getInstrumentation().addMonitor(EventDisplayActivity.class.getName(), null, false);

    @Before
    public void setUp() throws Exception{

        home = evActivity.getActivity();
    }

    @Test
    public void checkNavEvent() {
        // Open Drawer to click on navigation.
        onView(withId(R.id.drawerLayout))
                .check(matches(isClosed(Gravity.LEFT))) // Left Drawer should be closed.
                .perform(DrawerActions.open()); // Open Drawer
        onView(withId(R.id.navigationView))
                .perform(NavigationViewActions.navigateTo(R.id.item_events));
        Activity ev = getInstrumentation().waitForMonitorWithTimeout(monitor1, 5000);
        assertNotNull(ev);
    }

    @Test
    public void checkNavHome() {
        // Open Drawer to click on navigation.
        onView(withId(R.id.drawerLayout))
                .check(matches(isClosed(Gravity.LEFT))) // Left Drawer should be closed.
                .perform(DrawerActions.open()); // Open Drawer
        onView(withId(R.id.navigationView))
                .perform(NavigationViewActions.navigateTo(R.id.item_home));
        Activity ev = getInstrumentation().waitForMonitorWithTimeout(monitor, 5000);
        assertNotNull(ev);
    }

    @Test
    public void checkNavCal() {
        // Open Drawer to click on navigation.
        onView(withId(R.id.drawerLayout))
                .check(matches(isClosed(Gravity.LEFT))) // Left Drawer should be closed.
                .perform(DrawerActions.open()); // Open Drawer
        onView(withId(R.id.navigationView))
                .perform(NavigationViewActions.navigateTo(R.id.item_calendar));
        Activity ev = getInstrumentation().waitForMonitorWithTimeout(monitor2, 5000);
        assertNotNull(ev);
    }

    @Test
    public void checkNavProf() {
        // Open Drawer to click on navigation.
        onView(withId(R.id.drawerLayout))
                .check(matches(isClosed(Gravity.LEFT))) // Left Drawer should be closed.
                .perform(DrawerActions.open()); // Open Drawer
        onView(withId(R.id.navigationView))
                .perform(NavigationViewActions.navigateTo(R.id.item_profile));
        Activity ev = getInstrumentation().waitForMonitorWithTimeout(monitor3, 5000);
        assertNotNull(ev);
    }

    @Test
    public void checkNavWiki(){
        onView(withId(R.id.drawerLayout))
                .check(matches(isClosed(Gravity.LEFT))) // Left Drawer should be closed.
                .perform(DrawerActions.open()); // Open Drawer
        onView(withId(R.id.navigationView))
                .perform(NavigationViewActions.navigateTo(R.id.item_wiki));
        Activity ev = getInstrumentation().waitForMonitorWithTimeout(monitor4, 5000);
        assertNotNull(ev);
    }

    @Test
    public void checkNavMemb(){
        onView(withId(R.id.drawerLayout))
                .check(matches(isClosed(Gravity.LEFT))) // Left Drawer should be closed.
                .perform(DrawerActions.open()); // Open Drawer
        onView(withId(R.id.navigationView))
                .perform(NavigationViewActions.navigateTo(R.id.item_members));
        Activity ev = getInstrumentation().waitForMonitorWithTimeout(monitor5, 5000);
        assertNotNull(ev);
    }

    @Test
    public void checkNavSett(){
        onView(withId(R.id.drawerLayout))
                .check(matches(isClosed(Gravity.LEFT))) // Left Drawer should be closed.
                .perform(DrawerActions.open()); // Open Drawer
        onView(withId(R.id.navigationView))
                .perform(NavigationViewActions.navigateTo(R.id.item_settings));
        Activity ev = getInstrumentation().waitForMonitorWithTimeout(monitor6, 5000);
        assertNotNull(ev);
    }

    @Test
    public void testListViewDisplayed(){
        onView(withId(R.id.events_list)).check(matches(isDisplayed()));
    }

   /* @Test
    public void testListviewCorrect(){
        ArrayList<Event> events = home.returnlist();
        int count = events.size();
        for(int x = 0; x < count; count++){
            Event event = events.get(x);
            onData(anything()).inAdapterView(withId(R.id.events_list)).atPosition(x).equals(event);
        }

    }*/

    @Test
    public void testEventClick(){
        ListView lv = home.findViewById(R.id.events_list);
        ListAdapter lvadapter =  lv.getAdapter();
        Event event = (Event) lvadapter.getItem(0);
        String name = event.getName();
        onData(anything()).inAdapterView(withId(R.id.events_list)).atPosition(0).perform(click());
        onView(withId(R.id.Name_textView)).check(matches(withText(name)));
        Activity ev = getInstrumentation().waitForMonitorWithTimeout(monitor7, 5000);
        assertNotNull(ev);


    }

    @Test
    public void testEventDisplay(){
        ListView lv = home.findViewById(R.id.events_list);
        ListAdapter lvadapter =  lv.getAdapter();
        Event event = (Event) lvadapter.getItem(0);
        String name = event.getName();
        onData(anything()).inAdapterView(withId(R.id.events_list)).atPosition(0).perform(click());
        Activity ev = getInstrumentation().waitForMonitorWithTimeout(monitor7, 5000);
        assertNotNull(ev);
        TextView count = ev.findViewById(R.id.UsersNumber_textView);
        Integer countint = Integer.parseInt(count.getText().toString());
        Button marked = ev.findViewById(R.id.Mark_button);
        String markedText = marked.getText().toString();
        if(markedText == "MARK"){
            onView(withId(R.id.Mark_button)).perform(click());
            String countnew = Integer.toString(countint +1);
            onView(withId(R.id.Name_textView)).check(matches(withText(countnew)));
            onView(withId(R.id.Mark_button)).perform(click());
        }
        else if(markedText == "MARKED"){
            onView(withId(R.id.Mark_button)).perform(click());
            String countnew = Integer.toString(countint -1);
            onView(withId(R.id.Name_textView)).check(matches(withText(countnew)));
            onView(withId(R.id.Mark_button)).perform(click());
        }
    }

   // @Test
    /*public void testComment(){
        onData(anything()).inAdapterView(withId(R.id.events_list)).atPosition(0).perform(click());
        Activity ev = getInstrumentation().waitForMonitorWithTimeout(monitor7, 5000);
        assertNotNull(ev);
        onView(withId(R.id.Comment_editText)).perform(typeText("afafaffafafafafafaf")).perform(closeSoftKeyboard());
        onView(withId(R.id.Comment_button)).perform(click());
        onView(withText("afafaffafafafafafaf")).check(matches(notNullValue()));

    }*/
    @After
    public void tearDown() throws Exception{

        home = null;
    }

}