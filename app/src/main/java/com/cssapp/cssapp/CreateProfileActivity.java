package com.cssapp.cssapp;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.res.ResourcesCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;


public class CreateProfileActivity extends AppCompatActivity {
    private ImageView picture;
    private static final String IMAGE_DIRECTORY = "/CSSapp";
    private int GALLERY = 1, CAMERA = 2;
    //------------------------------------
    private EditText FirstName;
    private EditText LastName;
    private Spinner spinner;
    private String[] userTags = new String[6];
    private Bitmap imageBitmap;
    private DatabaseHelper mDatabaseHelper;
    private Typeface myFont;
    private CheckBox checkSports;
    private CheckBox checkCoding;
    private CheckBox checkDrinks;
    private CheckBox checkFood;
    private CheckBox checkGames;
    private CheckBox checkMovies;
    private Button btnCreate;
    private String years[];
    private String googleId;
    private String nameEntry;
    private String forenameEntry;
    private String yearSelection;
    private Integer year;
    DatabaseReference mDatabase;
    String currentUser;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        //Set theme
        if(MembersActivity.getSearch("theme",this) != null)
            if ( MembersActivity.getSearch("theme",this).equals("1")) {
                setTheme(R.style.DarkAppTheme);
            }
            else setTheme(R.style.AppTheme);





        //Get googleId from LoginActivity and currentUser from SettingsActivity
        Intent intent = getIntent();
        currentUser = intent.getStringExtra("settings");
        googleId = intent.getStringExtra("EXTRA_MESSAGE");

        //Store the current googleId
        if(googleId != null)
            setGoogle("String_key",googleId, CreateProfileActivity.this);
        else
            googleId = getGoogle("String_key", CreateProfileActivity.this);

