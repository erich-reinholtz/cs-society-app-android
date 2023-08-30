package com.cssapp.cssapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, SearchView.OnQueryTextListener {
    private DatabaseHelper mDatabaseHelper = new DatabaseHelper(this);
    private String author;
    private Integer filter = 1;
    private Button btnFilter;
    private String currentUser;
    private String name;
    private String description;
    private String authorName;
    private String eventId;
    private String[] tagsEvent;
    private String[] tagsUser;
    private String permission = "-1";
    private ArrayList<Event> eventsList;
    private ListView eventsListView;
    private EventsAdapter eAdapter;
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private SearchView searchView;
    private int i;
    private String message;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if(MembersActivity.getSearch("theme",this) != null)
            if ( MembersActivity.getSearch("theme",this).equals("1")) {
                setTheme(R.style.DarkAppTheme);
            }
            else setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);



        eventsListView = findViewById(R.id.events_list);
        eventsList = new ArrayList<>();
        mDatabaseHelper = new DatabaseHelper(this);
        btnFilter =findViewById(R.id.filter_button);
        currentUser = CreateProfileActivity.getDefaults("String_key", HomeActivity.this);
        eAdapter = new EventsAdapter(this, eventsList);

        searchView = findViewById(R.id.searchHome);
        searchView.setOnQueryTextListener(this);

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    searchView.setIconified(true);
                }
            }
        });

        //Enable check
        checkFirstTime();
        //Enable chooseFilter function
        chooseFilter();

        //Get currentUser's permission
        Cursor per = mDatabaseHelper.getDataById(Integer.parseInt(currentUser));
        while(per.moveToNext()) {
            permission = per.getString(5);
        }

        //Get currentUser's tags
        Cursor tags = mDatabaseHelper.getTagMatchesById(Integer.parseInt(currentUser));
        i = 0;
        tagsUser = new String[6];
        while(tags.moveToNext()) {
            tagsUser[i] = tags.getString(1);
            i++;
        }


        displayEvents();

        //Set list adapter
        eventsListView.setAdapter(eAdapter);

        //When a list item is clicked:
        listItemListener();

        setUpToolbarMenu();
        setUpNavigationDrawerMenu();
    }


    /**
     Utilities Functions
     ---------------------------------------------------------------------------------------------------
     */
    //Choose filter function
    // ---------------------------------------------------------------------------------------------------
    private void chooseFilter(){
        //Initialize SharedPreferences
        SharedPreferences sp = getSharedPreferences("your_prefs", HomeActivity.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sp.edit();

        //When btnFilter is clicked:
        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.app.AlertDialog.Builder pictureDialog = new android.app.AlertDialog.Builder((Context) HomeActivity.this,R.style.AlertDialogCustom);
                pictureDialog.setTitle("Filter");
                String[] pictureDialogItems = {
                        "None",
                        "Tags",
                        "CSS"};
                pictureDialog.setItems(pictureDialogItems,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        editor.putInt("your_int_key", 1);
                                        editor.apply();
                                        finish();
                                        startActivity(getIntent());
                                        break;

                                    case 1:
                                        editor.putInt("your_int_key", 0);
                                        editor.apply();
                                        finish();
                                        startActivity(getIntent());
                                    break;
                                    case 2:
                                        editor.putInt("your_int_key", 2);
                                        editor.apply();
                                        finish();
                                        startActivity(getIntent());
                                        break;
                                }
                            }
                        });
                pictureDialog.show();
            }


        });

        //Get chooseFilter value
        filter = sp.getInt("your_int_key", 1);
    }


    //Filter function
    // ---------------------------------------------------------------------------------------------------
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void filter() {
        Boolean tagsCheck = false;
        Boolean planningCheck = false;
        Boolean cssCheck = false;
        for(int i = 0; i < 6; i++)
            if(tagsUser[i] != null)
                for(int j = 0; j < 8; j++)
                    if(tagsEvent[j] != null) {
                        //CASE: The event has an user's tag
                        if (Objects.equals(tagsUser[i], tagsEvent[j]))
                            tagsCheck = true;
                        //CASE: The event has the CSS planning tag
                        if(tagsEvent[j].contains("CSS planning"))
                            planningCheck = true;
                        //CASE: The event has the CSS tag
                        if(tagsEvent[j].equals("CSS"))
                            cssCheck = true;
                    }
        //CASE: Tags filter
        if(filter == 0) {
            //Same tags present for user and event
            if(tagsCheck)
                //Planning tag present
                if(planningCheck) {
                    //Check for permission
                    if(Integer.parseInt(permission) > 1)
                        eventsList.add(0, new Event(name, description, authorName, eventId, 0));
                }
                //No planning tag present
                else
                    eventsList.add(0, new Event(name, description, authorName, eventId, 0));
        }
        else
            //CASE: No filter
            if(filter == 1)
                //Planning tag present
                if(planningCheck) {
                    //Check for permission
                    if(Integer.parseInt(permission) > 1)
                        eventsList.add(0, new Event(name, description, authorName, eventId, 0));
                }
                //No planning tag present
                else
                    eventsList.add(0, new Event(name, description, authorName, eventId, 0));
                //CASE: CSS filter
            else
                //CSS tag present
                if(cssCheck) {
                    //Planning tag present
                    if(planningCheck) {
                        //Check for permission
                        if(Integer.parseInt(permission) > 1)
                            eventsList.add(0, new Event(name, description, authorName, eventId, 0));
                    }
                    //No planning tag present
                    else
                        eventsList.add(0, new Event(name, description, authorName, eventId, 0));
                }
    }


    //Check if the Activity is launched for the first time
    // ---------------------------------------------------------------------------------------------------
    private void checkFirstTime(){
        //Initialize SharedPreferences
        SharedPreferences settings = getSharedPreferences("showpopup", 0);
        SharedPreferences.Editor editor2 = settings.edit();

        Cursor check = mDatabaseHelper.getData();
        Integer id = -1;
        while(check.moveToNext()) {
            id = check.getInt(0);
        }

        if(id == -1) {
            editor2.putBoolean("firsttime", false);
            editor2.apply();
        }

        //CASE: Activity not launched first time
        if(settings.getBoolean("firsttime",false)){}
        //CASE: Activity launched first time
        else{
            //Store value in SharedPreferences as true
            editor2.putBoolean("firsttime", true);
            editor2.apply();
            //Set currentUser to 0
            currentUser = "0";
            //Start LoginActivity
            startActivity(new Intent((Context) HomeActivity.this, LoginActivity.class));
            finish();
        }
    }


    //When an list item is clicked, do :
    // ---------------------------------------------------------------------------------------------------
    private void listItemListener() {
        eventsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent,View view,int position,long id)
            {
                TextView textview = view.findViewById(R.id.Id_textView);
                //Get string from TextView
                String eId = textview.getText().toString();
                Intent intent = new Intent((Context) HomeActivity.this, EventDisplayActivity.class);
                //Send string to EventDisplayActivity
                intent.putExtra("EXTRA_MESSAGE", eId);
                startActivity(intent);

            }
        });
    }


    //When back button is pressed:
    // ---------------------------------------------------------------------------------------------------
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onBackPressed() {
        if(MembersActivity.getSearch("key_1",this ) == null)
            MembersActivity.setSearch("key_1", "0", this);
        if(MembersActivity.getSearch("key_1",this ).equals("1")) {
            eAdapter.clearAdapter();
            displayEvents();
           MembersActivity.setSearch("key_1", "0", this);

        }
       else
        new AlertDialog.Builder(this,R.style.AlertDialogCustom)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Exit App")
                .setMessage("Are you sure you want to exit the app?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    public void onStart() {
        Intent intent = getIntent();
        message = intent.getStringExtra("EXTRA_MESSAGE");
        LoginActivity n = new LoginActivity();
        if(message == null)
            message = "1";
        n.eventListener(mDatabaseHelper, HomeActivity.this, message);
        super.onStart();
    }


    @Override
    protected void onStop() {
        super.onStop();
        MembersActivity.setSearch("key_1", "0", this);
    }


    //Starts ProfileActivity
    // ---------------------------------------------------------------------------------------------------
    public void goToProfile(View view) {
        Intent intent = new Intent((Context) this, ProfileActivity.class);
        intent.putExtra("EXTRA_MESSAGE",author );
        startActivity(intent);
    }

    private void setUpToolbarMenu() {
        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle("Home");
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
                String id = CreateProfileActivity.getDefaults("String_key", HomeActivity.this);
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

    @Override
    public boolean onQueryTextSubmit(String query) {
        MembersActivity.setSearch("key_1", "1", this);
        eAdapter.clearAdapter();
        String querySearch;
        querySearch = String.valueOf(searchView.getQuery());
        if(name != null)
        if (name.toLowerCase().contains(querySearch.toLowerCase()))
            eventsList.add(0, new Event(name, description, authorName, eventId, 0));
        eventsListView.setAdapter(eAdapter);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }


    private void displayEvents() {
        //Get all the events
        Cursor data = mDatabaseHelper.getEvents();
        while(data.moveToNext()) {
            //Get event data
            name = data.getString(1);
            description = data.getString(6);
            eventId = data.getString(0);
            author = data.getString(2);
            authorName = mDatabaseHelper.getItemName(author);

            //Get event's tags
            tagsEvent = new String[8];
            Cursor data3 = mDatabaseHelper.getEventTags(Integer.parseInt(eventId));
            i = 0;
            while(data3.moveToNext()) {
                tagsEvent[i] = data3.getString(1);
                i++;
            }

            //Start filter
            filter();
        }
    }

    public ArrayList<Event> returnlist(){
        return eventsList;
    }
}
