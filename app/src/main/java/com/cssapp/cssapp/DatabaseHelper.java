package com.cssapp.cssapp;


import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import android.util.Base64;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by User on 2/28/2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {


    Long maxidMatches;
    Long maxidEventTags;
    Long maxidParticipants;
    Long maxidComments;

    DatabaseReference reffMatches;
    DatabaseReference reffEventTags;
    DatabaseReference reffParticipants;
    DatabaseReference reffComments;
    DatabaseReference reffUsers;


    private static final String TAG = "DatabaseHelper";


    // columns of people_table
    private static final String TABLE_NAME = "people_table";
    private static final String COL1 = "ID";
    private static final String COL2 = "name";
    private static final String COL3 = "surname";
    private static final String COL4 = "year";
    private static final String COL5 = "image";
    private static final String COL6 = "permission";
    private static final String COL7 = "google";

    // columns of people tag match table

    private static final String MATCH_TABLE_NAME = "match_table";
    private static final String COL_MATCH_1 = "userID";
    private static final String COL_MATCH_2 = "tagID";

    // columns of event tags table

    private static final String EVENT_TAG_TABLE_NAME = "event_tags";
    private static final String COL_EVENT_TAG_1 = "eventID";
    private static final String COL_EVENT_TAG_2 = "tagID";

    // columns of events table

    private static final String EVENTS_TABLE_NAME = "events_table";
    private static final String COL_EVENTS_1 = "ID";
    private static final String COL_EVENTS_2 = "name";
    private static final String COL_EVENTS_3 = "author";
    private static final String COL_EVENTS_4 = "date";
    private static final String COL_EVENTS_5 = "time";
    private static final String COL_EVENTS_6 = "location";
    private static final String COL_EVENTS_7 = "description";
    private static final String COL_EVENTS_8 = "duration";
    private static final String COL_EVENTS_9 = "image";

    // columns of participants table

    private static final String PARTICIPANTS_TABLE_NAME = "participants_table";
    private static final String COL_PARTICIPANTS_1 = "personID";
    private static final String COL_PARTICIPANTS_2 = "tableID";

    // columns of comments table
    private static final String COMMENTS_TABLE_NAME = "comments_table";
    private static final String COL_COMMENTS_1 = "eventID";
    private static final String COL_COMMENTS_2 = "userID";
    private static final String COL_COMMENTS_3 = "comment";
    private static final String COL_COMMENTS_4 = "time";



    DatabaseReference mDatabase;


    public DatabaseHelper(Context context) {
        super(context, TABLE_NAME, null, 1);

        mDatabase = FirebaseDatabase.getInstance().getReference();



        reffMatches = FirebaseDatabase.getInstance().getReference().child("matches");
        reffMatches.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                maxidMatches = dataSnapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        reffEventTags = FirebaseDatabase.getInstance().getReference().child("eventTags");
        reffEventTags.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                maxidEventTags = dataSnapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    public void onCreate(SQLiteDatabase db) {


        String createTable = "CREATE TABLE " + TABLE_NAME + " (" + COL1 +" INTEGER, " +
                COL2 +" TEXT, " + COL3 + " TEXT, " + COL4 + " INTEGER, " + COL5 + " BLOB, " + COL6 + " INTEGER,"+ COL7 + " TEXT)" ;
        // CREATE TABLE PEOPLE (ID INTEGER PR K AUTO, name TEXT, surname TEXT, year INTEGER, image BLOB, permission INTEGER)

        String createMatchTable = "CREATE TABLE " + MATCH_TABLE_NAME + " (" + COL_MATCH_1 + " INTEGER, " +
                COL_MATCH_2 +" TEXT)";
        // CREATE TABLE MATCH ( UserID INTEGER, TagID TEXT)

        String createEventsTable = "CREATE TABLE " + EVENTS_TABLE_NAME +
                " (ID INTEGER, " + COL_EVENTS_2 + " TEXT, " +
                COL_EVENTS_3 + " INTEGER, " + COL_EVENTS_4 + " TEXT, " + COL_EVENTS_5 +
                " TEXT, " + COL_EVENTS_6 + " TEXT, " + COL_EVENTS_7 + " TEXT," + COL_EVENTS_8 + " TEXT, " + COL_EVENTS_9 +" BLOB)";
        // CREATE TABLE EVENTS (ID INTEGER PR K AUTO, name TEXT, author INTEGER,
        //                                  date TEXT, time TEXT, location TEXT, description TEXT )

        String createEventsTagTable = "CREATE TABLE " + EVENT_TAG_TABLE_NAME + " (" + COL_EVENT_TAG_1
                + " INTEGER, " + COL_EVENT_TAG_2 +" TEXT)";
        // CREATE TABLE EVENT TAG TABLE ( Event ID INTEGER, TagID TEXT)

        String createPaticipantsTable = "CREATE TABLE " + PARTICIPANTS_TABLE_NAME + " (" + COL_PARTICIPANTS_1
                + " INTEGER, " + COL_PARTICIPANTS_2 +" INTEGER)";
        // CREATE TABLE MATCH ( PersonID INTEGER, EventID INTEGER)

        String createCommentsTable = "CREATE TABLE " + COMMENTS_TABLE_NAME + " (" + COL_COMMENTS_1
                + " INTEGER, " + COL_COMMENTS_2 +" INTEGER,"+ COL_COMMENTS_3 +" TEXT," + COL_COMMENTS_4 +" TEXT)";
        // CREATE TABLE MATCH ( PersonID INTEGER, EventID INTEGER)


        db.execSQL(createTable);
        db.execSQL(createMatchTable);
        db.execSQL(createEventsTable);
        db.execSQL(createEventsTagTable);
        db.execSQL(createPaticipantsTable);
        db.execSQL(createCommentsTable);
    }


    public void clearDatabase(Context context) {
        DatabaseHelper helper = new DatabaseHelper(context);
        SQLiteDatabase database = helper.getWritableDatabase();
        database.delete(EVENTS_TABLE_NAME, null, null);
        database.delete(EVENT_TAG_TABLE_NAME, null, null);
        database.delete(TABLE_NAME, null, null);
        database.delete(MATCH_TABLE_NAME, null, null);
        database.delete(PARTICIPANTS_TABLE_NAME, null, null);
        database.delete(COMMENTS_TABLE_NAME, null, null);
        database.close();
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MATCH_TABLE_NAME);
        onCreate(db);
    }


    /**
     Adding functions
     ---------------------------------------------------------------------------------------------------
     */
    public boolean addData(Integer id, String name, String surname, Integer year, String[] tags, byte[] picture, Integer permission, String google) {
        SQLiteDatabase db = this.getWritableDatabase();

        // inserting names
        ContentValues contentValues = new ContentValues();

        contentValues.put(COL7, google);
        contentValues.put(COL6, permission);
        contentValues.put(COL5, picture);
        contentValues.put(COL4, year);
        contentValues.put(COL3, surname);
        contentValues.put(COL2, name);
        contentValues.put(COL1, id);

        addMatches(id, tags);

        Log.d(TAG, "addData: Adding " + name + " to " + TABLE_NAME);
        Log.d(TAG, "addData: Adding " + surname + " to " + TABLE_NAME);

        long result = db.insert(TABLE_NAME, null, contentValues);

        Log.d(TAG, "addData: Values successfully inserted");


        //if date as inserted incorrectly it will return -1
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }


    public void addMatches(Integer user, String[] tags) {
        Boolean works;
        for(String tag: tags) {
            if(tag != null)
                works = addMatch(user, tag);
        }

    }


    public boolean addMatch(Integer user, String tag)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_MATCH_2, tag);
        contentValues.put(COL_MATCH_1, user);


        Log.d(TAG, "addData: Adding "+ tag +" to " + MATCH_TABLE_NAME);

        long result = db.insert(MATCH_TABLE_NAME, null, contentValues);

        //if date as inserted incorrectly it will return -1
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void addTagsToCloud(Integer user, String[] tags){
        for(String tag: tags) {
            if (tag != null){
                DBMatch match = new DBMatch(user, tag);

                int min = 1 ;
                int max = 999_999 ;
                int randomNum = ThreadLocalRandom.current().nextInt( min , max + 1 );
                reffMatches.child(String.valueOf(randomNum)).setValue(match);
            }
        }
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public boolean addEvent(Integer id, String name, Integer author, String date, String time,
                            String location, String description, String[] tags, String duration, byte[] picture)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_EVENTS_9, picture);
        contentValues.put(COL_EVENTS_8, duration);
        contentValues.put(COL_EVENTS_7, description);
        contentValues.put(COL_EVENTS_6, location);
        contentValues.put(COL_EVENTS_5, time);
        contentValues.put(COL_EVENTS_4, date);
        contentValues.put(COL_EVENTS_3, author);
        contentValues.put(COL_EVENTS_2, name);
        contentValues.put(COL_EVENTS_1, id);

        Log.d("addEvent", "addEvent: Adding "+ name +" to " + EVENTS_TABLE_NAME);

        long result = db.insert(EVENTS_TABLE_NAME, null, contentValues);


        //----------------------------------------------
        // Adding tags
        addEventTags(id, tags);

        //if date as inserted incorrectly it will return -1
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }


    public void addEventTags(Integer id, String[] tags) {
        Boolean works;
        for(String tag: tags) {
            if(tag != null)
                works = addEventTag(id, tag);
        }
    }


    public boolean addEventTag(Integer id, String tag)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_EVENT_TAG_2, tag);
        contentValues.put(COL_EVENT_TAG_1, id);

        long result = db.insert(EVENT_TAG_TABLE_NAME, null, contentValues);

        //if date as inserted incorrectly it will return -1
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void addEventTagsToCloud(Integer user, String[] tags){
        for(String tag: tags) {
            if (tag != null){
                DBMatch match = new DBMatch(user, tag);

                int min = 1 ;
                int max = 999_999 ;
                int randomNum = ThreadLocalRandom.current().nextInt( min , max + 1 );
                reffEventTags.child(String.valueOf(randomNum)).setValue(match);
            }
        }
    }


    public boolean addParticipant(Integer personID, Integer eventID)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_PARTICIPANTS_2, eventID);
        contentValues.put(COL_PARTICIPANTS_1, personID);


        long result = db.insert(PARTICIPANTS_TABLE_NAME, null, contentValues);


        //if date as inserted incorrectly it will return -1
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }


    public boolean addComment(Integer eventID, Integer userID, String comment, String time)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_COMMENTS_4, time);
        contentValues.put(COL_COMMENTS_3, comment);
        contentValues.put(COL_COMMENTS_2, userID);
        contentValues.put(COL_COMMENTS_1, eventID);


        long result = db.insert(COMMENTS_TABLE_NAME, null, contentValues);

        //if date as inserted incorrectly it will return -1
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    /**
     Getting functions
     ---------------------------------------------------------------------------------------------------
     */
    public Cursor getData(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor data = db.rawQuery(query, null);
        return data;
    }


    public Integer getUser(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT MAX(" + COL1 + ") FROM " + TABLE_NAME;
        Cursor data = db.rawQuery(query, null);

        String result = "-1";
        while(data.moveToNext()) {
            result = data.getString(0);
        }
        return Integer.parseInt(result);
    }


    public Cursor getDataById(Integer id){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE ID = " + id;
        Cursor data = db.rawQuery(query, null);
        return data;
    }


    public String getItemName(String id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + COL2 + ", " + COL3 + " FROM " + TABLE_NAME + " WHERE " + COL1 + " = " + id;
        // SELECT NAME, SURNAME FROM TABLE WHERE ID = id

        Cursor data = db.rawQuery(query, null);

        String result = "Author name not found";
        String result2 = "Author surname not found";
        while(data.moveToNext())
        {
            result = data.getString(0);
            result2 = data.getString(1);
        }

        return result + " " + result2;
    }


    public Cursor getTagMatchesById(Integer id){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + MATCH_TABLE_NAME + " WHERE "+ COL_MATCH_1 +" = " + id;
        Cursor data = db.rawQuery(query, null);
        return data;
    }


    public Cursor getEventTags(Integer id){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + EVENT_TAG_TABLE_NAME + " WHERE "+ COL_EVENT_TAG_1 +" = " + id;
        // SELECT * FROM EVENT TAGS WHERE ID = id
        Cursor data = db.rawQuery(query, null);
        return data;
    }


    public Cursor getEvents(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + EVENTS_TABLE_NAME;
        Cursor data = db.rawQuery(query, null);
        return data;
    }


    public Cursor getEventsWithId(Integer id){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + EVENTS_TABLE_NAME + " WHERE author = " + id;
        Cursor data = db.rawQuery(query, null);
        return data;
    }


    public Cursor getEventWithId(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();

        String query = "SELECT * FROM " + EVENTS_TABLE_NAME + " WHERE ID = " + id;
        // SELECT * FROM EVENTS WHERE ID = id

        Cursor data = db.rawQuery(query, null);
        return data;

    }


    public Cursor getEventsWithdate(String eventdate){
        SQLiteDatabase db = this.getWritableDatabase();

        String query = "SELECT * FROM " + EVENTS_TABLE_NAME + " WHERE date='" + eventdate +"'";

        Cursor data = db.rawQuery(query, null);
        return data;
    }


    public String getEventName(String eventID)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + COL_EVENTS_2 + " FROM " + EVENTS_TABLE_NAME + " WHERE " + COL_EVENTS_1
                + " = " + eventID;
        // SELECT NAME FROM EVENT_TABLE WHERE ID = id

        Cursor data = db.rawQuery(query, null);

        String result = "Event name not found";

        while(data.moveToNext())
        {
            result = data.getString(0);
        }

        return result;

    }


    public Cursor getCommentWithId(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();

        String query = "SELECT * FROM " + COMMENTS_TABLE_NAME + " WHERE "+ COL_COMMENTS_1 +" = " + id;
        // SELECT * FROM EVENTS WHERE ID = id

        Cursor data = db.rawQuery(query, null);
        return data;

    }


    public Integer getNumberOfParticipants(String id)
    {
        SQLiteDatabase db = getReadableDatabase();
      return (int)  DatabaseUtils.queryNumEntries(db, "participants_table",
                "tableID=?", new String[] {id});
    }


    public Cursor getEventsOfUser(Integer userID)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + PARTICIPANTS_TABLE_NAME + " WHERE " + COL_PARTICIPANTS_1
                + " = " + userID;
        // SELECT * FROM participants_table WHERE userID = id
        Cursor data = db.rawQuery(query, null);
        return data;
    }


    /**
     Deleting functions
     ---------------------------------------------------------------------------------------------------
     */
    public void deleteParticipantEntry(Integer participantID, Integer eventID)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + PARTICIPANTS_TABLE_NAME + " WHERE " + COL_PARTICIPANTS_1
                + " = " + participantID + " AND " + COL_PARTICIPANTS_2 + " = " + eventID;
        // DELETE FROM participants_table WHERE col1 = partID and col2 = eventID
        Log.d(TAG, "deleteName: query: " + query);
        db.execSQL(query);

        removeParticipantFromFirebase(participantID, eventID);
    }


    private void removeParticipantFromFirebase(final Integer participantID, final Integer eventID){

        DatabaseReference tagRef = FirebaseDatabase.getInstance().getReference().child("participants");

        tagRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    if( ds.child("eventID").getValue(Integer.class).equals( eventID )
                            && ds.child("personID").getValue(Integer.class).equals( participantID ) ){
                        String key = ds.getKey();
                        mDatabase.child("participants").child(key).removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void removeTagsFromFirebase(Integer id){

        final Integer identity = id;

        DatabaseReference tagRef = FirebaseDatabase.getInstance().getReference().child("matches");

        tagRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    if(ds.child("userId").getValue(Integer.class).equals( identity )){
                        String key = ds.getKey();
                        mDatabase.child("matches").child(key).removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void removeEventTagsFromFirebase(Integer id) {

        final Integer identity = id;

        DatabaseReference tagRef = FirebaseDatabase.getInstance().getReference().child("eventTags");

        tagRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    if(ds.child("userId").getValue(Integer.class).equals( identity )){
                        String key = ds.getKey();
                        mDatabase.child("eventTags").child(key).removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void deleteEventById(Integer id){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + EVENTS_TABLE_NAME + " WHERE ID = " + id;
        Log.d(TAG, "deleteName: query: " + query);
        db.execSQL(query);

        mDatabase.child("events").child(id.toString()).removeValue();
        removeEventTagsFromFirebase(id);
    }


    public void deleteCommentsById(Integer id){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + COMMENTS_TABLE_NAME + " WHERE " + COL_COMMENTS_1 + " = " + id;
        Log.d(TAG, "deleteName: query: " + query);
        db.execSQL(query);

        removeCommentsFromFirebase(id);
    }


    private void removeCommentsFromFirebase(Integer id){

        final Integer identity = id;

        DatabaseReference tagRef = FirebaseDatabase.getInstance().getReference().child("comments");

        tagRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    if(ds.child("eventID").getValue(Integer.class).equals( identity )){
                        String key = ds.getKey();
                        mDatabase.child("comments").child(key).removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void deleteCommentByUser(Integer userID, Integer eventID, String time){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + COMMENTS_TABLE_NAME + " WHERE " + COL_COMMENTS_1 + " = " + eventID + " AND " + COL_COMMENTS_2 + " = " + userID + " AND " + COL_COMMENTS_4 + " = '" + time + "'";
        Log.d(TAG, "deleteName: query: " + query);
        db.execSQL(query);

        removeCommentsFromFirebaseByUser(userID, eventID, time);
    }


    private void removeCommentsFromFirebaseByUser(final Integer userID, final Integer eventID, final String time){


        DatabaseReference tagRef = FirebaseDatabase.getInstance().getReference().child("comments");

        tagRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    if(
                            ds.child("userID").getValue(Integer.class).equals( userID )
                                && ds.child("eventID").getValue(Integer.class).equals( eventID )
                                    && ds.child("time").getValue(String.class).equals( time )

                            ){
                        String key = ds.getKey();
                        mDatabase.child("comments").child(key).removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void deleteProfileById(Integer id){
        SQLiteDatabase db = this.getWritableDatabase();

        String query2 = "DELETE FROM " + EVENTS_TABLE_NAME + " WHERE author = " + id;
        db.execSQL(query2);



        removeEventsFromFirebase(id);
        removeEventTagsFromFirebase(id);

    }


    private void removeEventsFromFirebase(Integer idAuthor){

        final Integer identity = idAuthor;

        DatabaseReference tagRef = FirebaseDatabase.getInstance().getReference().child("events");

        tagRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    if(ds.child("author").getValue(Integer.class).equals( identity )){
                        String key = ds.getKey();
                        mDatabase.child("events").child(key).removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    /**
     Updating functions
     ---------------------------------------------------------------------------------------------------
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void updateProfile(Integer id,  // changed from int to Integer, hope this doesn't break anything
                              String name,
                              String surname,
                              Integer year,
                              String[] tags,
                              byte[] picture) {

        ContentValues cv = new ContentValues();
        cv.put(COL2,name);
        cv.put(COL3,surname);
        cv.put(COL4,year);
        cv.put(COL5,picture);
        SQLiteDatabase db = this.getWritableDatabase();
        db.update(TABLE_NAME,
                cv,
                COL1+"=?",
                new String[]{Long.toString(id)}
        );
        Log.i("db update s=", "s= " );
        String query = "DELETE FROM " + MATCH_TABLE_NAME + " WHERE userID = " + id;
        db.execSQL(query);



        removeTagsFromFirebase(id);
        addMatches(id, tags);
        addTagsToCloud(id, tags);

        String encodedPicture = Base64.encodeToString(picture, Base64.DEFAULT);

        mDatabase.child("userDetails").child(id.toString()).child("name").setValue(name);
        mDatabase.child("userDetails").child(id.toString()).child("surname").setValue(surname);
        mDatabase.child("userDetails").child(id.toString()).child("year").setValue(year);
        mDatabase.child("userDetails").child(id.toString()).child("picture").setValue(encodedPicture);

    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void updateEvent(int id,
                            String name,
                            String date,
                            String time,
                            String location,
                            String description,
                            String[] tags,
                            String duration,
                            byte[] picture) {

        ContentValues cv = new ContentValues();
        cv.put(COL_EVENTS_2,name);
        cv.put(COL_EVENTS_4,date);
        cv.put(COL_EVENTS_5,time);
        cv.put(COL_EVENTS_6,location);
        cv.put(COL_EVENTS_7,description);
        cv.put(COL_EVENTS_8,duration);
        cv.put(COL_EVENTS_9,picture);
        SQLiteDatabase db = this.getWritableDatabase();
        db.update(EVENTS_TABLE_NAME,
                cv,
                COL_EVENTS_1+"=?",
                new String[]{Long.toString(id)}
        );

        String query = "DELETE FROM " + EVENT_TAG_TABLE_NAME + " WHERE eventID = " + id;
        db.execSQL(query);

        removeEventTagsFromFirebase(id);
        addEventTags(id, tags);
        addEventTagsToCloud(id, tags);

        // insertions

        String encodedPicture = Base64.encodeToString(picture, Base64.DEFAULT);

        mDatabase.child("events").child(String.valueOf(id)).child("name").setValue(name);
        mDatabase.child("events").child(String.valueOf(id)).child("date").setValue(date);
        mDatabase.child("events").child(String.valueOf(id)).child("time").setValue(time);
        mDatabase.child("events").child(String.valueOf(id)).child("location").setValue(location);
        mDatabase.child("events").child(String.valueOf(id)).child("description").setValue(description);
        mDatabase.child("events").child(String.valueOf(id)).child("duration").setValue(duration);
        mDatabase.child("events").child(String.valueOf(id)).child("picture").setValue(encodedPicture);

    }


    /**
     Checking functions
     ---------------------------------------------------------------------------------------------------
     */
    public Boolean checkIfUserExists(Integer user){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT COUNT(1) FROM " + TABLE_NAME + " WHERE ID = " + user;

        Cursor data = db.rawQuery(query, null);

        Integer result = -1;

        while(data.moveToNext()){
            result = Integer.parseInt(data.getString(0));
        }

        if(result == 1)
            return true;

        return false;
    }


    public Cursor checkUserWithGoogle(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE google ='" + id + "'";
        Cursor data = db.rawQuery(query, null);
        return data;
    }


    public Boolean checkParticipant(Integer participantID, Integer eventID)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT COUNT(*) FROM " + PARTICIPANTS_TABLE_NAME + " WHERE " +
                COL_PARTICIPANTS_1 + " = " + participantID + " AND " + COL_PARTICIPANTS_2 +
                " = " + eventID;
        // SELECT * FROM PARTICIPANTS_TABLE WHERE col1 = partID AND col2 = eventID

        Cursor data = db.rawQuery(query, null);

        Integer result = -1;

        while (data.moveToNext()) {
            result = Integer.parseInt(data.getString(0));
        }

        if(result == 0)
            return false;

        return true;
    }


    public Boolean checkIfEventExists(Integer eventId){

        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT COUNT(1) FROM " + EVENTS_TABLE_NAME + " WHERE ID = " + eventId;

        Cursor data = db.rawQuery(query, null);

        Integer result = -1;

        while(data.moveToNext()){
            result = Integer.parseInt(data.getString(0));
        }

        if(result == 1)
            return true;

        return false;

    }


    /**
     Utilities functions
     ---------------------------------------------------------------------------------------------------
     */
    public Bitmap getImage(Integer id){

        SQLiteDatabase db = this.getWritableDatabase();
        //String qu = "select " + COL5 + " from " + TABLE_NAME + " where ID=" + i ;
        String qu = "SELECT * FROM " + TABLE_NAME+ " WHERE ID = " + id;
        // select IMAGE from TABLE where ID=i
        Cursor cur = db.rawQuery(qu, null);

        if (cur.moveToFirst()){
            byte[] imgByte = cur.getBlob(4);
            cur.close();

            return BitmapFactory.decodeByteArray(imgByte, 0, imgByte.length);
        }
        if (cur != null && !cur.isClosed()) {
            cur.close();
        }

        return null ;
    }


    public Bitmap getEventImage(Integer id){

        SQLiteDatabase db = this.getWritableDatabase();
        //String qu = "select " + COL5 + " from " + TABLE_NAME + " where ID=" + i ;
        String qu = "SELECT * FROM " + EVENTS_TABLE_NAME + " WHERE ID = " + id;
        // select IMAGE from TABLE where ID=i
        Cursor cur = db.rawQuery(qu, null);

        if (cur.moveToFirst()){
            byte[] imgByte = cur.getBlob(8);
            cur.close();

            return BitmapFactory.decodeByteArray(imgByte, 0, imgByte.length);
        }
        if (cur != null && !cur.isClosed()) {
            cur.close();
        }

        return null ;
    }


    public void changePermission(Integer personID, Integer newPermission)
    {
        ContentValues cv = new ContentValues();
        cv.put(COL6,newPermission);
        SQLiteDatabase db = this.getWritableDatabase();
        db.update(TABLE_NAME,
                cv,
                COL1+"=?",
                new String[]{Long.toString(personID)}
        );

        mDatabase.child("userDetails").child(personID.toString()).child("permission").setValue(newPermission);
    }
}
