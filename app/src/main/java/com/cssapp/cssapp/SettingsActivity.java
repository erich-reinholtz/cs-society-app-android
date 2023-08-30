package com.cssapp.cssapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import androidx.annotation.NonNull;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

public class SettingsActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, NavigationView.OnNavigationItemSelectedListener {

    private String permission = "-1";
    private GoogleApiClient mGoogleApiClient;
    private String currentUser;
    private Button btnDelete;
    private DatabaseHelper mDatabaseHelper;
    private Switch makePrivateSwitch;
    private Switch darkThemeSwitch;
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if(MembersActivity.getSearch("theme",this) != null)
        if ( MembersActivity.getSearch("theme",this).equals("1")) {
            setTheme(R.style.DarkAppTheme);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        else setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        makePrivateSwitch = findViewById(R.id.privateProfileSwitch);
        currentUser = CreateProfileActivity.getDefaults("String_key", SettingsActivity.this);
        btnDelete = findViewById(R.id.deleteProfileBtn);
        mDatabaseHelper = new DatabaseHelper(this);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();


        //Enable delete profile function
        deleteProfile();
        //Enable make profile private function
        makePrivate();

        darkThemeSwitch = findViewById(R.id.darkThemeSwitch);
        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            darkThemeSwitch.setChecked(true);
        }



        darkThemeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    MembersActivity.setSearch("theme","1",SettingsActivity.this);
                    recreate();


                }
                else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    MembersActivity.setSearch("theme","0",SettingsActivity.this);
                    recreate();
                }
            }
        });

        setUpToolbarMenu();
        setUpNavigationDrawerMenu();
    }


    // -----------------------------------------------------------------------------------------------------
    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }


    // LogOut function
    // -----------------------------------------------------------------------------------------------------
    public void logOutFunction(View view){
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {}
                });
        Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
        Toast.makeText(SettingsActivity.this, "Signed Out", Toast.LENGTH_SHORT).show();
        startActivity(intent);
        finish();
    }


    // -----------------------------------------------------------------------------------------------------
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}


    // Delete profile function
    // -----------------------------------------------------------------------------------------------------
    private void deleteProfile() {
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder pictureDialog = new AlertDialog.Builder(SettingsActivity.this,R.style.AlertDialogCustom);
                pictureDialog.setTitle("Do you want to delete profile?");
                String[] pictureDialogItems = {
                        "Yes",
                        "No"};
                pictureDialog.setItems(pictureDialogItems,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        Intent intent = new Intent(SettingsActivity.this, CreateProfileActivity.class);
                                        //Delete the event using the id
                                        CreateProfileActivity.setCheck("String_key","true", SettingsActivity.this);
                                        mDatabaseHelper.deleteProfileById(Integer.parseInt(currentUser));
                                        intent.putExtra("settings", currentUser);
                                        startActivity(intent);
                                        finish();
                                        break;
                                    case 1:
                                        break;
                                }
                            }
                        });
                pictureDialog.show();
            }
        });
    }


    // Make profile private function
    // -----------------------------------------------------------------------------------------------------
    private void makePrivate() {
        //Get currentUser's permission
        Cursor per = mDatabaseHelper.getDataById(Integer.parseInt(currentUser));
        while(per.moveToNext()) {
            permission = per.getString(5);
        }

        //Checks user's permission
        boolean value = permission.equals("1");
        makePrivateSwitch.setChecked(value);

        makePrivateSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if(Integer.parseInt(permission) <2 )
                        mDatabaseHelper.changePermission(Integer.parseInt(currentUser), 1);
                    else
                        Toast.makeText(SettingsActivity.this, "Admin/Committee can't make their profile private.", Toast.LENGTH_SHORT).show();
                } else {
                    if(Integer.parseInt(permission) <2 )
                        mDatabaseHelper.changePermission(Integer.parseInt(currentUser), 0);
                    else
                        Toast.makeText(SettingsActivity.this, "Admin/Committee can't make their profile private.", Toast.LENGTH_SHORT).show();
                }
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
        mToolbar.setTitle("Settings");
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
                String id = CreateProfileActivity.getDefaults("String_key", SettingsActivity.this);
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
}
