package com.example.android.med_manager.ui;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.med_manager.R;
import com.example.android.med_manager.customViews.SignUpButton;
import com.example.android.med_manager.utilities.PreferenceUtils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import static com.example.android.med_manager.data.MedContract.ProfileEntry.*;
import static com.example.android.med_manager.utilities.LoginUtils.*;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String PROFILE = "profile";
    private static final int REQUEST_CODE_SIGN_IN = 1;
    private static final String TAG = LoginActivity.class.getSimpleName();

    GoogleSignInClient mGoogleSigninClient;

    Context context = LoginActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hindStatusBar();
        this.setContentView(R.layout.activity_login);
        /* the GoogleSignInOptions helps our this app request the user data required by this app for
         * example if while building the calling the Builder() method on the GoogleSignInOptions
         * we chain methods call of requestEmail(), requestProfile(), requestId() methods, the result
         * is that the data of the user returned to us form the sign in will be email,profile info
         * and requestId.
         * the Builder() methods takes in one params GoogleSignInOptions.DEFAULT_SIGN_IN, in other
         * to basic user info, to requst more info add the requestScopes*/
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .requestId()
                .build();
        /* here we create a GoogleSignInClient object by calling the getClient() method of
        *  GoogleSignIn object/class on the GoogleSignIn Object this returns a GoogleSignInClient
        *  object, the getClient() method takes in two parameters the activity implementing the
        *  GoogleSignInClient and the GoogleSignInOptions object.
        *  the GoogleSignInClient Object make available signIn functionality in our app .*/
        mGoogleSigninClient = GoogleSignIn.getClient(this, googleSignInOptions);

        /* gives reference to the SignInButton view on screen so we can attach an onclickListener
        to it */
        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(this);

        final EditText emailInputField = findViewById(R.id.username_edit_text);
        final EditText passwordInputField = findViewById(R.id.password_editText);

        Button mLoginButton = findViewById(R.id.login_button);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailInputField.getText().toString();
                String inputtedPassword = passwordInputField.getText().toString();
                authenticateUser(email, inputtedPassword);
            }
        });

        SignUpButton signUpButton = findViewById(R.id.email_sign_up_button);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchProfileActivity();
            }
        });
    }

    private void authenticateUser(String email, String inputtedPassword ){
        Cursor cursor = getUserData(context, PROFILE_COLUMN_EMAIL, email);

        if ((cursor.getCount() < 1)) {
            Toast.makeText(context, "Your account doesn't exist",
                    Toast.LENGTH_SHORT).show();
        } else {
            cursor.moveToFirst(); // get to the right location in the cursor
            String passwordInDatabase = cursor.getString(cursor.getColumnIndexOrThrow(PROFILE_COLUMN_PASSWORD));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(PROFILE_COLUMN_NAME));
            long userId = cursor.getInt(cursor.getColumnIndexOrThrow(PROFILE_DB_DEFAULT_ID));
            cursor.close();

            if(passwordInDatabase == null || passwordInDatabase.isEmpty()){
                Toast.makeText(context, "You don't have a password, Please login with Google",
                        Toast.LENGTH_SHORT).show();
            } else  {

                if ((comparePasswords(passwordInDatabase, inputtedPassword))) {
                    PreferenceUtils.setLoggedInUser(this, userId);
                    launchLoginActivity();
                    Toast.makeText(context, "Welcome " + name,
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Wrong password inputted",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    /*this method helps us hide the statusBar*/
    private void hindStatusBar() {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    protected void onStart() {
        super.onStart();
        /*within the onStart method we request if the user as signedIn by call the
        * getLastSignedInAccount() method on the GoogleSignIn object this will return a
        * GoogleSignInAccount object if the user is signedIn else it returns null if not null we
        * lunch the HomeActivity as parent activity*/
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        int id = (int) PreferenceUtils.getLoggedInUser(this);

        if (account != null) {
            launchLoginActivity();
        } else if(id != 0){
            launchLoginActivity();
        }
    }

    private void getUsersInfo(GoogleSignInAccount account) {
        if (account == null){
            Toast.makeText(this,"No Account Info Found",Toast.LENGTH_SHORT)
            .show();
        }else {
            String usersEmail = account.getEmail();
            String usersName = account.getGivenName();
            String usersFamilyName = account.getFamilyName();
            String usersId = account.getId();
            String usersPhotoUrl = (account.getPhotoUrl() != null) ? account.getPhotoUrl().toString() : "";
            String usersDisplayName = account.getDisplayName();

            Log.i(TAG, "THE LOGS YOU LOOKING FOR" + usersEmail + usersName +
                    usersFamilyName + usersId + usersDisplayName + "  \n   " + usersPhotoUrl);

            ContentValues contentValues = new ContentValues();
            contentValues.put(PROFILE_COLUMN_EMAIL, usersEmail);
            contentValues.put(PROFILE_COLUMN_NAME, usersName);
            contentValues.put(PROFILE_SURNAME_NAME, usersFamilyName);
            contentValues.put(PROFILE_ID_GOOGLE, usersId);
            contentValues.put(PROFILE_USER_NAME, usersDisplayName);
            contentValues.put(PROFILE_COLUMN_PASSWORD, "");
            contentValues.put(COLUMN_USER_PHOTO_URI, usersPhotoUrl);

            Uri returnedUri = getContentResolver().insert(CONTENT_URI, contentValues);
            PreferenceUtils.setLoggedInUser(this, ContentUris.parseId(returnedUri));

            launchLoginActivity();
        }
    }

    public void launchLoginActivity() {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
    }

    public void launchProfileActivity() {
        Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
        intent.putExtra(ProfileActivity.SIGN_UP, "signUp");
        startActivity(intent);
    }

    /*onClick on the  signIn button it calls the signIn() method */
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.sign_in_button) {
            signIn();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /*Here on the GoogleSignInClient we call the geSignInIntent() this will prompt the user to select
         * Google account to sign in with */
    private void signIn() {
        Intent signInIntent = mGoogleSigninClient.getSignInIntent();
        startActivityForResult(signInIntent, REQUEST_CODE_SIGN_IN);
    }

    /*after the user signsIn we get a GoogleSignInAccount object to display for the user */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            getUsersInfo(account);
            Toast.makeText(context, "Welcome " + account.getGivenName(),
                    Toast.LENGTH_SHORT).show();
        } catch (ApiException e) {
            e.printStackTrace();
            Toast.makeText(context, "signIn failed, ensure your internet connection is turned on" ,
                    Toast.LENGTH_SHORT).show();
            getUsersInfo(null);
        }

    }

    @Override
    public void onBackPressed() {
        if (!getIntent().hasExtra(PROFILE)){
            moveTaskToBack(true);
        } else {
            moveTaskToBack(false);
        }
    }
}
