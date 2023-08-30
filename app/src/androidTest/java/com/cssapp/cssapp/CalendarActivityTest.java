package com.cssapp.cssapp;

//import android.support.test.rule.ActivityTestRule;

import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.TimePicker;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import androidx.test.espresso.contrib.PickerActions;
import androidx.test.rule.ActivityTestRule;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.*;

public class CalendarActivityTest {

    @Rule
    public ActivityTestRule<CalendarActivity> calActivity = new ActivityTestRule<>(CalendarActivity.class);

    private CalendarActivity cact = null;

    @Before
    public void  setUp() throws Exception{

        cact = calActivity.getActivity();
    }


    @Test
    public void test1(){
        //onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(23, 10, 2019));
    }

    @After
    public void tearDown() throws Exception {
        cact = null;
    }

}