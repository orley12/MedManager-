package com.example.android.med_manager.ui;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BaseTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.transition.Transition;
import com.example.android.med_manager.R;
import com.example.android.med_manager.data.MedContract.ProfileEntry;
import com.example.android.med_manager.utilities.FileUtils;
import com.example.android.med_manager.utilities.ImageUtils;
import com.example.android.med_manager.utilities.PreferenceUtils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import java.io.File;

import static com.example.android.med_manager.data.MedContract.ProfileEntry.COLUMN_USER_PHOTO_URI;
import static com.example.android.med_manager.data.MedContract.ProfileEntry.CONTENT_URI;
import static com.example.android.med_manager.data.MedContract.ProfileEntry.PROFILE_COLUMN_EMAIL;
import static com.example.android.med_manager.data.MedContract.ProfileEntry.PROFILE_COLUMN_NAME;
import static com.example.android.med_manager.data.MedContract.ProfileEntry.PROFILE_COLUMN_PASSWORD;
import static com.example.android.med_manager.data.MedContract.ProfileEntry.PROFILE_DB_DEFAULT_ID;
import static com.example.android.med_manager.data.MedContract.ProfileEntry.PROFILE_ID_GOOGLE;
import static com.example.android.med_manager.data.MedContract.ProfileEntry.PROFILE_SURNAME_NAME;
import static com.example.android.med_manager.data.MedContract.ProfileEntry.PROFILE_USER_NAME;

public class ProfileActivity extends AppCompatActivity {

    private static final String LOG_TAG = ProfileActivity.class.getSimpleName();

    public static final String SIGN_UP = "sign_up";

    private String mFilePath = "";

    private final int ACTIVITY_CHOOSE_FILE = 1001;

    EditText mNameEditText;

    EditText mSurnameEditText;

    EditText mUsernameEditText;

    EditText mEmailEditText;

    TextView mMainNameEditText;

    TextView mAbbreviatedNameTextView;

    EditText mPassWordEditText;

    private String mUserPhotoUri;

    boolean mUserPhotoChanged = false;

    private Context mContext = this;

    GoogleSignInClient mGoogleSignInClient;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mUserPhotoChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mNameEditText = (EditText) findViewById(R.id.first_name);

        mSurnameEditText = (EditText) findViewById(R.id.last_name);

        mUsernameEditText = (EditText) findViewById(R.id.user_name);

        mEmailEditText = (EditText) findViewById(R.id.email);

        mMainNameEditText = (TextView) findViewById(R.id.main_name);

        mPassWordEditText = (EditText) findViewById(R.id.password);

        mAbbreviatedNameTextView = (TextView) findViewById(R.id.abbreviated_name);

        mAbbreviatedNameTextView.setOnTouchListener(mTouchListener);

        mAbbreviatedNameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chooseFile;
                chooseFile = new Intent(Intent.ACTION_GET_CONTENT);

                chooseFile.setType("image/jpeg | image/png");
                chooseFile.addCategory(Intent.CATEGORY_OPENABLE);

