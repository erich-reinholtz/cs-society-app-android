package com.cssapp.cssapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

import static com.cssapp.cssapp.CreateProfileActivity.getBitmapAsByteArray;

public class EditEventActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private ImageView imageview;
    private static final String IMAGE_DIRECTORY = "/CSSapp";
    private int GALLERY = 1, CAMERA = 2;
    private Bitmap image;
    private Integer currentUser;
    private CheckBox checkSports;
    private CheckBox checkCoding;
    private CheckBox checkDrinks;
    private CheckBox checkFood;
    private CheckBox checkGames;
    private CheckBox checkMovies;
    private CheckBox checkCSS;
    private CheckBox checkPlanning;
    private String name;
    private String date;
    private String time;
    private String location;
    private String description;
    private String duration;
    private String[] tags = new String[8];
    private  String[] newTags = new String[8];
    private DatabaseHelper mDatabaseHelper;
    private String eventId;
    private EditText eName;
    private EditText eDate;
    private EditText eTime;
    private EditText eLocation;
    private EditText eDescription;
    private EditText eDuration;
    private String nameEntry;
    private String dateEntry;
    private String timeEntry;
    private String locationEntry;
    private String descriptionEntry;
    private String durationEntry;
    private Button btnSave;
    private String permission;
    Context context = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);

        //Set theme
        if(MembersActivity.getSearch("theme",this) != null)
            if ( MembersActivity.getSearch("theme",this).equals("1")) {
                setTheme(R.style.DarkAppTheme);
            }
            else setTheme(R.style.AppTheme);


        // Get event's id from EventDisplayActivity
        Intent intent = getIntent();
        eventId = intent.getStringExtra("EXTRA_MESSAGE");

        initializeValues();
        setFont();

        getDataFromDatabase();
        displayDataFromDB();

        //When btnSave is clicked:
        btnSaveListener();
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
        currentUser = Integer.parseInt(CreateProfileActivity.getDefaults("String_key", EditEventActivity.this));
        checkSports = findViewById(R.id.Sports_checkBox);
        checkCoding = findViewById(R.id.Coding_checkBox);
        checkDrinks = findViewById(R.id.Drinks_checkBox);
        checkFood = findViewById(R.id.Food_checkBox);
        checkGames = findViewById(R.id.Games_checkBox);
        checkMovies = findViewById(R.id.Movies_checkBox);
        checkCSS = findViewById(R.id.CSS_checkBox);
        checkPlanning = findViewById(R.id.Planning_checkBox);
        imageview = findViewById(R.id.Event_imageView);
        mDatabaseHelper = new DatabaseHelper(this);
        eName = findViewById(R.id.Name_editText);
        eDate = findViewById(R.id.Date_editText);
        eTime = findViewById(R.id.Time_editText);
        eLocation = findViewById(R.id.Location_editText);
        eDescription = findViewById(R.id.Description_editText);
        eDuration = findViewById(R.id.Duration_editText);
        btnSave =  findViewById(R.id.Save_button);
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


    //Get data from database
    // ---------------------------------------------------------------------------------------------------
    private void getDataFromDatabase() {
        Cursor data = mDatabaseHelper.getEventWithId(Integer.parseInt(eventId)) ;
        while(data.moveToNext())  {
            name = data.getString(1);
            date = data.getString(3);
            time = data.getString(4);
            location = data.getString(5);
            description = data.getString(6);
            duration = data.getString(7);
        }

        Cursor data2 = mDatabaseHelper.getEventTags(Integer.parseInt(eventId));
        int i = 0;
        while(data2.moveToNext()) {
            tags[i] = data2.getString(1);
            i++;
        }
    }


    //Display data from database
    // ---------------------------------------------------------------------------------------------------
    private void displayDataFromDB() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        long sum = 0;
        try {
            sum = format.parse(time).getTime() + format.parse(duration).getTime() + 7200000;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        duration = format.format(sum);

        eName.setHint(name);
        eDate.setHint(date);
        eTime.setHint(time);
        eLocation.setHint(location);
        eDescription.setHint(description);
        eDuration.setHint(duration);

        for(String tag: tags)
            if(tag != null) {
                if (tag.equals("Coding"))
                    checkCoding.setChecked(true);
                if (tag.equals("Games"))
                    checkGames.setChecked(true);
                if (tag.equals("Movies"))
                    checkMovies.setChecked(true);
                if (tag.equals("Food"))
                    checkFood.setChecked(true);
                if (tag.equals("Drinks"))
                    checkDrinks.setChecked(true);
                if (tag.equals("Sports"))
                    checkSports.setChecked(true);
                if (tag.equals("CSS"))
                    checkCSS.setChecked(true);
                if (tag.equals("CSS planning"))
                    checkPlanning.setChecked(true);
            }
        Bitmap img = mDatabaseHelper.getEventImage(Integer.parseInt(eventId));
        imageview.setImageBitmap(img);
    }


    //Get user's input data
    // ---------------------------------------------------------------------------------------------------
    private void getInputData() {
        nameEntry = eName.getText().toString();
        dateEntry = eDate.getText().toString();
        timeEntry = eTime.getText().toString();
        locationEntry = eLocation.getText().toString();
        descriptionEntry = eDescription.getText().toString();
        durationEntry = eDuration.getText().toString();

        if(nameEntry.isEmpty())
            nameEntry = name;

        if(dateEntry.isEmpty())
            dateEntry = date;

        if(timeEntry.isEmpty())
            timeEntry= time;

        if(locationEntry.isEmpty())
            locationEntry = location;

        if(descriptionEntry.isEmpty())
            descriptionEntry = description;

        if(durationEntry.isEmpty())
            durationEntry = duration;

        if(checkSports.isChecked())
            newTags[0] = "Sports";

        if(checkCoding.isChecked())
            newTags[1] = "Coding";

        if(checkDrinks.isChecked())
            newTags[2] = "Drinks";

        if(checkFood.isChecked())
            newTags[3] = "Food";

        if(checkGames.isChecked())
            newTags[4] = "Games";

        if(checkMovies.isChecked())
            newTags[5] = "Movies";

        if(checkCSS.isChecked())
            newTags[6] = "CSS";

        if(checkPlanning.isChecked())
            newTags[7] = "CSS planning";

        image= ((BitmapDrawable)imageview.getDrawable()).getBitmap();
    }


    //When btnSave is clicked:
    // ---------------------------------------------------------------------------------------------------
    private void btnSaveListener() {
        btnSave.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                getInputData();

                //CASE: Image size is ok
                if(image.getWidth() <= 2048 && image.getHeight() <= 1080 ) {
                    //Add data to database
                    byte[] data = getBitmapAsByteArray(image);
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
                    long sum = 0;
                    try {
                        sum = format.parse(durationEntry).getTime() - format.parse(timeEntry).getTime() - 7200000;
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    durationEntry = format.format(sum);

                    mDatabaseHelper.updateEvent(Integer.parseInt(eventId), nameEntry, dateEntry, timeEntry, locationEntry, descriptionEntry, newTags, durationEntry, data);
                    //Start EventDisplayActivity
                    Intent intent = new Intent(EditEventActivity.this, EventDisplayActivity.class);
                    intent.putExtra("EXTRA_MESSAGE", eventId);
                    Toast.makeText(EditEventActivity.this, "Changes saved", Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                    finish();
                }
                //CASE: Image is too big
                else
                    Toast.makeText(EditEventActivity.this, "Image too big. Please choose a smaller one.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    //Check user's permission and apply visibility
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


    //When the back button is pressed:
    // ---------------------------------------------------------------------------------------------------
    public void onBackPressed() {
        Intent intent = new Intent(this, EventDisplayActivity.class);
        intent.putExtra("EXTRA_MESSAGE", eventId);
        startActivity(intent);
        finish();
    }


    /**
     Calendar and clock functions
     -----------------------------------------------------------------------------------------------------
     */
    public void startTime(View v){
        final Calendar c = Calendar.getInstance();
        final int mhour = c.get(Calendar.HOUR_OF_DAY);
        final int mminute = c.get(Calendar.MINUTE);
        TimePickerDialog tpd = new TimePickerDialog(context,R.style.TimeDialogCustom, new TimePickerDialog.OnTimeSetListener(){
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                String time1 = timeConversion(hourOfDay)+ ":" + timeConversion(minute) + ":00";
                eTime.setText(time1);

            }
        },mhour,mminute, true);
        tpd.setTitle("Start time:");
        tpd.show();


    }


    public void endTime(View v){
        final Calendar c = Calendar.getInstance();
        final int mhour = c.get(Calendar.HOUR_OF_DAY);
        final int mminute = c.get(Calendar.MINUTE);
        TimePickerDialog tpd = new TimePickerDialog(context,R.style.TimeDialogCustom, new TimePickerDialog.OnTimeSetListener(){
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                String time1 = timeConversion(hourOfDay)+ ":" + timeConversion(minute) + ":00";
                eDuration.setText(time1);

            }
        },mhour,mminute, true);
        tpd.setTitle("End time:");
        tpd.show();
    }


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


    public void showDatePickerDialog(View v){
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }


    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {}


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String date = Integer.toString(dayOfMonth) + "/" + Integer.toString(month + 1) + "/" + Integer.toString(year);
        eDate.setText(date);

    }


    /**
     Picture functions
     -----------------------------------------------------------------------------------------------------
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
                    Toast.makeText(EditEventActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
                    imageview.setImageBitmap(bitmap);
                    // DATABASE IMAGE
                    image = bitmap;
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(EditEventActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (requestCode == CAMERA) {
            Bitmap thumbnail = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
            imageview.setImageBitmap(thumbnail);
            saveImage(thumbnail);
            // DATABASE IMAGE
            image = thumbnail;
            Toast.makeText(EditEventActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
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
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath());

            f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}
