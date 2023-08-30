package com.cssapp.cssapp;


import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.widget.CalendarView;

import java.text.SimpleDateFormat;
import java.util.HashMap;

public class CalendarActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private String [] tagsEvent;
    private int i = 0;
    private DatabaseHelper mDatabaseHelper;
    private String permission;
    private Integer UserId;
    private CalendarView mcalendarview;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        //Set theme
        if(MembersActivity.getSearch("theme",this) != null)
            if ( MembersActivity.getSearch("theme",this).equals("1")) {
                setTheme(R.style.DarkAppTheme);
            }
            else setTheme(R.style.AppTheme);

        mDatabaseHelper = new DatabaseHelper(this);
        UserId = Integer.parseInt(CreateProfileActivity.getDefaults("String_key", CalendarActivity.this));
        mcalendarview =findViewById(R.id.calendarView);

        //Get permission
        Cursor data = mDatabaseHelper.getDataById(UserId);
        while(data.moveToNext()) {
            permission = data.getString(5);
        }

        displayCurrentDate();
        setCalendarListener();
        setUpToolbarMenu();
        setUpNavigationDrawerMenu();
    }


    private void setCalendarListener(){
        mcalendarview.setOnDateChangeListener(new CalendarView.OnDateChangeListener(){
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                String date = dayOfMonth + "/" + (month+1) + "/" + year;

                EventViewFragment eventViewFragment = (EventViewFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
                assert eventViewFragment != null;
                eventViewFragment.setDate(date);
                appendEvents(date, eventViewFragment);
                eventViewFragment.listclicklistener();
            }
        });
    }


    private void displayCurrentDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy");
        String curDate = sdf.format(mcalendarview.getDate());
        EventViewFragment eventViewFragment = (EventViewFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
        assert eventViewFragment != null;
        eventViewFragment.setDate(curDate);
        appendEvents(curDate, eventViewFragment);
        eventViewFragment.listclicklistener();
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void appendEvents(String event_date, EventViewFragment eventViewFragment) {
        HashMap<Integer, String> map = new HashMap<>();
        Cursor data = mDatabaseHelper.getEventsWithdate(event_date);
        while(data.moveToNext()){
            int result = data.getInt(0);
            String result1 = data.getString(1);

            tagsEvent = new String[8];
            Cursor eventTags = mDatabaseHelper.getEventTags(result);
            i = 0;
            while(eventTags.moveToNext()) {
                tagsEvent[i] = eventTags.getString(1);
                i++;
            }

            Boolean check = false;

            for(int j = 0; j < 8; j++)
                if(tagsEvent[j] != null)
                    //CASE: The event has the CSS planning tag
                    if(tagsEvent[j].contains("CSS planning"))
                        check = true;

            if(check) {
                //CASE: User has permission to see the event
                if(Integer.parseInt(permission) > 1)
                        map.put(result, result1);
            }

            else{
                map.put(result, result1);
            }
       }
        eventViewFragment.createAdapter(map);
    }


    private void setUpToolbarMenu() {
        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle("Calendar");
    }


    private void setUpNavigationDrawerMenu() {
        NavigationView navigationView = findViewById(R.id.navigationView);
        mDrawerLayout = findViewById(R.id.drawerLayout);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_close);
        mDrawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        mDrawerLayout.closeDrawer(GravityCompat.START);

        switch (menuItem.getItemId()) {

            case R.id.item_home:
                Intent homeIntent = new Intent(getApplicationContext(), HomeActivity.class);
                homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);
                finish();
                break;

            case R.id.item_profile:
                Intent profileIntent = new Intent(getApplicationContext(), ProfileActivity.class);
                String id = CreateProfileActivity.getDefaults("String_key", CalendarActivity.this);
                profileIntent.putExtra("EXTRA_MESSAGE", id);
                profileIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(profileIntent);
                finish();
                break;

            case R.id.item_events:
                Intent eventsIntent = new Intent(getApplicationContext(), EventsActivity.class);
                eventsIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(eventsIntent);
                finish();
                break;

            case R.id.item_calendar:
                Intent calendarIntent = new Intent(getApplicationContext(), CalendarActivity.class);
                calendarIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(calendarIntent);
                finish();
                break;

            case R.id.item_wiki:
                Intent wikiIntent = new Intent(getApplicationContext(), WikiActivity.class);
                wikiIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(wikiIntent);
                finish();
                break;

            case R.id.item_members:
                Intent membersIntent = new Intent(getApplicationContext(), MembersActivity.class);
                membersIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(membersIntent);
                finish();
                break;

            case R.id.item_settings:
                Intent settingsIntent = new Intent(getApplicationContext(), SettingsActivity.class);
                settingsIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(settingsIntent);
                finish();
                break;
        }
        return false;
    }


    public void onBackPressed() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
