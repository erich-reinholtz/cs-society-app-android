package com.cssapp.cssapp;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;
import androidx.core.content.res.ResourcesCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

import android.app.TimePickerDialog;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateEventActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {
    private CheckBox checkSports;
    private CheckBox checkCoding;
    private CheckBox checkDrinks;
    private CheckBox checkFood;
    private CheckBox checkGames;
    private CheckBox checkMovies;
    private CheckBox checkCSS;
    private CheckBox checkPlanning;
    private EditText nameEvent;
    private EditText dateEvent;
    private EditText timeEvent;
    private EditText locationEvent;
    private EditText descriptionEvent;
    private EditText endTimeEvent;
    private String[] tags = new String[8];
    private DatabaseHelper mDatabaseHelper;
    private ImageView imageview;
    private static final String IMAGE_DIRECTORY = "/CSSapp";
    private int GALLERY = 1, CAMERA = 2;
    private Bitmap imgBitmap;
    private Integer currentUser;
    private Button btnCreate;
    private String nameEntry;
    private String dateEntry;
    private String timeEntry;
    private String locationEntry;
    private String descriptionEntry;
    private String durationEntry;
    private String permission;
    Context context = this;
    private Date date1;
    private Date date2;

    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        //Set theme
        if(MembersActivity.getSearch("theme",this) != null)
            if ( MembersActivity.getSearch("theme",this).equals("1")) {
                setTheme(R.style.DarkAppTheme);
            }
            else setTheme(R.style.AppTheme);

        initializeValues();
        setFont();
        //When btnCreate is clicked:
        buttonCreateListener();
        //Enable textWatcher
        textWatcher();
        //Apply visibility according to user's permission
        checkPermission();
    }
    /**
     Utilities Functions
     ---------------------------------------------------------------------------------------------------
     */
    //Initialize values
    // ---------------------------------------------------------------------------------------------------
    private void initializeValues() {
        currentUser = Integer.parseInt(CreateProfileActivity.getDefaults("String_key", CreateEventActivity.this));
        imageview = findViewById(R.id.event_imageView);
        nameEvent = findViewById(R.id.Name_editText);
        dateEvent = findViewById(R.id.Date_editText);
        timeEvent = findViewById(R.id.Time_editText);
        locationEvent = findViewById(R.id.Location_editText);
        descriptionEvent = findViewById(R.id.Description_editText);
        endTimeEvent = findViewById(R.id.event_duration);
        checkSports = findViewById(R.id.Sports_checkBox);
        checkCoding = findViewById(R.id.Coding_checkBox);
        checkDrinks = findViewById(R.id.Drinks_checkBox);
        checkFood = findViewById(R.id.Food_checkBox);
        checkGames = findViewById(R.id.Games_checkBox);
        checkMovies = findViewById(R.id.Movies_checkBox);
        checkCSS = findViewById(R.id.CSS_checkBox);
        checkPlanning = findViewById(R.id.CSSplanning_checkBox);
        mDatabaseHelper = new DatabaseHelper(this);
        btnCreate = findViewById(R.id.Save_button);
    }


    //Set font
    // ---------------------------------------------------------------------------------------------------
    private void setFont() {
        Typeface myFont = ResourcesCompat.getFont(this, R.font.open_sans_light);
        checkSports.setTypeface(myFont);
        checkCoding.setTypeface(myFont);
        checkDrinks.setTypeface(myFont);
        checkFood.setTypeface(myFont);
        checkGames.setTypeface(myFont);
        checkMovies.setTypeface(myFont);
        checkCSS.setTypeface(myFont);
        checkPlanning.setTypeface(myFont);

    }


    //Get user's input data
    // ---------------------------------------------------------------------------------------------------
    private void getData(){
        nameEntry = nameEvent.getText().toString();
        dateEntry = dateEvent.getText().toString();
        timeEntry = timeEvent.getText().toString();
        locationEntry = locationEvent.getText().toString();
        descriptionEntry = descriptionEvent.getText().toString();
       // durationEntry = endTimeEvent.getText().toString();

        if(checkSports.isChecked())
            tags[0] = "Sports";

        if(checkCoding.isChecked())
            tags[1] = "Coding";

        if(checkDrinks.isChecked())
            tags[2] = "Drinks";

        if(checkFood.isChecked())
            tags[3] = "Food";

        if(checkGames.isChecked())
            tags[4] = "Games";

        if(checkMovies.isChecked())
            tags[5] = "Movies";

        if(checkCSS.isChecked())
            tags[6] = "CSS";

        if(checkPlanning.isChecked())
            tags[7] = "CSS planning";
    }


    //When btnCreate is clicked:
    // ---------------------------------------------------------------------------------------------------
    private void buttonCreateListener() {
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                //Get data for the database
                getData();
                long difference = date2.getTime() - date1.getTime() - 7200000;
                @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
                durationEntry = format.format(difference);
                //CASE: Image size is ok
                if(imgBitmap.getWidth() <= 2048 && imgBitmap.getHeight() <= 1080 ) {
                    AddData(nameEntry, dateEntry, timeEntry, locationEntry, descriptionEntry, tags, durationEntry);
                    //Start HomeActivity
                    Intent intent = new Intent(CreateEventActivity.this, HomeActivity.class);
                    Toast.makeText(CreateEventActivity.this, "Event created", Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                    finish();
                }
                //CASE: Image is too big
                else
                    Toast.makeText(CreateEventActivity.this, "Image too big. Please choose a smaller one.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    //AddData to the database
    // ---------------------------------------------------------------------------------------------------
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void AddData(String name, String date, String time, String location, String description, String[] tags, String duration) {
        byte[] data = getBitmapAsByteArray(imgBitmap);

        int min = 1 ;
        int max = 999_999 ;
        int randomNum = ThreadLocalRandom.current().nextInt( min , max + 1 );

        mDatabaseHelper.addEvent(randomNum, name, currentUser, date, time, location, description, tags, duration, data);
        mDatabaseHelper.addEventTagsToCloud(randomNum, tags);

        DBEvent thisEvent = new DBEvent(randomNum, name, currentUser, date, time, location,
                description, duration, Base64.encodeToString(data, Base64.DEFAULT));
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("events").child(String.valueOf(randomNum)).setValue(thisEvent);
    }


    //Convert bitmap to byteArray
    // ---------------------------------------------------------------------------------------------------
    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }


    //Function that makes sure all the fields are completed
    // ---------------------------------------------------------------------------------------------------
    private void textWatcher() {
        TextWatcher required = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String name = nameEvent.getText().toString().trim();
                String date = dateEvent.getText().toString().trim();
                String time = timeEvent.getText().toString().trim();
                String location = locationEvent.getText().toString().trim();
                String description = descriptionEvent.getText().toString().trim();
                String duration = endTimeEvent.getText().toString().trim();

                btnCreate.setEnabled(!name.isEmpty() && !date.isEmpty() && !time.isEmpty() && !duration.isEmpty() && !location.isEmpty() && !description.isEmpty() && imageview.getDrawable() != null);

            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        nameEvent.addTextChangedListener(required);
        dateEvent.addTextChangedListener(required);
        timeEvent.addTextChangedListener(required);
        locationEvent.addTextChangedListener(required);
        descriptionEvent.addTextChangedListener(required);
        endTimeEvent.addTextChangedListener(required);

        imageview.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {

            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                String name = nameEvent.getText().toString().trim();
                String date = dateEvent.getText().toString().trim();
                String time = timeEvent.getText().toString().trim();
                String location = locationEvent.getText().toString().trim();
                String description = descriptionEvent.getText().toString().trim();
                String duration = endTimeEvent.getText().toString().trim();

                btnCreate.setEnabled(!name.isEmpty() && !date.isEmpty() && !time.isEmpty() && !duration.isEmpty() && !location.isEmpty() && !description.isEmpty() && imageview.getDrawable() != null);
            }
        });
    }


    //Check for permission and apply visibility
    // ---------------------------------------------------------------------------------------------------
    private void checkPermission() {
        Cursor per = mDatabaseHelper.getDataById(currentUser);
        while(per.moveToNext()) {
            permission = per.getString(5);
        }

        if(Integer.parseInt(permission) < 2) {
            checkCSS.setVisibility(View.GONE);
            checkPlanning.setVisibility(View.GONE);
        }
    }


    //Get event start time
    // ---------------------------------------------------------------------------------------------------
    public void startTime(View v){
        final Calendar c = Calendar.getInstance();
        final int mhour = c.get(Calendar.HOUR_OF_DAY);
        final int mminute = c.get(Calendar.MINUTE);
        TimePickerDialog tpd = new TimePickerDialog(context,R.style.TimeDialogCustom, new TimePickerDialog.OnTimeSetListener(){
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                String time1 = timeConversion(hourOfDay)+ ":" + timeConversion(minute) + ":00";
                        timeEvent.setText(time1);
                        try{
                            @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
                            date1 = format.parse(time1);

                        }catch (java.text.ParseException e) {
                            e.printStackTrace();
                        }
            }
        },mhour,mminute, true);
        tpd.setTitle("Start time:");
        tpd.show();
    }


    //Get event end time
    // ---------------------------------------------------------------------------------------------------
    public void endTime(View v){
        final Calendar c = Calendar.getInstance();
        final int mhour = c.get(Calendar.HOUR_OF_DAY);
        final int mminute = c.get(Calendar.MINUTE);
        TimePickerDialog tpd = new TimePickerDialog(context,R.style.TimeDialogCustom, new TimePickerDialog.OnTimeSetListener(){
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String time1 = timeConversion(hourOfDay)+ ":" + timeConversion(minute) + ":00";
                endTimeEvent.setText(time1);
                try{
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
                    date2 = format.parse(time1);

                }catch (java.text.ParseException e) {
                    e.printStackTrace();
                }
            }
        },mhour,mminute, true);
        tpd.setTitle("End time:");
        tpd.show();
    }


    //Convert time value to string
    // ---------------------------------------------------------------------------------------------------
    private String timeConversion(int time){
        String newTime;
        if(time < 10){
            newTime = "0" + Integer.toString(time);
        }
        else{
            newTime = Integer.toString(time);
        }
        return newTime;
    }


    //When the back button is pressed:
    // ---------------------------------------------------------------------------------------------------
    public void onBackPressed() {
        Intent intent = new Intent(this, EventsActivity.class);
        startActivity(intent);
        finish();
    }
    /**
     Calendar and clock functions
     -----------------------------------------------------------------------------------------------------
     **/
    public void showDatePickerDialog(View v){
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }


    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {}


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String date = Integer.toString(dayOfMonth) + "/" + Integer.toString(month + 1) + "/" + Integer.toString(year);
        dateEvent.setText(date);
    }
    /**
     Picture functions
     ---------------------------------------------------------------------------------------------------
     */
    public void showPictureDialog(View view){
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera" };
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallery();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }


    public void choosePhotoFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY);
    }


    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    Toast.makeText(CreateEventActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
                    imageview.setImageBitmap(bitmap);
                    // DATABASE IMAGE
                    imgBitmap = bitmap;
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(CreateEventActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (requestCode == CAMERA) {
            Bitmap thumbnail = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
            imageview.setImageBitmap(thumbnail);
            saveImage(thumbnail);
            // DATABASE IMAGE
            imgBitmap = thumbnail;
            Toast.makeText(CreateEventActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
        }
    }


    public void saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }

        try {
            File f = new File(wallpaperDirectory, Calendar.getInstance()
                    .getTimeInMillis() + ".jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(this,
                    new String[]{f.getPath()},
                    new String[]{"imgBitmap/jpeg"}, null);
            fo.close();
            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath());

            f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}
