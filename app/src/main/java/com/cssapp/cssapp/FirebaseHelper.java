package com.cssapp.cssapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class FirebaseHelper extends AppCompatActivity {


    DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    DBUser dbuser;

    Long maxidUsers;
    DatabaseReference reffMatches;

    public FirebaseHelper(String googleId, DBUser userObj){


        mDatabase = FirebaseDatabase.getInstance().getReference();

        reffMatches = FirebaseDatabase.getInstance().getReference().child("userDetails");
        reffMatches.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                maxidUsers = dataSnapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




        mAuth = FirebaseAuth.getInstance();
        this.dbuser = userObj;

        mAuth.createUserWithEmailAndPassword(googleId, "password")
                .addOnCompleteListener( this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("create user", "createUser:onComplete:" + task.isSuccessful());

                        if (task.isSuccessful()) {
                            onAuthSuccess(task.getResult().getUser(), dbuser);
                        } else {
                            Log.d("create user", "createUser:onComplete: task unsuccessful");
                        }
                    }
                });


    }

    private void onAuthSuccess(FirebaseUser user, DBUser dbuser) {
        String username = usernameFromEmail(user.getEmail());

        // Write new user
        writeNewUser(user.getUid(), username, user.getEmail(), dbuser);

    }

    private String usernameFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }

    // [START basic_write]
    private void writeNewUser(String userId, String name, String email, DBUser dbuser) {

        User currUser = new User(name, email);

        mDatabase.child("users").child(userId).setValue(currUser);
        mDatabase.child("userDetails").child(dbuser.id.toString()).setValue(dbuser);
    }



}
