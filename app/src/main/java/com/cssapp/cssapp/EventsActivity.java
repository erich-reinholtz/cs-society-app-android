package com.cssapp.cssapp;

import android.content.Intent;
import android.database.Cursor;
import androidx.annotation.NonNull;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class EventsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DatabaseHelper mDatabaseHelper;
    private String eventName;
    private  String eventId;
    private Integer currentUser;
    private ArrayList<Event> createdEventsList;
    private ArrayList<Event> markedEventsList;
    private ListView myEventsListView;
    private ListView markedEventsListView;
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if(MembersActivity.getSearch("theme",this) != null)
            if ( MembersActivity.getSearch("theme",this).equals("1")) {
                setTheme(R.style.DarkAppTheme);
            }
            else setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        currentUser = Integer.parseInt(CreateProfileActivity.getDefaults("String_key", EventsActivity.this));
        createdEventsList = new ArrayList<>();
        markedEventsList = new ArrayList<>();
        myEventsListView = findViewById(R.id.myEventsListView);
        markedEventsListView = findViewById(R.id.markedEventsListView);
        mDatabaseHelper = new DatabaseHelper(this);


        //Create list with user's events
        createMyEventsList();
        //Create list with user's marked events
        createMarkedEventsList();

        setUpToolbarMenu();
        setUpNavigationDrawerMenu();
    }


    /**
     Utilities Functions
     ---------------------------------------------------------------------------------------------------
     */
    //Method for Setting the Height of the ListView dynamically. Hack to fix the issue of not showing all the items of the ListView when placed inside a ScrollView.
    // ---------------------------------------------------------------------------------------------------
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    //Starts CreateEventActivity
    // ---------------------------------------------------------------------------------------------------
    public void goToCreateEve(View view) {
        Intent intent = new Intent(this, CreateEventActivity.class);
        startActivity(intent);
        finish();
    }


    //Create user's events list
    // ---------------------------------------------------------------------------------------------------
    private void createMyEventsList() {
        Cursor myEventsData = mDatabaseHelper.getEventsWithId(currentUser);
        while(myEventsData.moveToNext()) {
            //Get the data from the database
            eventName = myEventsData.getString(1);
            eventId = myEventsData.getString(0);
            //Add the events to the listView
            createdEventsList.add(0, new Event(eventName, "", "", eventId, 1));
        }

        EventsAdapter myEAdapter = new EventsAdapter(this, createdEventsList);
        myEventsListView.setAdapter(myEAdapter);
        setListViewHeightBasedOnChildren(myEventsListView);
        //When list item is clicked:
        eventsItemListener(myEventsListView);
    }


    //Create user's marked events list
    // ---------------------------------------------------------------------------------------------------
    private void createMarkedEventsList() {
        Cursor markedEventsData = mDatabaseHelper.getEventsOfUser(currentUser);
        while(markedEventsData.moveToNext()) {
            //Get the data from the database
            eventId = markedEventsData.getString(1);
            eventName = mDatabaseHelper.getEventName(eventId);
            //Add the events to the listView
            markedEventsList.add(0, new Event(eventName, "", "", eventId, 1));
        }

        EventsAdapter markedEAdapter = new EventsAdapter(this, markedEventsList);
        markedEventsListView.setAdapter(markedEAdapter);
        setListViewHeightBasedOnChildren(markedEventsListView);

        //When list item is clicked:
        eventsItemListener(markedEventsListView);
    }


    //When list item is clicked:
    private void eventsItemListener(ListView listview) {
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent,View view,int position,long id)
            {
                TextView textview = view.findViewById(R.id.Id_textView);
                //Get string from TextView
                String eId = textview.getText().toString();
                Intent intent = new Intent(EventsActivity.this, EventDisplayActivity.class);
                //Send string to EventDisplayActivity
                intent.putExtra("EXTRA_MESSAGE", eId);
                startActivity(intent);
            }
        });
    }

    public void onBackPressed() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void setUpToolbarMenu() {
        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle("Events");
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
                String id = CreateProfileActivity.getDefaults("String_key", EventsActivity.this);
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

    public ArrayList<Event> returnmarked(){
        return markedEventsList;
    }

    public ArrayList<Event> returncreated(){
        return createdEventsList;
    }
}
