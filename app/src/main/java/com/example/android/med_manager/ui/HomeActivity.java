package com.example.android.med_manager.ui;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.FloatingActionButton;
import android.support.test.espresso.IdlingResource;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.android.med_manager.R;
import com.example.android.med_manager.customViews.IgnoreButton;
import com.example.android.med_manager.customViews.TakenButton;
import com.example.android.med_manager.data.MedContract.MedEntry;
import com.example.android.med_manager.data.MedDbHelper;
import com.example.android.med_manager.detail.DetailActivity;
import com.example.android.med_manager.idlingResource.SimpleIdlingResource;
import com.example.android.med_manager.profile.ProfileActivity;
import com.example.android.med_manager.sync.MedReminderIntentService;
import com.example.android.med_manager.sync.NotificationScheduler;
import com.example.android.med_manager.sync.ReminderTasks;
import com.example.android.med_manager.utilities.RecyclerItemClickListener;


public class HomeActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    public static final String TAG = HomeActivity.class.getSimpleName();

    private static final int MED_TASK_LOADER_ID = 1;

    MedListAdapter mMedListAdapter;

    RecyclerView mRecyclerView;

    LinearLayoutManager mLinearLayoutManager;

    MedDbHelper mMedDbHelper;

    long mId;

    FloatingActionButton mFloatingActionButton;

    // The Idling Resource which will be null in production.
    @Nullable
    private SimpleIdlingResource mIdlingResource;

    /**
     * Only called from test, creates and returns a new {@link SimpleIdlingResource}.
     */
    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (mIdlingResource == null) {
            mIdlingResource = new SimpleIdlingResource();
        }
        return mIdlingResource;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mMedListAdapter = new MedListAdapter(HomeActivity.this);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        mRecyclerView.setAdapter(mMedListAdapter);

        mMedDbHelper = new MedDbHelper(this);

        mFloatingActionButton = (FloatingActionButton) findViewById(R.id.fab);

        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, MedFormActivity.class);
                startActivity(intent);
            }
        });

        getSupportLoaderManager().initLoader(MED_TASK_LOADER_ID, null, this);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            // Called when a user swipes left or right on a ViewHolder
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                // Here is where you'll implement swipe to delete

                // COMPLETED (1) Construct the URI for the item to delete
                //[Hint] Use getTag (from the adapter code) to get the id of the swiped item
                // Retrieve the id of the task to delete
                mId = (long) viewHolder.itemView.getTag();
                NotificationScheduler.cancelReminder(HomeActivity.this, mId);

                // Build appropriate uri with String row id appended
                String stringId = Long.toString(mId);
                Uri uri = MedEntry.CONTENT_URI;
                uri = uri.buildUpon().appendPath(stringId).build();

                // COMPLETED (2) Delete a single row of data using a ContentResolver
                getContentResolver().delete(uri, null, null);

                // COMPLETED (3) Restart the loader to re-query for all tasks after a deletion
                getSupportLoaderManager().restartLoader(MED_TASK_LOADER_ID, null, HomeActivity.this);

            }
        }).attachToRecyclerView(mRecyclerView);

        mRecyclerView.addOnItemTouchListener(createItemClickListener(mRecyclerView));

        getIdlingResource();
    }


    public RecyclerItemClickListener createItemClickListener(final RecyclerView recyclerView) {
        return new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Cursor cursor = mMedListAdapter.entireCursorForHomeActivity();
                cursor.moveToPosition(position); // get to the right location in the cursor
                final long idIndex = cursor.getInt(cursor.getColumnIndexOrThrow(MedEntry.MED_DB_DEFAULT_ID));
                if (view instanceof TakenButton) {
                    Intent incrementTakenCountIntent = new Intent(HomeActivity.this, MedReminderIntentService.class);
                    incrementTakenCountIntent.setAction(ReminderTasks.ACTION_INCREMENT_MED_TAKEN_COUNT);
                    incrementTakenCountIntent.putExtra("id", idIndex);
                    HomeActivity.this.startService(incrementTakenCountIntent);
                } else if (view instanceof IgnoreButton){
                    Intent incrementTakenCountIntent = new Intent(HomeActivity.this, MedReminderIntentService.class);
                    incrementTakenCountIntent.setAction(ReminderTasks.ACTION_INCREMENT_MED_IGNORE_COUNT);
                    incrementTakenCountIntent.putExtra("id", idIndex);
                    HomeActivity.this.startService(incrementTakenCountIntent);
                } else {
                    Intent intent = new Intent(HomeActivity.this, DetailActivity.class);
                    Uri currentMedDataUri = returnCurrentMedDataUri(position);
                    intent.setData(currentMedDataUri);
                    startActivity(intent);
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {
                    Intent intent = new Intent(HomeActivity.this, MedFormActivity.class);
                    Uri currentMedDataUri = returnCurrentMedDataUri(position);
                    intent.setData(currentMedDataUri);
                    startActivity(intent);
                }
        });

}

    public Uri returnCurrentMedDataUri(int positionClicked) {
        Cursor cursor = mMedListAdapter.entireCursorForHomeActivity();
        cursor.moveToPosition(positionClicked);
        long idIndex = cursor.getLong(cursor.getColumnIndexOrThrow(MedEntry.MED_DB_DEFAULT_ID));
        Uri currentMedDataUri = ContentUris.withAppendedId(MedEntry.CONTENT_URI, idIndex);
        return currentMedDataUri;
    }

    @Override
    public boolean onCreatePanelMenu(int featureId, Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    public Cursor getListOfMedNames(String searchText) {
        String[] mProjection = {MedEntry.MED_DB_DEFAULT_ID,
                MedEntry.MED_COLUMN_NAME,
                MedEntry.MED_COLUMN_DOSAGE,
                MedEntry.MED_COLUMN_START_DATE,
                MedEntry.MED_COLUMN_END_DATE,
                MedEntry.MED_COLUMN_TYPE};

        String selection = MedEntry.MED_COLUMN_NAME + "=?";
        String[] selectionArgs = {String.valueOf(searchText)};

        Cursor cursor = getContentResolver().query(MedEntry.CONTENT_URI, mProjection, selection, selectionArgs, null);

        assert cursor != null;
        cursor.close();

        return cursor;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int optionSelected = item.getItemId();

        switch (optionSelected) {
            case R.id.action_profile:
                launchProfileActivity();
                break;
            case R.id.action_add_med:
                launchMedFormActivity();
                break;
            case R.id.action_delete_all_meds:
                deleteAllMedEntries();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void launchProfileActivity() {
        Intent launchProfileActivityIntent = new Intent(HomeActivity.this, ProfileActivity.class);
        startActivity(launchProfileActivityIntent);
    }

    private void launchMedFormActivity() {
        Intent launchMedFormActivityIntent = new Intent(HomeActivity.this, MedFormActivity.class);
        startActivity(launchMedFormActivityIntent);
    }

    private void deleteAllMedEntries() {
        int deleteResult = getContentResolver().delete(MedEntry.CONTENT_URI, null, null);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String[] projection = {
                MedEntry.MED_DB_DEFAULT_ID,
                MedEntry.MED_COLUMN_NAME,
                MedEntry.MED_COLUMN_TYPE,
                MedEntry.MED_COLUMN_DESCRIPTION,
                MedEntry.MED_COLUMN_DOSAGE,
                MedEntry.MED_COLUMN_INTERVAL,
                MedEntry.MED_COLUMN_START_DATE,
                MedEntry.MED_COLUMN_END_DATE,
                MedEntry.MED_COLUMN_TAKEN_COUNT,
                MedEntry.MED_COLUMN_IGNORE_COUNT
        };

        return new CursorLoader(
                this,
                MedEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mMedListAdapter.swapCursor(cursor);
            if (mIdlingResource != null) {
                mIdlingResource.setIdleState(true);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMedListAdapter.swapCursor(null);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
