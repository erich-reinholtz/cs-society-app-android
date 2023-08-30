package com.cssapp.cssapp;

import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.IsNull.notNullValue;

public class LoginActivityTest {
    @Rule
    public ActivityTestRule<LoginActivity> evActivity = new ActivityTestRule<>(LoginActivity.class);

    @Test
    public void checkSignInButton(){

        onView(withId(R.id.sign_in_button)).perform(click());
        //onView(withText("Choose an account")).check(matches(notNullValue()));


    }

}