package com.cssapp.cssapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ListView;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DatabaseHelper mDatabaseHelper;
    private String eventName;
    private String [] tagsEvent;
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private String UserId;
    private Integer currentUser;
    private ImageView picture;
    private Integer id;
    private String name;
    private String surname;
    private String year;
    private String permission;
    private String[] userTags = new String[6];
    private String eventId;
    private ArrayList<Event> eventsList = new ArrayList<>();
    private ListView eventsListView;
    private int i = 0;
    private TextView uName;
    private TextView uYear;
    private TextView uTags;
    private TextView createdEvents;
    private Button btnChangePermissions;
    private Button editBtn;


    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if(MembersActivity.getSearch("theme",this) != null)
            if ( MembersActivity.getSearch("theme",this).equals("1")) {
                setTheme(R.style.DarkAppTheme);
            }
            else setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //Receive user's id
        Intent intent = getIntent();
        UserId = intent.getStringExtra("EXTRA_MESSAGE");

        currentUser = Integer.parseInt(CreateProfileActivity.getDefaults("String_key", ProfileActivity.this));
        picture = findViewById(R.id.Profile_imageView);
        eventsListView = findViewById(R.id.createdEventsListView);
        mDatabaseHelper = new DatabaseHelper(this);
        uName = findViewById(R.id.FirstName_textView);
        uYear = findViewById(R.id.CourseYear_textView);
        uTags = findViewById(R.id.Tags_textView);
        createdEvents = findViewById(R.id.Events_textView);
        btnChangePermissions =  findViewById(R.id.changePermissionsBtn);
        editBtn = findViewById(R.id.editProfileBtn);

        getDataFromDB();
        displayDataFromDB();
        createEventsList();
        //Enable change permission function
        changePermission();
        //When editBtn is clicked:
        editBtnListener();

        setVisibility();

        setUpToolbarMenu();
        setUpNavigationDrawerMenu();



    }
    /**
     Utilities Functions
     ---------------------------------------------------------------------------------------------------
     */
    //Get data from database
    // ---------------------------------------------------------------------------------------------------
    private void getDataFromDB() {
        //Get data from database
        Cursor data = mDatabaseHelper.getDataById(Integer.parseInt(UserId));
        while(data.moveToNext()) {
            id = Integer.parseInt(data.getString(0));
            name = data.getString(1);
            surname = data.getString(2);
            year = data.getString(3);
            permission = data.getString(5);
        }

        //Get the userTags from the database
        Cursor data2 = mDatabaseHelper.getTagMatchesById(id);
        while(data2.moveToNext()) {
            userTags[i] = data2.getString(1);
            i++;
        }
    }


    //Display data from database
    // ---------------------------------------------------------------------------------------------------
    @SuppressLint("SetTextI18n")
    private void displayDataFromDB() {
        // Generating enlisting of userTags in one string variable
        StringBuilder result = new StringBuilder();
        for(String tag: userTags)
        {
            if(tag != null) {
                result.append("• ").append(tag).append("\n");
                Log.d("Result: ", "Concatenating " + tag + " to result.");
            }
        }
        //Store the data from the database
        uName.setText(name + " " + surname);
        uYear.setText("Year " + year);
        uTags.setText(result.toString());

        Bitmap img = mDatabaseHelper.getImage(Integer.parseInt(UserId));
        picture.setImageBitmap(img);
    }


    //Create user's created events list
    // ---------------------------------------------------------------------------------------------------
    private void createEventsList() {
        Cursor eventData = mDatabaseHelper.getEventsWithId(Integer.parseInt(UserId));
        while(eventData.moveToNext()) {
            //Get the data from the database
            eventName = eventData.getString(1);
            eventId = eventData.getString(0);
            tagsEvent = new String[8];
            Cursor eventTags = mDatabaseHelper.getEventTags(Integer.parseInt(eventId));
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
            ////CASE: The event has the CSS planning tag
            if(check) {
                //CASE: User has permission to see the event
                if(Integer.parseInt(permission) > 1)
                    eventsList.add(0, new Event(eventName, "", "", eventId, 1));
            }
            //CASE: The event doesn't have the CSS planning tag
            else
                eventsList.add(0, new Event(eventName, "", "", eventId, 1));
        }
        EventsAdapter eAdapter = new EventsAdapter(this, eventsList);
        eventsListView.setAdapter(eAdapter);

        EventsActivity.setListViewHeightBasedOnChildren(eventsListView);
        //When list item is clicked:
        listItemListener();
    }


    //When list item is clicked:
    // ---------------------------------------------------------------------------------------------------
    private void listItemListener() {
        eventsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent,View view,int position,long id)
            {
                TextView textview = view.findViewById(R.id.Id_textView);
                //Get string from TextView
                String eId = textview.getText().toString();
                Intent intent = new Intent(ProfileActivity.this, EventDisplayActivity.class);
                //Send string to EventDisplayActivity
                intent.putExtra("EXTRA_MESSAGE", eId);
                startActivity(intent);
            }
        });
    }


    //Change permission
    // ---------------------------------------------------------------------------------------------------
    private void changePermission() {
        btnChangePermissions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder pictureDialog = new AlertDialog.Builder(ProfileActivity.this,R.style.AlertDialogCustom);
                pictureDialog.setTitle("Select Action");
                String[] pictureDialogItems = {
                        "Make admin",
                        "Make committee",
                        "Make user"};
                pictureDialog.setItems(pictureDialogItems,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        mDatabaseHelper.changePermission(Integer.parseInt(UserId), 3);
                                        break;
                                    case 1:
                                        mDatabaseHelper.changePermission(Integer.parseInt(UserId), 2);
                                        break;
                                    case 2:
                                        mDatabaseHelper.changePermission(Integer.parseInt(UserId), 0);
                                        break;
                                }
                            }
                        });
                pictureDialog.show();
            }
        });
    }


    //When edit button is clicked:
    // ---------------------------------------------------------------------------------------------------
    private void editBtnListener() {
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
                intent.putExtra("EXTRA_MESSAGE", UserId);
                startActivity(intent);
                finish();
            }
        });
    }


    //Set visibility according to user's permission
    // ---------------------------------------------------------------------------------------------------
    @SuppressLint("SetTextI18n")
    private void setVisibility(){
        if(Integer.parseInt(permission) == 1 && !UserId.contentEquals(String.valueOf(currentUser))) {
            uName.setText("Private Account");
            uYear.setVisibility(View.GONE);
            uTags.setVisibility(View.GONE);
            picture.setVisibility(View.GONE);
            btnChangePermissions.setVisibility(View.GONE);
            editBtn.setVisibility(View.GONE);
            eventsListView.setVisibility(View.GONE);
            createdEvents.setVisibility(View.GONE);
        }

        if(!UserId.contentEquals(String.valueOf(currentUser)))
            editBtn.setVisibility(View.GONE);

        if(Integer.parseInt(permission) < 3) {
            btnChangePermissions.setVisibility(View.GONE);
        }
    }


    //Starts EditProfileActivity
    // ---------------------------------------------------------------------------------------------------
    public void goToEditProfile(View view) {
        Intent intent = new Intent(this, EditProfileActivity.class);
        startActivity(intent);
        finish();
    }



    private void setUpToolbarMenu() {
        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle("Profile");
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
                String id = CreateProfileActivity.getDefaults("String_key", ProfileActivity.this);
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
