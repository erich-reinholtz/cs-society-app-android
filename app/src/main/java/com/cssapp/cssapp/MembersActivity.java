package com.cssapp.cssapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;


public class MembersActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, SearchView.OnQueryTextListener {

    private DatabaseHelper mDatabaseHelper;
    private String userName;
    private String userLastName;
    private String UserId;
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private SearchView searchView;
    ListView membersListView;
    EventsAdapter membersAdapter;
    ArrayList<Event> membersList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if(MembersActivity.getSearch("theme",this) != null)
            if ( MembersActivity.getSearch("theme",this).equals("1")) {
                setTheme(R.style.DarkAppTheme);
            }
            else setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_members);

         membersList = new ArrayList<>();
         membersListView = findViewById(R.id.membersListView);

        //Initialise the database
        mDatabaseHelper = new DatabaseHelper(this);
        searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(this);
        membersAdapter = new EventsAdapter(this, membersList);

        createMembersList();




        membersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent,View view,int position,long id)
            {
                //Get string from TextView
                TextView textview = view.findViewById(R.id.Id_textView);
                String eId = textview.getText().toString();
                Intent intent = new Intent(MembersActivity.this, ProfileActivity.class);
                //Send string to EventDisplayActivity
                intent.putExtra("EXTRA_MESSAGE", eId);
                startActivity(intent);
            }
        });

        EventsActivity.setListViewHeightBasedOnChildren(membersListView);

        setUpToolbarMenu();
        setUpNavigationDrawerMenu();

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    searchView.setIconified(true);
                }
            }
        });

    }

    public void onBackPressed() {
        Intent intent;
        if(getSearch("key",this ) == null)
            setSearch("key", "0", this);
        if(getSearch("key",this ).equals("1")) {
            membersAdapter.clearAdapter();
            createMembersList();
            setSearch("key", "0", this);

        }
        else{
            intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();}
    }

    private void setUpToolbarMenu() {
        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle("Members");
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
                String id = CreateProfileActivity.getDefaults("String_key", MembersActivity.this);
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
    protected void onStop() {
        super.onStop();
        setSearch("key", "0", this);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        setSearch("key", "1", this);
        membersAdapter.clearAdapter();
        String querySearch;
        querySearch = String.valueOf(searchView.getQuery());
        if ((userName + " " + userLastName).toLowerCase().contains(querySearch.toLowerCase()))
           membersList.add(0, new Event(userName + " " + userLastName, "", "", UserId, 1));
        membersListView.setAdapter(membersAdapter);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    public static void setSearch(String key, String value, Context context) {
        SharedPreferences search = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = search.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getSearch(String key, Context context) {
        SharedPreferences search = PreferenceManager.getDefaultSharedPreferences(context);
        return search.getString(key, null);
    }


    private void createMembersList() {
        Cursor membersData = mDatabaseHelper.getData();
        while(membersData.moveToNext()) {
            userName = membersData.getString(1);
            userLastName = membersData.getString(2);
            UserId = membersData.getString(0);



            membersList.add(0, new Event(userName + " " + userLastName, "", "", UserId, 1));
        }
        membersListView.setAdapter(membersAdapter);
    }

}
