package com.cssapp.cssapp;

import androidx.test.espresso.action.ViewActions;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.StringContains.containsString;

public class CreateProfileActivityTest {

    @Rule
    public ActivityTestRule<CreateProfileActivity> evActivity = new ActivityTestRule<>(CreateProfileActivity.class);


    @Test
    public void testTextBoxes() {
        onView(withId(R.id.FirstName_editText)).perform(typeText("John"));
        onView(withId(R.id.FirstName_editText)).check(matches(withText("John")));
        onView(withId(R.id.LastName_editText)).perform(ViewActions.scrollTo()).perform(typeText("Smith"));
        onView(withId(R.id.LastName_editText)).check(matches(withText("Smith")));
    }

    @Test
    public void testAddPhoto(){
        onView(withId(R.id.ChoosePicture_button)).perform(click());
        onView(withText("Select Action")).check(matches(notNullValue()));
    }

    @Test
    public void testSelectYear(){
        onView(withId(R.id.ChooseYear_spinner)).perform(click());
        onView(withText("Year 3")).perform(click());
        onView(withId(R.id.ChooseYear_spinner)).check(matches(withSpinnerText("Year 3")));

    }

    @Test
    public void testCheckboxes(){
        onView(withId(R.id.Sports_checkBox)).perform(click());
        onView(withId(R.id.Coding_checkBox)).perform(click());

    }
}