        //Assigning the values to the objects
        initializeValues();
        //Setting the font for spinner and checkboxes
        setFont();
        checkAccount();
        //When btnCreate is clicked :
        btnCreateListener();
        //Enable the text watcher function
        textWatcher();
    }


    //Adapter for setting the spinner font
    // ---------------------------------------------------------------------------------------------------
    class CustomAdapter<T> extends ArrayAdapter<T> {
        CustomAdapter(Context context, int textViewResourceId, T[] objects) {
            super(context, textViewResourceId, objects);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            ((TextView) view).setTypeface(myFont);
            return view;
        }

        public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
            View v =super.getDropDownView(position, convertView, parent);
            ((TextView) v).setTypeface(myFont);
            return v;
        }
    }


    /**
     Utilities Functions
     ---------------------------------------------------------------------------------------------------
     */
    //Assign values function
    // ---------------------------------------------------------------------------------------------------
    private void initializeValues(){
        myFont = ResourcesCompat.getFont(this, R.font.open_sans_light);
        picture =findViewById(R.id.ProfilePicture_imageView);
        years = getResources().getStringArray(R.array.academicYear_array);
        FirstName = findViewById(R.id.FirstName_editText);
        LastName = findViewById(R.id.LastName_editText);
        spinner = findViewById(R.id.ChooseYear_spinner);
        btnCreate = findViewById(R.id.Create_button);
        checkSports = findViewById(R.id.Sports_checkBox);
        checkCoding = findViewById(R.id.Coding_checkBox);
        checkDrinks = findViewById(R.id.Drinks_checkBox);
        checkFood = findViewById(R.id.Food_checkBox);
        checkGames = findViewById(R.id.Games_checkBox);
        checkMovies = findViewById(R.id.Movies_checkBox);
        mDatabaseHelper = new DatabaseHelper(this);
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }


    //Set font function
    // ---------------------------------------------------------------------------------------------------
    private void setFont(){
        CustomAdapter<CharSequence> adapter = new CustomAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, years);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
        checkSports.setTypeface(myFont);
        checkCoding.setTypeface(myFont);
        checkDrinks.setTypeface(myFont);
        checkFood.setTypeface(myFont);
        checkGames.setTypeface(myFont);
        checkMovies.setTypeface(myFont);
    }


    //Check if there is an account
    // ---------------------------------------------------------------------------------------------------
    private void checkAccount(){
        Integer id = -1;
        Cursor checkGoogleId = mDatabaseHelper.checkUserWithGoogle(googleId);
        while(checkGoogleId.moveToNext()){
            id = checkGoogleId.getInt(0);
        }

        if(id != -1) {
            setDefaults("String_key",String.valueOf(id), CreateProfileActivity.this);
            goToHome();
        }
    }


    //Get user's input data
    // ---------------------------------------------------------------------------------------------------
    private void getInputData() {
        // Grabbing entries of the form
        nameEntry = FirstName.getText().toString();
        forenameEntry = LastName.getText().toString();
        yearSelection = spinner.getSelectedItem().toString();

        // Getting the last letter of the selection, which is the year number
        Integer length = yearSelection.length();
        Character lastChar = yearSelection.charAt(length - 1);


        // Converting the letter into an integer, the old-fashioned way
        if(!yearSelection.equals("Postgraduate"))
            year = lastChar - '0';
        else
            year = 5;

        // Getting tag bool value for checkBoxes
        if(checkSports.isChecked())
            userTags[0] = "Sports";

        if(checkCoding.isChecked())
            userTags[1] = "Coding";

        if(checkDrinks.isChecked())
            userTags[2] = "Drinks";

        if(checkFood.isChecked())
            userTags[3] = "Food";

        if(checkGames.isChecked())
            userTags[4] = "Games";

        if(checkMovies.isChecked())
            userTags[5] = "Movies";
    }


    //When btnCreate is clicked:
    // ---------------------------------------------------------------------------------------------------
    private void btnCreateListener() {
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                getInputData();

                //CASE: Image size is ok
                if(imageBitmap.getWidth() <= 2048 && imageBitmap.getHeight() <= 1080 ) {
                    // Passing the entries on to AddData
                    if (FirstName.length() != 0 && LastName.length() != 0 && year != 0)
                        AddData(nameEntry, forenameEntry, year, userTags, googleId);


                    Integer id = -1;
                    Cursor checkGoogleId = mDatabaseHelper.checkUserWithGoogle(googleId);
                    while(checkGoogleId.moveToNext()){
                        id = checkGoogleId.getInt(0);
                    }
                    //Store the current user's value in SharedPreferences
                    //CASE: Profile was not deleted
                    if(!getCheck("String_key",CreateProfileActivity.this).equals("true"))
                        setDefaults("String_key",String.valueOf(id), CreateProfileActivity.this);
                    //CASE: Profile was deleted
                    else
                        setDefaults("String_key",currentUser, CreateProfileActivity.this);


                    // Starting HomeActivity
                    goToHome();
                    Toast.makeText(CreateProfileActivity.this, "Profile created", Toast.LENGTH_SHORT).show();
                }
                //CASE: Image size is too big
                else
                    Toast.makeText(CreateProfileActivity.this, "Image too big. Please choose a smaller one.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    //Text Watcher function
    // ---------------------------------------------------------------------------------------------------
    private void textWatcher(){
        TextWatcher creationWatcher = new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String firstName = FirstName.getText().toString().trim();
                String lastName = LastName.getText().toString().trim();

                btnCreate.setEnabled(!firstName.isEmpty() && !lastName.isEmpty() && spinner.getSelectedItemPosition() != 0 && picture.getDrawable() != null);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        FirstName.addTextChangedListener(creationWatcher);
        LastName.addTextChangedListener(creationWatcher);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                btnCreate.setEnabled(!FirstName.getText().toString().trim().isEmpty() && !LastName.getText().toString().trim().isEmpty() && position != 0 && picture.getDrawable() != null);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        picture.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {

            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                btnCreate.setEnabled(!FirstName.getText().toString().trim().isEmpty() && !LastName.getText().toString().trim().isEmpty() && spinner.getSelectedItemPosition() != 0 && picture.getDrawable() != null);
            }
        });
    }


    //Start HomeActivity
    // ---------------------------------------------------------------------------------------------------
    private void goToHome() {
        Intent intent = new Intent(CreateProfileActivity.this, HomeActivity.class);
        intent.putExtra("EXTRA_MESSAGE", "create");
        startActivity(intent);
    }


    //When back button is pressed: Asked if you want to exit the app
    // ---------------------------------------------------------------------------------------------------
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


    /**
     Database Functions
     ---------------------------------------------------------------------------------------------------
     */
    //Add data to database
    // ---------------------------------------------------------------------------------------------------
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void AddData(String newEntry, String newEntry2, Integer yearEntry, String[] tags, String googleId) {
        byte[] data = getBitmapAsByteArray(imageBitmap);


        int min = 1 ;
        int max = 999_999 ;
        int randomNum = ThreadLocalRandom.current().nextInt( min , max + 1 );

        DBUser dbuser = new DBUser(randomNum, newEntry, newEntry2, yearEntry, Base64.encodeToString(data, Base64.DEFAULT),
                0, googleId);

        //CASE: Profile was not deleted
        if(!getCheck("String_key",CreateProfileActivity.this).equals("true")) {
            mDatabaseHelper.addData(randomNum, newEntry, newEntry2, yearEntry, tags, data, 3,googleId );
            new FirebaseHelper(googleId, dbuser);
            mDatabaseHelper.addTagsToCloud(randomNum, tags);

        }
        //CASE: Profile was deleted
        else
           mDatabaseHelper.updateProfile(Integer.parseInt(currentUser), newEntry, newEntry2, yearEntry, tags, data);
    }


    //Convert bitmap to ByteArray
    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }


    /**
     Image functions
     ---------------------------------------------------------------------------------------------------
     */
    //Show picture dialog
    // ---------------------------------------------------------------------------------------------------
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


    //CASE: Choose from gallery
    // ---------------------------------------------------------------------------------------------------
    public void choosePhotoFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY);
    }


    //CASE: Choose from camera
    // ---------------------------------------------------------------------------------------------------
    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }


    //Function for resolving the 2 cases
    // ---------------------------------------------------------------------------------------------------
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
                    Toast.makeText(CreateProfileActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
                    picture.setImageBitmap(bitmap);
                    // Database imageBitmap
                    imageBitmap = bitmap;
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(CreateProfileActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (requestCode == CAMERA) {
            Bitmap thumbnail = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
            picture.setImageBitmap(thumbnail);
            saveImage(thumbnail);
            // Database imageBitmap
            imageBitmap = thumbnail;
            Toast.makeText(CreateProfileActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
        }
    }


    //Function for saving the imageBitmap
    // ---------------------------------------------------------------------------------------------------
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
                    new String[]{"imageBitmap/jpeg"}, null);
            fo.close();
            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath());

            f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }


    /**
     SharedPreferences functions
     ---------------------------------------------------------------------------------------------------
     */
    public static void setDefaults(String key, String value, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getDefaults(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, null);
    }

    public static void setGoogle(String key, String value, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getGoogle(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, null);
    }

    public static void setCheck(String key, String value, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getCheck(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, null);
    }



}
