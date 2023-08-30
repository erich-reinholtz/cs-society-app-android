package com.cssapp.cssapp;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Build;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;


public class EventDisplayActivity extends AppCompatActivity {
    private Integer currentUser;
    private String name;
    private String date;
    private String time;
    private String location;
    private String description;
    private String[] tags = new String[8];
    private Integer author;
    private String duration;
    private ImageView picture;
    private DatabaseHelper mDatabaseHelper;
    private Integer eventId;
    private TextView eName;
    private TextView eDate;
    private TextView eTime;
    private TextView eDuration;
    private TextView eLocation;
    private TextView eTags;
    private TextView eDescription;
    private TextView usersGoing;
    private Bitmap imgBitmap;
    private Button btnEdit;
    private Button btnDelete;
    private Button btnMark;
    private Button btnComment;
    private ListView commentsListView;
    private ArrayList<Event> commentsList = new ArrayList<>();
    private String commentTime;
    private String commentAuthor;
    private String content;

    DatabaseReference mDatabase;


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
        setContentView(R.layout.activity_event_display);

        //Receive event's id
        Intent intent = getIntent();
        String eventS = intent.getStringExtra("EXTRA_MESSAGE");
        eventId = Integer.parseInt(eventS);


        initializeValues();

        getDataFromDB();
        displayDataFromDB();
        //When editBtn is clicked
        editButtonListener();

        //Enable delete event function
        deleteEvent();
        //Enable mark event function
        markEvent();
        //Enable comment on event function
        commentOnEvent();
        displayComments();
        //Enable delete comment function
        deleteComment();

