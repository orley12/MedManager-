package com.example.android.med_manager;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SearchView;

import com.example.android.med_manager.data.MedContract.MedEntry;
import com.example.android.med_manager.data.MedDbHelper;
import com.example.android.med_manager.sync.MedReminderIntentService;
import com.example.android.med_manager.sync.ReminderTasks;

public class HomeActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    public static final String LOG_TAG = HomeActivity.class.getSimpleName();

    private static final int MED_TASK_LOADER_ID = 0;

    public static long TIME_INTERVAL;

    MedListAdapter mMedListAdapter;

    RecyclerView mRecyclerView;

    LinearLayoutManager mLinearLayoutManager;

    MedDbHelper mMedDbHelper;

    int mId;

    SearchView mSearchView;

    FloatingActionButton mFloatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mMedListAdapter = new MedListAdapter(HomeActivity.this);

        mRecyclerView = findViewById(R.id.recycler_view);

        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        mRecyclerView.setAdapter(mMedListAdapter);

        mMedDbHelper = new MedDbHelper(this);

        mFloatingActionButton = findViewById(R.id.fab);

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
                mId = (int) viewHolder.itemView.getTag();

                // Build appropriate uri with String row id appended
                String stringId = Integer.toString(mId);
                Uri uri = MedEntry.CONTENT_URI;
                uri = uri.buildUpon().appendPath(stringId).build();

                // COMPLETED (2) Delete a single row of data using a ContentResolver
                getContentResolver().delete(uri, null, null);

                // COMPLETED (3) Restart the loader to re-query for all tasks after a deletion
                getSupportLoaderManager().restartLoader(MED_TASK_LOADER_ID, null, HomeActivity.this);

            }
        }).attachToRecyclerView(mRecyclerView);

        mRecyclerView.addOnItemTouchListener(createItemClickListener(mRecyclerView));

//        ReminderUtilities.scheduleMedNotificationReminder(HomeActivity.this);

    }

    public RecyclerItemClickListener createItemClickListener(final RecyclerView recyclerView) {
        return new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (view instanceof LinearLayout) {
                    Cursor cursor = mMedListAdapter.entireCursorForHomeActivity();
                    cursor.moveToPosition(position); // get to the right location in the cursor
                    final long idIndex = cursor.getInt(cursor.getColumnIndexOrThrow(MedEntry.MED_DB_DEFAULT_ID));
                    Intent incrementTakenCountIntent = new Intent(HomeActivity.this, MedReminderIntentService.class);
                    incrementTakenCountIntent.setAction(ReminderTasks.ACTION_INCREMENT_MED_TAKEN_COUNT);
                    incrementTakenCountIntent.putExtra("id", idIndex);
                    HomeActivity.this.startService(incrementTakenCountIntent);
                } else {
                        Intent intent = new Intent(HomeActivity.this, DetailActivity.class);
                        Bundle bundle = returnBundleForNewActivity(position);
                        intent.putExtra("bundle", bundle);
                        startActivity(intent);
                    Log.i(LOG_TAG, "CONTAINER :" + position);
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {
                Intent intent = new Intent(HomeActivity.this, MedFormActivity.class);
                Cursor cursor = mMedListAdapter.entireCursorForHomeActivity();
                cursor.moveToPosition(position);
                int idIndex = cursor.getInt(cursor.getColumnIndexOrThrow(MedEntry.MED_DB_DEFAULT_ID));
                Uri currentMedDataUri = ContentUris.withAppendedId(MedEntry.CONTENT_URI, idIndex);
                intent.setData(currentMedDataUri);
                startActivity(intent);
                mMedListAdapter.notifyItemRemoved(position);
            }
        });

}

    public Bundle returnBundleForNewActivity(int positionClicked) {

        Cursor cursor = mMedListAdapter.entireCursorForHomeActivity();

        cursor.moveToPosition(positionClicked);

        final int idIndex = cursor.getInt(cursor.getColumnIndexOrThrow(MedEntry.MED_DB_DEFAULT_ID));
        String name = cursor.getString(cursor.getColumnIndexOrThrow(MedEntry.MED_COLUMN_NAME));
        int type = cursor.getInt(cursor.getColumnIndexOrThrow(MedEntry.MED_COLUMN_TYPE));
        String description = cursor.getString(cursor.getColumnIndexOrThrow(MedEntry.MED_COLUMN_DESCRIPTION));
        int dosage = cursor.getInt(cursor.getColumnIndexOrThrow(MedEntry.MED_COLUMN_DOSAGE));
        String interval = cursor.getString(cursor.getColumnIndexOrThrow(MedEntry.MED_COLUMN_INTERVAL));
        String startDate = cursor.getString(cursor.getColumnIndexOrThrow(MedEntry.MED_COLUMN_START_DATE));
        String endDate = cursor.getString(cursor.getColumnIndexOrThrow(MedEntry.MED_COLUMN_END_DATE));
        int takenCount = cursor.getInt(cursor.getColumnIndexOrThrow(MedEntry.MED_COLUMN_TAKEN_COUNT));
        int ignoreCount = cursor.getInt(cursor.getColumnIndexOrThrow(MedEntry.MED_COLUMN_IGNORE_COUNT));

        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        bundle.putInt("type", type);
        bundle.putString("description", description);
        bundle.putInt("dosage", dosage);
        bundle.putString("interval", interval);
        bundle.putString("startDate", startDate);
        bundle.putString("endDate", endDate);
        bundle.putInt("id", idIndex);
        bundle.putInt("takenCount",takenCount);
        bundle.putInt("ignoreCount",ignoreCount);

        return bundle;
    }

    @Override
    public boolean onCreatePanelMenu(int featureId, Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search_view);
//        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
//        searchView.setOnQueryTextListener(this);
        return true;
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
            case R.id.action_search_view:
                removeRecyclerView();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void removeRecyclerView(){
        mRecyclerView.setVisibility(View.GONE);
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
    protected void onResume() {
        super.onResume();
        getSupportLoaderManager().restartLoader(MED_TASK_LOADER_ID, null, this);
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
