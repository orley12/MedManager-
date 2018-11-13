package com.example.android.med_manager.ui;

import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.android.med_manager.R;
import com.example.android.med_manager.utilities.PreferenceUtils;
import com.google.android.gms.auth.api.signin.*;

import static com.example.android.med_manager.data.MedContract.ProfileEntry.*;
import static com.example.android.med_manager.utilities.LoginUtils.getUserData;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = ProfileActivity.class.getSimpleName();

    public static final String SIGN_UP = "sign_up";

    private String imageUri = "";

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

    private boolean isComingFromSignUp;

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
                Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);

                chooseFile.setType("image/jpeg | image/png");
                chooseFile.addCategory(Intent.CATEGORY_OPENABLE);

                startActivityForResult(chooseFile, ACTIVITY_CHOOSE_FILE);
            }
        });

        isComingFromSignUp = getIntent().hasExtra(SIGN_UP);

        if (!isComingFromSignUp){
            dbQueryForUsersData();
        }

        if (isComingFromSignUp){
            setTitle(R.id.signup);
        }

        GoogleSignInOptions mGoogleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, mGoogleSignInOptions);
    }

    private void dbQueryForUsersData() {


        int id = (int) PreferenceUtils.getLoggedInUser(this);
        Cursor cursor = getUserData(ProfileActivity.this, PROFILE_DB_DEFAULT_ID, String.valueOf(id));

        cursor.moveToFirst();
            String name = cursor.getString(cursor.getColumnIndexOrThrow(PROFILE_COLUMN_NAME));
            String surname = cursor.getString(cursor.getColumnIndexOrThrow(PROFILE_SURNAME_NAME));
            String username = cursor.getString(cursor.getColumnIndexOrThrow(PROFILE_USER_NAME));
            String email = cursor.getString(cursor.getColumnIndexOrThrow(PROFILE_COLUMN_EMAIL));
            String password = cursor.getString(cursor.getColumnIndexOrThrow(PROFILE_COLUMN_PASSWORD));
            mUserPhotoUri = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_PHOTO_URI));

            cursor.close();
            mNameEditText.setText(name);
            mSurnameEditText.setText(surname);
            mUsernameEditText.setText(username);
            mEmailEditText.setText(email);
            mPassWordEditText.setText(password);
            mMainNameEditText.setText(name);
            if (mUserPhotoUri.isEmpty()) {
                mAbbreviatedNameTextView.setText(name.substring(0, 1) + surname.substring(0, 1));
            } else {
                setUserImage(Uri.parse(mUserPhotoUri));
            }
    }

    public void dbUpdateUserData() {
        String name = mNameEditText.getText().toString().trim();
        String surname = mSurnameEditText.getText().toString().trim();
        String username = mUsernameEditText.getText().toString().trim();
        String email = mEmailEditText.getText().toString().trim();
        String password = mPassWordEditText.getText().toString().trim();

        ContentValues contentValues = new ContentValues();
        contentValues.put(PROFILE_COLUMN_NAME,name);
        contentValues.put(PROFILE_SURNAME_NAME,surname);
        contentValues.put(PROFILE_USER_NAME,username);
        contentValues.put(PROFILE_COLUMN_EMAIL,email);
        contentValues.put(PROFILE_COLUMN_PASSWORD,password);
        if (mUserPhotoChanged){
            contentValues.put(COLUMN_USER_PHOTO_URI, imageUri);
        }

        long id =  PreferenceUtils.getLoggedInUser(this);
        updateProfile(contentValues, id);

        finish();
    }

    private void updateProfile(ContentValues contentValues,long id) {
        String selection = PROFILE_DB_DEFAULT_ID + "=?" ;
        String[] selectionArgs = {String.valueOf(id)};
        getContentResolver().update(CONTENT_URI, contentValues, selection, selectionArgs);
    }

    public void dbInsertUserData() {
        String name = mNameEditText.getText().toString().trim();
        String surname = mSurnameEditText.getText().toString().trim();
        String username = mUsernameEditText.getText().toString().trim();
        String email = mEmailEditText.getText().toString().trim();
        String password = mPassWordEditText.getText().toString().trim();

        Cursor cursor = getUserData(ProfileActivity.this, PROFILE_COLUMN_EMAIL, email);
        if ((cursor.getCount() > 0)) {
            Toast.makeText(ProfileActivity.this, "Your account already exist, please login",
                    Toast.LENGTH_SHORT).show();
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(PROFILE_COLUMN_NAME, name);
        contentValues.put(PROFILE_SURNAME_NAME, surname);
        contentValues.put(PROFILE_USER_NAME, username);
        contentValues.put(PROFILE_COLUMN_EMAIL, email);
        contentValues.put(PROFILE_COLUMN_PASSWORD, password);
        if (mUserPhotoChanged){
            contentValues.put(COLUMN_USER_PHOTO_URI, imageUri);
        }

        insertProfile(contentValues);

        lunchLoginActivity();

        Toast.makeText(ProfileActivity.this,
                "Please log in with your credentials",
                Toast.LENGTH_SHORT).show();
    }

    private long insertProfile(ContentValues contentValues) {
        Uri returnedUri = getContentResolver().insert(CONTENT_URI,contentValues);
        return ContentUris.parseId(returnedUri);
    }

    private void lunchLoginActivity() {
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.putExtra(LoginActivity.PROFILE, "profile");
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == ACTIVITY_CHOOSE_FILE && resultCode == Activity.RESULT_OK){

            Uri uri = data.getData();

            assert uri != null;
            imageUri = uri.toString();

            setUserImage(uri);
        } else {
            Toast.makeText(ProfileActivity.this,
                    "Sorry we were unable to load your image, please try again",
                    Toast.LENGTH_SHORT).show();
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
                if (!isComingFromSignUp){
                    dbUpdateUserData();
                }else {
                    dbInsertUserData();
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

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if(isComingFromSignUp){
            MenuItem menuItem = menu.findItem(R.id.action_log_out);
            menuItem.setVisible(false);
        }
        return true;
    }
}
