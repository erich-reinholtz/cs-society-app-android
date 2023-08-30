package com.cssapp.cssapp;

import android.app.AlertDialog;
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
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.res.ResourcesCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Objects;

import static com.cssapp.cssapp.CreateProfileActivity.getBitmapAsByteArray;


public class EditProfileActivity extends AppCompatActivity {
    private ImageView imageview;
    private static final String IMAGE_DIRECTORY = "/CSSapp";
    private int GALLERY = 1, CAMERA = 2;

    private Typeface myFont;
    private DatabaseHelper mDatabaseHelper;
    private Bitmap image;
    private String UserId;
    private Integer currentUser;
    private String[] years;
    private Spinner spinner;
    private CheckBox checkSports;
    private CheckBox checkCoding;
    private CheckBox checkDrinks;
    private CheckBox checkFood;
    private CheckBox checkGames;
    private CheckBox checkMovies;
    private String name;
    private String surname;
    private String userYear;
    private String[] tags = new String[6];
    private EditText uName;
    private EditText uSurname;
    private String[] changedTags = new String[6];
    private String nameEntry;
    private String surnameEntry;
    private String yearSelection;
    private Integer year;
    private Button btnSave;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if(MembersActivity.getSearch("theme",this) != null)
            if ( MembersActivity.getSearch("theme",this).equals("1")) {
                setTheme(R.style.DarkAppTheme);
            }
            else setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        //Receive UserId from ProfileActivity
        Intent intent = getIntent();
        UserId = intent.getStringExtra("EXTRA_MESSAGE");

        initializeValues();
        setFont();
        getDataFromDB();
        displayDataFromDB();
        //When btnSave is clicked:
        btnSaveListener();
    }
    /**
     Utilities Functions
     ---------------------------------------------------------------------------------------------------
     */
    //Initialize values
    // ---------------------------------------------------------------------------------------------------
    private void initializeValues() {
        currentUser = Integer.parseInt(CreateProfileActivity.getDefaults("String_key", EditProfileActivity.this));
        imageview = findViewById(R.id.ProfilePicture_imageView);
        myFont = ResourcesCompat.getFont(this, R.font.open_sans_light);
        years = getResources().getStringArray(R.array.academicYear_array);
        spinner =findViewById(R.id.ChooseYear_spinner);
        checkSports = findViewById(R.id.Sports_checkBox);
        checkCoding = findViewById(R.id.Coding_checkBox);
        checkDrinks = findViewById(R.id.Drinks_checkBox);
        checkFood = findViewById(R.id.Food_checkBox);
        checkGames = findViewById(R.id.Games_checkBox);
        checkMovies = findViewById(R.id.Movies_checkBox);
        mDatabaseHelper = new DatabaseHelper(this);
        uName = findViewById(R.id.FirstName_editText);
        uSurname =findViewById(R.id.LastName_editText);
        btnSave = findViewById(R.id.Save_button);
    }


    //Set font
    // ---------------------------------------------------------------------------------------------------
    private void setFont() {
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


    //Font adapter for spinner
    // ---------------------------------------------------------------------------------------------------
    class CustomAdapter<T> extends ArrayAdapter<T> {
        CustomAdapter(Context context, int textViewResourceId,
                      T[] objects) {
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


    //Get data from database
    // ---------------------------------------------------------------------------------------------------
    private void getDataFromDB() {
        //Get data from database
        Cursor data = mDatabaseHelper.getDataById(Integer.parseInt(UserId));
        while(data.moveToNext()) {
            name = data.getString(1);
            surname = data.getString(2);
            userYear = data.getString(3);
        }
        Cursor data2 = mDatabaseHelper.getTagMatchesById(Integer.parseInt(UserId));
        int i = 0;
        while(data2.moveToNext()) {
            tags[i] = data2.getString(1);
            i++;
        }
    }


    //Display data from database
    // ---------------------------------------------------------------------------------------------------
    private void displayDataFromDB() {
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
            }

        uName.setHint(name);
        uSurname.setHint(surname);
        spinner.setSelection(Integer.parseInt(userYear));
        final Bitmap img = mDatabaseHelper.getImage(Integer.parseInt(UserId));
        imageview.setImageBitmap(img);
    }


    //Get user's input data
    // ---------------------------------------------------------------------------------------------------
    private void getInputData() {
        // Grabbing entries of the form
        nameEntry = uName.getText().toString();
        surnameEntry = uSurname.getText().toString();
        yearSelection = spinner.getSelectedItem().toString();

        // Getting the last letter of the selection, which is the userYear number
        Integer length = yearSelection.length();
        Character lastChar = yearSelection.charAt(length - 1);


        // Converting the letter into an integer, the old-fashioned way
        if(!yearSelection.equals("Postgraduate"))
            year = lastChar - '0';
        else
            year = 5;

        // Getting tag bool value for Sports
        if(checkSports.isChecked())
            changedTags[0] = "Sports";

        if(checkCoding.isChecked())
            changedTags[1] = "Coding";

        if(checkDrinks.isChecked())
            changedTags[2] = "Drinks";

        if(checkFood.isChecked())
            changedTags[3] = "Food";

        if(checkGames.isChecked())
            changedTags[4] = "Games";

        if(checkMovies.isChecked())
            changedTags[5] = "Movies";

        if(nameEntry.isEmpty())
            nameEntry = name;

        if(surnameEntry.isEmpty())
            surnameEntry = surname;


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
                byte[] data = getBitmapAsByteArray(image);
                //CASE: Image size is ok
                if(image.getWidth() <= 2048 && image.getHeight() <= 1080 ) {
                    //Update DB
                    mDatabaseHelper.updateProfile(currentUser, nameEntry, surnameEntry,year,changedTags, data);
                    // Starting HomeActivity
                    Intent intent = new Intent(EditProfileActivity.this, ProfileActivity.class);
                    Toast.makeText(EditProfileActivity.this, "Changes saved", Toast.LENGTH_SHORT).show();
                    intent.putExtra("EXTRA_MESSAGE", String.valueOf(currentUser));
                    startActivity(intent);
                    finish();
                }
                //CASE: Image is too big
                else
                    Toast.makeText(EditProfileActivity.this, "Image too big. Please choose a smaller one.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    //Picture options
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


    //Case : choose from gallery
    // ---------------------------------------------------------------------------------------------------
    public void choosePhotoFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY);
    }


    //Case : choose from camera
    // ---------------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------------
    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }


    //Resolving the 2 cases
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
                    Toast.makeText(EditProfileActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
                    imageview.setImageBitmap(bitmap);
                    image = bitmap;

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(EditProfileActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {
            Bitmap thumbnail = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
            imageview.setImageBitmap(thumbnail);
            saveImage(thumbnail);
            image = thumbnail;
            Toast.makeText(EditProfileActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
        }
    }

    //Function for saving the image
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
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath());

            f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    //Starts ProfileActivity
    // ---------------------------------------------------------------------------------------------------
    public void goToProfile(View view) {
        Intent intent = new Intent(this, ProfileActivity.class);
        Toast.makeText(EditProfileActivity.this, "Saved", Toast.LENGTH_SHORT).show();
        startActivity(intent);
        finish();
    }


    //When the back button is pressed:
    // ---------------------------------------------------------------------------------------------------
    public void onBackPressed() {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra("EXTRA_MESSAGE", UserId);
        startActivity(intent);
        finish();
    }
}
