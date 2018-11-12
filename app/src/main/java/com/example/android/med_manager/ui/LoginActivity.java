package com.example.android.med_manager.ui;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.android.med_manager.R;
import com.example.android.med_manager.data.MedContract.MedEntry;
import com.example.android.med_manager.data.MedContract.ProfileEntry;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_CODE_SIGN_IN = 1;
    private static final String LOG_TAG = LoginActivity.class.getSimpleName();
    GoogleSignInClient mGoogleSigninClient;

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
        if (account != null) {
            launchHomeActivity();
        }
    }

    private void getUsersInfo(GoogleSignInAccount account) {
        if (account == null){
            Toast.makeText(this,"No Accout Info Found",Toast.LENGTH_SHORT);
        }else {
            String usersEmail = account.getEmail();
            String usersName = account.getGivenName();
            String usersFamilyName = account.getFamilyName();
            String usersId = account.getId();
            String usersDisplayName = account.getDisplayName();

            Log.i(LOG_TAG, "THE LOGS YOU LOOKING FOR" + usersEmail + usersName + usersFamilyName + usersId + usersDisplayName);

//            ContentValues contentValues = new ContentValues();
//            contentValues.put(ProfileEntry.PROFILE_COLUMN_EMAIL, "");
//            contentValues.put(ProfileEntry.PROFILE_COLUMN_NAME, "");
//            contentValues.put(ProfileEntry.PROFILE_SURNAME_NAME, "");
//            contentValues.put(ProfileEntry.PROFILE_ID_GOOGLE, "");
//            contentValues.put(ProfileEntry.PROFILE_USER_NAME, "");
//
//            Uri returnedUri = getContentResolver().insert(ProfileEntry.CONTENT_URI, contentValues);
//            long idValueOfParseUri = ContentUris.parseId(returnedUri);
//            Log.i(LOG_TAG, "THE ID YOU LOOKING FOR" + idValueOfParseUri);
//            insertValuesIntoDatabase(idValueOfParseUri, usersEmail, usersName, usersFamilyName, usersId, usersDisplayName);
//            launchHomeActivity();
        }
    }

    public void insertValuesIntoDatabase(long idIndex, String email, String givenName, String familyName, String Id
            , String displayName) {

        String selection = MedEntry.MED_DB_DEFAULT_ID + "=?";
        String[] selectionArgs = new String[]{String.valueOf(Long.toString(idIndex))};

        ContentValues contentValues = new ContentValues();
        contentValues.put(ProfileEntry.PROFILE_COLUMN_EMAIL, email);
        contentValues.put(ProfileEntry.PROFILE_COLUMN_NAME, givenName);
        contentValues.put(ProfileEntry.PROFILE_SURNAME_NAME, familyName);
        contentValues.put(ProfileEntry.PROFILE_ID_GOOGLE, Id);
        contentValues.put(ProfileEntry.PROFILE_USER_NAME, displayName);

        getContentResolver().update(ProfileEntry.CONTENT_URI, contentValues, selection, selectionArgs);
    }

    public void launchHomeActivity() {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
    }

    /*onClick on the  signIn button it calls the signIn() method */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sign_in_button:
                signIn();
        }
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
            if (account == null){
            }
        } catch (ApiException e) {
            e.printStackTrace();
            Log.w(LOG_TAG, "sigin failed code = " + e.getStatusCode());
            getUsersInfo(null);
        }

    }
}
