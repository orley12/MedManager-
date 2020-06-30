package com.example.android.med_manager.profile;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
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
import com.example.android.med_manager.model.User;
import com.example.android.med_manager.sync.NotificationScheduler;
import com.example.android.med_manager.ui.LoginActivity;
import com.example.android.med_manager.utilities.PreferenceUtils;
import com.example.android.med_manager.utilities.UserUtils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import static com.example.android.med_manager.data.MedContract.ProfileEntry.PROFILE_DB_DEFAULT_ID;
import static com.example.android.med_manager.utilities.LoginUtils.getUserDataFormDataBase;
import static com.example.android.med_manager.utilities.UserUtils.getProfileContentValues;
import static com.example.android.med_manager.utilities.UserUtils.validateUserInput;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = ProfileActivity.class.getSimpleName();

    public static final String SIGN_UP = "sign_up";

    private String imageUri = "";

    private final int ACTIVITY_CHOOSE_FILE = 1001;

    EditText mFirstNameEditText;

    EditText mLastNameEditText;

    EditText mUsernameEditText;

    EditText mEmailEditText;

    TextView mMainNameEditText;

    TextView mAbbreviatedNameTextView;

    EditText mPassWordEditText;

    boolean mUserPhotoChanged = false;

//    private Context mContext = this;

    GoogleSignInClient mGoogleSignInClient;

//    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
//        @Override
//        public boolean onTouch(View view, MotionEvent motionEvent) {
//            mUserPhotoChanged = true;
//            return false;
//        }
//    };

    private boolean isSignedInUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initProperties();

//        mAbbreviatedNameTextView.setOnTouchListener(mTouchListener);

        setOnClickListenerOnAbbreviatedNameTextView();

        isSignedInUser = getIntent().hasExtra(SIGN_UP);

        if (!isSignedInUser){
            setUpActivityForSignedInUser();
        } else {
            setTitle(R.string.signup);
        }

        GoogleSignInOptions mGoogleSignInOptions = setupGoogleSignInOptions();

        mGoogleSignInClient = GoogleSignIn.getClient(this, mGoogleSignInOptions);
    }

    private void initProperties() {
        mFirstNameEditText = (EditText) findViewById(R.id.first_name);

        mLastNameEditText = (EditText) findViewById(R.id.last_name);

        mUsernameEditText = (EditText) findViewById(R.id.user_name);

        mEmailEditText = (EditText) findViewById(R.id.email);

        mMainNameEditText = (TextView) findViewById(R.id.main_name);

        mPassWordEditText = (EditText) findViewById(R.id.password);

        mAbbreviatedNameTextView = (TextView) findViewById(R.id.abbreviated_name);
    }

    private void setOnClickListenerOnAbbreviatedNameTextView() {
        mAbbreviatedNameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUserPhotoChanged = true;

                Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);

                chooseFile.setType("image/jpeg | image/png");
                chooseFile.addCategory(Intent.CATEGORY_OPENABLE);

                startActivityForResult(chooseFile, ACTIVITY_CHOOSE_FILE);
            }
        });
    }

    private void setUpActivityForSignedInUser() {
        int id = (int) PreferenceUtils.getLoggedInUser(this);

        Cursor cursor = getUserDataFormDataBase(ProfileActivity.this, PROFILE_DB_DEFAULT_ID, String.valueOf(id));

        User user = UserUtils.getUserObject(cursor);

        setViewProperties(user);
    }

    private void setViewProperties(User user) {
        mFirstNameEditText.setText(user.getFirstName());
        mLastNameEditText.setText(user.getLastName());
        mUsernameEditText.setText(user.getUsername());
        mEmailEditText.setText(user.getEmail());
        mPassWordEditText.setText(user.getPassword());
        if (user.getPhotoUrl().isEmpty()) {
            mAbbreviatedNameTextView.setText(String.format("%s %s", user.getFirstName().substring(0, 1), user.getLastName().substring(0, 1)));
            mAbbreviatedNameTextView.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        } else {
            setUserImage(Uri.parse(user.getPhotoUrl()));
        }
    }

    @NonNull
    private GoogleSignInOptions setupGoogleSignInOptions() {
        return new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
    }

    public void updateProfile() {

        User user = getViewProperties();

        ContentValues contentValues = getProfileContentValues(user, mUserPhotoChanged, imageUri);

        long id =  PreferenceUtils.getLoggedInUser(this);

        ProfileRepository.updateProfile(this, contentValues, id);

        finish();
    }

    private User getViewProperties(){
        String firstName = mFirstNameEditText.getText().toString().trim();
        String lastName = mLastNameEditText.getText().toString().trim();
        String username = mUsernameEditText.getText().toString().trim();
        String email = mEmailEditText.getText().toString().trim();
        String password = mPassWordEditText.getText().toString().trim();

        return new User(null, email, firstName, lastName, null, username, password);

    }

    public void insertProfile() {
        User user = getViewProperties();

        if (validateUserInput(ProfileActivity.this, user)) return;

        ContentValues contentValues = getProfileContentValues(user, mUserPhotoChanged, imageUri);

        ProfileRepository.insertProfile(ProfileActivity.this, contentValues);

        lunchLoginActivity();

        Toast.makeText(ProfileActivity.this,
                "Please log in with your credentials",
                Toast.LENGTH_SHORT).show();
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
                if (!isSignedInUser){
                    updateProfile();
                }else {
                    insertProfile();
                }
                return true;
            case R.id.action_log_out:
                mGoogleSignInClient.signOut();
                PreferenceUtils.setLoggedOut(this);
                NotificationScheduler.unRegisterAlarms(this);
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if(isSignedInUser){
            MenuItem menuItem = menu.findItem(R.id.action_log_out);
            menuItem.setVisible(false);
        }
        return true;
    }
}
