package com.cssapp.cssapp;

import androidx.test.InstrumentationRegistry;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.PickerActions;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;
import android.widget.DatePicker;
import android.widget.TimePicker;

import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
//import static	android.support.test.espresso.contrib.PickerActions;

import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.IsNull.notNullValue;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class CreateEventActivityTest {

    @Rule
    public ActivityTestRule<CreateEventActivity> activityRule = new ActivityTestRule<>(CreateEventActivity.class);

    @Test
    public void testTextBoxes() {
        onView(withId(R.id.Name_editText)).perform(typeText("Hackathon"));
        onView(withId(R.id.Name_editText)).check(matches(withText("Hackathon")));
        onView(withId(R.id.Location_editText)).perform(ViewActions.scrollTo()).perform(typeText("MVB"));
        onView(withId(R.id.Location_editText)).check(matches(withText("MVB")));
        onView(withId(R.id.Description_editText)).perform(ViewActions.scrollTo()).perform(typeText("Hackathon in MVB"));
        onView(withId(R.id.Description_editText)).check(matches(withText("Hackathon in MVB")));
    }

    @Test
    public void testAddPhoto(){
        onView(withId(R.id.add_event_photo)).perform(click());
        onView(withText("Select Action")).check(matches(notNullValue()));
    }

    @Test
    public void testDateButton(){
        onView((withId(R.id.Time_button))).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(2019,10,22));
        onView(withText("OK")).inRoot(isDialog()).check(matches(isDisplayed())).perform(click());
        onView(withId(R.id.Date_editText)).check(matches(withText("22/10/2019")));

    }

    @Test
    public void testTimeButton() {
        onView(withId(R.id.Date_button)).perform(click());
        onView(withClassName(Matchers.equalTo(TimePicker.class.getName()))).perform(PickerActions.setTime(13,33));
        onView(withText("OK")).inRoot(isDialog()).check(matches(isDisplayed())).perform(click());
        onView(withId(R.id.Time_editText)).check(matches(withText("13:33:00")));
    }

    @Test
    public void testDurationButton() {
        onView(withId(R.id.End_button)).perform(click());
        onView(withClassName(Matchers.equalTo(TimePicker.class.getName()))).perform(PickerActions.setTime(15,33));
        onView(withText("OK")).inRoot(isDialog()).check(matches(isDisplayed())).perform(click());
        onView(withId(R.id.event_duration)).check(matches(withText("15:33:00")));
    }

    @Test
    public void testCheckboxes(){
        onView(withId(R.id.Sports_checkBox)).perform(click());
        onView(withId(R.id.Coding_checkBox)).perform(click());

    }

}