        //Set visibility according to the user's permission
        if(!author.equals(currentUser)) {
           btnEdit.setVisibility(View.GONE);
            btnDelete.setVisibility(View.GONE);
         }
    }



    //Initialize values
    // ---------------------------------------------------------------------------------------------------
    private void initializeValues(){
        currentUser = Integer.parseInt(CreateProfileActivity.getDefaults("String_key", EventDisplayActivity.this));
        picture = findViewById(R.id.Event_image);
        mDatabaseHelper = new DatabaseHelper(this);
        eName = findViewById(R.id.Name_textView);
        eDate = findViewById(R.id.Date_textView);
        eTime = findViewById(R.id.Time_textView);
        eDuration = findViewById(R.id.Event_duration);
        eLocation = findViewById(R.id.Location_textView);
        eDescription =  findViewById(R.id.Description_textView);
        eTags = findViewById(R.id.Tags_textView);
        usersGoing = findViewById(R.id.UsersNumber_textView);
        imgBitmap = mDatabaseHelper.getEventImage(eventId);
        btnEdit =findViewById(R.id.Edit_button);
        btnDelete = findViewById(R.id.Delete_button);
        btnMark = findViewById(R.id.Mark_button);
        btnComment = findViewById(R.id.Comment_button);
        commentsListView = findViewById(R.id.comments_ListView);

        mDatabase = FirebaseDatabase.getInstance().getReference();

    }




    //Get the data from the database function
    // ---------------------------------------------------------------------------------------------------
    private void getDataFromDB() {
        Cursor eventData = mDatabaseHelper.getEventWithId(eventId) ;
        while(eventData.moveToNext()) {
            name = eventData.getString(1);
            date = eventData.getString(3);
            time = eventData.getString(4);
            location = eventData.getString(5);
            description = eventData.getString(6);
            author = eventData.getInt(2);
            duration = eventData.getString(7);
        }

        Cursor eventTags = mDatabaseHelper.getEventTags(eventId);
        int i = 0;
        while(eventTags.moveToNext()) {
            tags[i] = eventTags.getString(1);
            i++;
        }
    }


    //Display the data from the database function
    // ---------------------------------------------------------------------------------------------------
    private void displayDataFromDB() {
        StringBuilder result = new StringBuilder();
        for(String tag: tags)
        {
            if(tag != null) {
                result.append("• ").append(tag).append("   ");
                Log.d("Result: ", "Concatenating " + tag + " to result.");
            }
        }

        eName.setText(name);
        eDate.setText(date);
        eTime.setText(time);
        eDuration.setText(duration);
        eLocation.setText(location);
        eTags.setText(result.toString());
        eDescription.setText(description);
        usersGoing.setText(String.valueOf(mDatabaseHelper.getNumberOfParticipants(String.valueOf(eventId))));
        picture.setImageBitmap(imgBitmap);
    }


    //When btnEdit is clicked:
    // ---------------------------------------------------------------------------------------------------
    private void editButtonListener() {
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent((Context) EventDisplayActivity.this, EditEventActivity.class);
                //Sends event's id to EditEventActivity
                intent.putExtra("EXTRA_MESSAGE", String.valueOf(eventId));
                startActivity(intent);
                finish();
            }
        });
    }


    //Delete event function
    // ---------------------------------------------------------------------------------------------------
    private void deleteEvent() {
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder pictureDialog = new AlertDialog.Builder((Context) EventDisplayActivity.this,R.style.AlertDialogCustom);
                pictureDialog.setTitle("Do you want to delete event?");
                String[] pictureDialogItems = {
                        "Yes",
                        "No"};
                pictureDialog.setItems(pictureDialogItems,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        Intent intent = new Intent((Context) EventDisplayActivity.this, HomeActivity.class);
                                        //Delete the event using the id
                                        mDatabaseHelper.deleteEventById(eventId);
                                        mDatabaseHelper.deleteCommentsById(eventId);
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


    //Mark event function
    // ---------------------------------------------------------------------------------------------------
    @SuppressLint("SetTextI18n")
    private void markEvent() {
        boolean marked = mDatabaseHelper.checkParticipant(currentUser, eventId);
        if(marked)
            btnMark.setText("Marked");
        else
            btnMark.setText("Mark");

        btnMark.setOnClickListener(new View.OnClickListener() {
            boolean marked = mDatabaseHelper.checkParticipant(currentUser, eventId);
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                if(marked) {
                    mDatabaseHelper.deleteParticipantEntry(currentUser, eventId);
                    btnMark.setText("Mark");
                    finish();
                    startActivity(getIntent());
                }
                else {
                    mDatabaseHelper.addParticipant(currentUser, eventId);
                    btnMark.setText("Marked");

                    DBParticipant part = new DBParticipant(currentUser, eventId);

                    int min = 1 ;
                    int max = 999_999 ;
                    int randomNum = ThreadLocalRandom.current().nextInt( min , max + 1 );

                    mDatabase.child("participants").child(String.valueOf(randomNum)).setValue(part);
                    finish();
                    startActivity(getIntent());
                }
            }
        });
    }


    //Comment function
    // ---------------------------------------------------------------------------------------------------
    private void commentOnEvent() {
        btnComment.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                EditText comment = findViewById(R.id.Comment_editText);
                String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                String commentString = comment.getText().toString().trim();
                if(!commentString.isEmpty()) {
                    mDatabaseHelper.addComment(eventId, currentUser, commentString, currentDateTimeString);

                    DBComment comm = new DBComment(eventId, currentUser, commentString, currentDateTimeString);
                    int min = 1 ;
                    int max = 999_999 ;
                    int randomNum = ThreadLocalRandom.current().nextInt( min , max + 1 );
                    mDatabase.child("comments").child(String.valueOf(randomNum)).setValue(comm);

                    finish();
                    startActivity(getIntent());
                }
            }
        });
    }


    //Display comments function
    private void displayComments() {
        Cursor comment = mDatabaseHelper.getCommentWithId(eventId);
        while(comment.moveToNext()) {
            //Get the data from the database
            commentTime = comment.getString(3);
            content = comment.getString(2);
            commentAuthor = mDatabaseHelper.getItemName(comment.getString(1));

            //Add the events to the listView
            commentsList.add(0, new Event(commentAuthor, content, commentTime, String.valueOf(eventId), 1));
        }
        EventsAdapter eAdapter = new EventsAdapter(this, commentsList);
        commentsListView.setAdapter(eAdapter);
        EventsActivity.setListViewHeightBasedOnChildren(commentsListView);
    }


    //Delete comment function
    // ---------------------------------------------------------------------------------------------------
    private void deleteComment() {
        commentsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent,View view,int position,long id)
            {   TextView time = findViewById(R.id.Author_textView);
                final String timeE = time.getText().toString().trim();

                AlertDialog.Builder pictureDialog = new AlertDialog.Builder((Context) EventDisplayActivity.this,R.style.AlertDialogCustom);
                pictureDialog.setTitle("Do you want to delete comment?");
                String[] pictureDialogItems = {
                        "Yes",
                        "No"};
                pictureDialog.setItems(pictureDialogItems,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        mDatabaseHelper.deleteCommentByUser(currentUser, eventId,timeE );
                                        finish();
                                        startActivity(getIntent());
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
}