                startActivityForResult(chooseFile, ACTIVITY_CHOOSE_FILE);
            }
        });

        if (!getIntent().hasExtra(SIGN_UP)){
            dbQueryForUsersData();
        }

        GoogleSignInOptions mGoogleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)

                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, mGoogleSignInOptions);
    }

    private void dbQueryForUsersData() {

        String[] projection = new String[]{
                PROFILE_DB_DEFAULT_ID,
                PROFILE_USER_NAME,
                PROFILE_COLUMN_EMAIL,
                PROFILE_SURNAME_NAME,
                PROFILE_COLUMN_NAME,
                PROFILE_ID_GOOGLE,
                PROFILE_COLUMN_PASSWORD,
                COLUMN_USER_PHOTO_URI
        };

        int id = (int) PreferenceUtils.getLoggedInUser(this);
        String selection = ProfileEntry.PROFILE_DB_DEFAULT_ID + "=?" ;
        String[] selectionArgs = {String.valueOf(id)};
        Cursor cursor = getContentResolver().query(ProfileEntry.CONTENT_URI, projection, selection,
                selectionArgs, null);
        String name = null;
        String surname = null;
        String username = null;
        String email = null;
        String googleId = null;
        String password = null;

        int count = 0;
        if (cursor.moveToFirst()) {
            name = cursor.getString(cursor.getColumnIndexOrThrow(PROFILE_COLUMN_NAME));
            surname = cursor.getString(cursor.getColumnIndexOrThrow(PROFILE_SURNAME_NAME));
            username = cursor.getString(cursor.getColumnIndexOrThrow(PROFILE_USER_NAME));
            email = cursor.getString(cursor.getColumnIndexOrThrow(PROFILE_COLUMN_EMAIL));
            googleId = cursor.getString(cursor.getColumnIndexOrThrow(PROFILE_ID_GOOGLE));
            password = cursor.getString(cursor.getColumnIndexOrThrow(PROFILE_COLUMN_PASSWORD));
            mUserPhotoUri = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_PHOTO_URI));
        }
        count = cursor.getCount();
        Log.i(LOG_TAG,"CURSOR COUNT" + count);

        cursor.close();
        mNameEditText.setText(name);
        mSurnameEditText.setText(surname);
        mUsernameEditText.setText(username);
        mEmailEditText.setText(email);
        mPassWordEditText.setText(password);
        mMainNameEditText.setText(name);
        if (mUserPhotoUri.isEmpty()|| mUserPhotoUri == "") {
            mAbbreviatedNameTextView.setText(name.substring(0, 1) + surname.substring(0, 1));
        }else if (mUserPhotoUri.contains("https:")) {
            Log.i(LOG_TAG,"Internet mUserPhotoUri" + Uri.parse(mUserPhotoUri));
            setUserImage(Uri.parse(mUserPhotoUri));
//            saveImageFromTheInternet(mUserPhotoUri, id);
        }else {
            Log.i(LOG_TAG,"mUserPhotoUri" + Uri.parse(mUserPhotoUri));
            setUserImage(Uri.parse(mUserPhotoUri));
        }
    }
    private void saveImageFromTheInternet(String mUserPhotoUri, int id) {
        if(mUserPhotoUri != null && !mUserPhotoUri.equals("") && id > 0) {
            Log.i(LOG_TAG, "I Was Called");

            Glide.with(this)
                    .load(mUserPhotoUri)
                    .into(mBaseTargetLoadIntoDb);
        }
    }
    BaseTarget mBaseTargetLoadIntoDb = new BaseTarget<Bitmap>() {

        @Override
        public void onResourceReady(@NonNull final Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    File file= FileUtils.saveImage(mContext, resource);

                    ContentValues values = new ContentValues();

                    Uri savedImageUri = Uri.fromFile(new File(file.getAbsolutePath()));
                    values.put(COLUMN_USER_PHOTO_URI, savedImageUri.toString());

                    String selection = ProfileEntry.PROFILE_DB_DEFAULT_ID + "=?" ;
                    String[] selectionArgs = {String.valueOf(PreferenceUtils.getLoggedInUser(mContext))};

                    mContext.getContentResolver().update(CONTENT_URI,values,selection,selectionArgs);
                }
            }).start();

        }

        @Override
        public void getSize(@NonNull SizeReadyCallback cb) {

        }

        @Override
        public void removeCallback(@NonNull SizeReadyCallback cb) {

        }
    };


    public void dbUpdateForUsersData() {
        String name = mNameEditText.getText().toString().trim();
        String surname = mSurnameEditText.getText().toString().trim();
        String username = mUsernameEditText.getText().toString().trim();
        String email = mEmailEditText.getText().toString().trim();
        String password = mPassWordEditText.getText().toString().trim();
        Uri profilePicUri = null;

        if (!mUserPhotoChanged){
            Log.i(LOG_TAG,"WE ARE GOOD");
            profilePicUri = Uri.parse(mUserPhotoUri);
//        } else if (FileUtils.getPath(this, Uri.parse(mUserPhotoUri))== mFilePath) {
//            Log.i(LOG_TAG, "WE ARE GOOD 2");
//            profilePicUri = Uri.parse(mUserPhotoUri);
        } else {
            File savedProfilePicsFile = FileUtils.saveImage(mContext, ImageUtils.getBitmapFromPath(mContext,mFilePath));
            profilePicUri = Uri.fromFile(new File(savedProfilePicsFile.getAbsolutePath()));
        }

    ContentValues contentValues = new ContentValues();
        contentValues.put(PROFILE_COLUMN_NAME,name);
        contentValues.put(PROFILE_SURNAME_NAME,surname);
        contentValues.put(PROFILE_USER_NAME,username);
        contentValues.put(PROFILE_COLUMN_EMAIL,email);
        contentValues.put(PROFILE_COLUMN_PASSWORD,password);
        contentValues.put(COLUMN_USER_PHOTO_URI, profilePicUri.toString());

        long id =  PreferenceUtils.getLoggedInUser(this);
        String selection = ProfileEntry.PROFILE_DB_DEFAULT_ID + "=?" ;
        String[] selectionArgs = {String.valueOf(id)};
        getContentResolver().update(ProfileEntry.CONTENT_URI,contentValues,selection,selectionArgs);

        finish();
    }

    public void dbInsertForUsersData() {
        String name = mNameEditText.getText().toString().trim();
        String surname = mSurnameEditText.getText().toString().trim();
        String username = mUsernameEditText.getText().toString().trim();
        String email = mEmailEditText.getText().toString().trim();
        String password = mPassWordEditText.getText().toString().trim();
        Uri profilePicUri = null;

        if(!mFilePath.equals("")){
            File savedProfilePicsFile = FileUtils.saveImage(mContext, ImageUtils.getBitmapFromPath(mContext,mFilePath));
            profilePicUri = Uri.fromFile(new File(savedProfilePicsFile.getAbsolutePath()));
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(PROFILE_COLUMN_NAME,name);
        contentValues.put(PROFILE_SURNAME_NAME,surname);
        contentValues.put(PROFILE_USER_NAME,username);
        contentValues.put(PROFILE_COLUMN_EMAIL,email);
        contentValues.put(PROFILE_COLUMN_PASSWORD,password);
        contentValues.put(COLUMN_USER_PHOTO_URI, profilePicUri.toString());

        Uri returnedUri = getContentResolver().insert(CONTENT_URI,contentValues);
        long currentLoggedInUsersId = ContentUris.parseId(returnedUri);

        PreferenceUtils.setLoggedOut(this);
        PreferenceUtils.setLoggedInUser(this, currentLoggedInUsersId);

        resumeHomeActivity();

    }

    private void resumeHomeActivity() {
        Intent intent = new Intent(mContext, HomeActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != RESULT_OK)
            return;

        if(requestCode == ACTIVITY_CHOOSE_FILE){

            Uri uri = data.getData();

             mFilePath = FileUtils.getPath(this, uri);

            Log.e(LOG_TAG, "onActivityResult: FilePath == " + mFilePath);

            if(uri != null) {
                setUserImage(uri);
            }
        }
    }

    private void setUserImage(Uri uri) {
        Glide.with(this)
        .load(uri)
        .apply(RequestOptions.bitmapTransform(new CircleCrop()))
        .into(new SimpleTarget<Drawable>() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                mAbbreviatedNameTextView.setBackground(resource);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int optionSelectedId = item.getItemId();
        switch (optionSelectedId) {
            case R.id.action_save:
                if (!getIntent().hasExtra(ProfileActivity.SIGN_UP)){
                    dbUpdateForUsersData();
                }else {
                    dbInsertForUsersData();
                }
                return true;
            case R.id.action_log_out:
                mGoogleSignInClient.signOut();
                PreferenceUtils.setLoggedOut(this);
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
