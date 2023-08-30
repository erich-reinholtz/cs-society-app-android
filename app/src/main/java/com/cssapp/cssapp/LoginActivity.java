package com.cssapp.cssapp;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.cssapp.cssapp.CreateProfileActivity.getGoogle;

/**
 Google SignIn implementation
 ---------------------------------------------------------------------------------------------------
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    private static final int RC_SIGN_IN = 9001;

    private DatabaseHelper helper = new DatabaseHelper(this);

    private GoogleSignInClient mGoogleSignInClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if(MembersActivity.getSearch("theme",this) != null)
            if ( MembersActivity.getSearch("theme",this).equals("1")) {
                setTheme(R.style.DarkAppTheme);
            }
            else setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        findViewById(R.id.sign_in_button).setOnClickListener(this);


        // FILLING UP DATABASE

       eventListener(helper, LoginActivity.this, "1");

    }

    public void eventListener(final DatabaseHelper helper, final Context context, final String message){
       FirebaseDatabase mFirebaseDatabase;
       DatabaseReference myRef;


        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();




        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(message.equals("create")) {

                }
                else
                    helper.clearDatabase(context);

                addMatchesFromFirebase(dataSnapshot, helper);
                addUsersFromFirebase(dataSnapshot, helper);
                addEventTagsFromFirebase(dataSnapshot, helper);
                addParticipantsFromFirebase(dataSnapshot, helper);
                addCommentsFromFirebase(dataSnapshot, helper);
                addEventsFromFirebase(dataSnapshot, helper);


            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addCommentsFromFirebase(DataSnapshot dataSnapshot, DatabaseHelper helper) {

        for (DataSnapshot ds : dataSnapshot.child("comments").getChildren()) {

            if(ds != null){

                String comment = ds.child("comment").getValue(String.class);
                Integer eventID = ds.child("eventID").getValue(Integer.class);
                String time = ds.child("time").getValue(String.class);
                Integer userID = ds.child("userID").getValue(Integer.class);

                if( comment != null &&
                        eventID != null &&
                            time != null &&
                               userID != null){


                    if(!helper.checkIfEventExists(eventID)) {
                        helper.addComment(eventID, userID, comment, time);
                    }
                }
           }
        }
    }

    private void addParticipantsFromFirebase(DataSnapshot dataSnapshot, DatabaseHelper helper) {


        for (DataSnapshot ds : dataSnapshot.child("participants").getChildren()) {

            if(ds != null) {

                Integer eventId = ds.child("eventID").getValue(Integer.class);
                Integer personId = ds.child("personID").getValue(Integer.class);

                if(eventId != null && personId != null){

                    if(!helper.checkIfEventExists(eventId)){
                        helper.addParticipant(personId, eventId);
                    }
                }
            }
        }
    }


    private void addEventTagsFromFirebase(DataSnapshot dataSnapshot, DatabaseHelper helper) {

        for(DataSnapshot ds : dataSnapshot.child("eventTags").getChildren()){

            if(ds != null) {

                Integer eventId = ds.child("userId").getValue(Integer.class);
                String tagName = ds.child("tagName").getValue(String.class);

                if(eventId != null
                        &&
                        tagName != null){


                    if(!helper.checkIfEventExists(eventId)){
                        helper.addEventTag(eventId, tagName);
                    }
                }
            }
        }
    }


    private void addMatchesFromFirebase(DataSnapshot dataSnapshot, DatabaseHelper helper) {

        for (DataSnapshot ds : dataSnapshot.child("matches").getChildren()) {

            if (ds != null) {

                Integer userId = ds.child("userId").getValue(Integer.class);
                String tagName = ds.child("tagName").getValue(String.class);


                // INSERTING ONLY IF NO NULL VALUES READ
                if (    userId != null &&
                        tagName != null ) {

                    // add check if user exists = done
                    if(!helper.checkIfUserExists(userId))
                        helper.addMatch(userId, tagName);

                }
            }

        }
    }


    private void addUsersFromFirebase(DataSnapshot dataSnapshot, DatabaseHelper helper) {

        for (DataSnapshot ds : dataSnapshot.child("userDetails").getChildren()) {
            if (ds != null) {

                Integer id = ds.child("id").getValue(Integer.class);
                String name = ds.child("name").getValue(String.class);
                String surname = ds.child("surname").getValue(String.class);
                Integer year = ds.child("year").getValue(Integer.class);
                String picture = ds.child("picture").getValue(String.class);
                Integer permission = ds.child("permission").getValue(Integer.class);
                String google = ds.child("google").getValue(String.class);


                // INSERTING ONLY IF NO NULL VALUES READ
                if (    id != null &&
                        name != null &&
                        surname != null &&
                        year != null &&
                        picture != null &&
                        permission != null &&
                        google != null) {


                    byte[] decodedPicture = android.util.Base64.decode(picture, android.util.Base64.DEFAULT);

                    // add check if user exists = done
                    if(!helper.checkIfUserExists(id))
                        helper.addData(id, name, surname, year, new String[0], decodedPicture,
                            permission, google);
                }
            }

        }
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void addEventsFromFirebase(DataSnapshot dataSnapshot, DatabaseHelper helper) {
        for (DataSnapshot ds : dataSnapshot.child("events").getChildren()) {

            if (ds != null) {

                Integer id = ds.child("id").getValue(Integer.class);
                String name = ds.child("name").getValue(String.class);
                Integer author = ds.child("author").getValue(Integer.class);
                String date = ds.child("date").getValue(String.class);
                String time = ds.child("time").getValue(String.class);
                String location = ds.child("location").getValue(String.class);
                String description = ds.child("description").getValue(String.class);
                String duration = ds.child("duration").getValue(String.class);
                String encodedPicture = ds.child("picture").getValue(String.class);

                if( id != null &&
                        name != null &&
                            author != null &&
                                date != null &&
                                    time != null &&
                                        location != null &&
                                            description != null &&
                                                duration != null &&
                                                    encodedPicture != null ){

                    byte[] decodedPicture = android.util.Base64.decode(encodedPicture, android.util.Base64.DEFAULT);

                    if(!helper.checkIfEventExists(id)){
                        helper.addEvent(id, name, author, date, time, location, description, new String[0], duration, decodedPicture);
                    }

                }


            }


        }
    }



    //Click function for the SingIn Button
    //---------------------------------------------------------------------------------------------------
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
            // ...
        }
    }


    //Google SignIn function
    // ---------------------------------------------------------------------------------------------------
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    //SingIn request function
    //---------------------------------------------------------------------------------------------------
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {

            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
            if(account != null) {

                String personEmail = account.getEmail();
                assert personEmail != null;

                //CASE: Request from University email
                if (personEmail.contains("@my.bristol.ac.uk")) {
                    goToCreateProfile(personEmail);
                    Toast.makeText(LoginActivity.this, "Signed in", Toast.LENGTH_SHORT).show();
                }
                //CASE: Request from non-University email
                else
                    Toast.makeText(LoginActivity.this, "Please sign in with your University account.", Toast.LENGTH_SHORT).show();
                signOut();
            }
        }
    }


    //Google SignOut function
    // ---------------------------------------------------------------------------------------------------
    public void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
    }


    // Start CreateProfileActivity
    //----------------------------------------------------------------------------------------------------
    private void goToCreateProfile(String personEmail) {
        Intent intent = new Intent(this, CreateProfileActivity.class);
        intent.putExtra("EXTRA_MESSAGE", personEmail);
        startActivity(intent);
        finish();
    }


    //When the back button is clicked:
    //----------------------------------------------------------------------------------------------------
    public void onBackPressed() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
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
}
