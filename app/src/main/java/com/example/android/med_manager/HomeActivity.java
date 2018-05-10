package com.example.android.med_manager;

import android.content.ContentResolver;
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
import android.support.v7.widget.SearchView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.android.med_manager.data.MedContract.MedEntry;
import com.example.android.med_manager.data.MedDbHelper;
import com.example.android.med_manager.sync.NotificationScheduler;

//import android.support.v4.content.Loader;

public class HomeActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    public static final String LOG_TAG = HomeActivity.class.getSimpleName();

    private static final int MED_TASK_LOADER_ID = 1;

    public static long TIME_INTERVAL;

    MedListAdapter mMedListAdapter;

    RecyclerView mRecyclerView;

    LinearLayoutManager mLinearLayoutManager;

    MedDbHelper mMedDbHelper;

    long mId;

    SearchView mSearchView;

    FloatingActionButton mFloatingActionButton;

    ListView listView;

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

//        listView = findViewById(R.id.list_view);

//        mRecyclerView.setVisibility(View.GONE);

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
                Log.i(LOG_TAG, "onSwiped: " + mId);
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

    }


    public RecyclerItemClickListener createItemClickListener(final RecyclerView recyclerView) {
        return new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                    Intent intent = new Intent(HomeActivity.this, DetailActivity.class);
                    Uri currentMedDataUri = returnCurrentMedDataUri(position);
                    intent.setData(currentMedDataUri);
                    startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                if (view instanceof LinearLayout) {

            } else {
                    Intent intent = new Intent(HomeActivity.this, MedFormActivity.class);
                    Uri currentMedDataUri = returnCurrentMedDataUri(position);
                    intent.setData(currentMedDataUri);
                    startActivity(intent);
                }
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
        mSearchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        mSearchView.setSubmitButtonEnabled(true);
        mSearchView.setOnQueryTextListener(onQueryTextListener);
        return true;
    }
    private SearchView.OnQueryTextListener onQueryTextListener =
            new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    Cursor medQuery = getListOfMedNames(query);
                    SearchAdapter cursorAdapter = new SearchAdapter
                            (HomeActivity.this, medQuery,mSearchView );
                    mSearchView.setSuggestionsAdapter(cursorAdapter);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    Cursor medQuery = getListOfMedNames(newText);
                    SearchAdapter cursorAdapter = new SearchAdapter
                            (HomeActivity.this, medQuery,mSearchView );
                    mSearchView.setSuggestionsAdapter(cursorAdapter);
                    return true;
                }
            };

    public Cursor getListOfMedNames(String searchText) {

        Cursor cursor = null;
        ContentResolver contentResolver = getContentResolver();

        String[] mProjection = {MedEntry.MED_DB_DEFAULT_ID,
                MedEntry.MED_COLUMN_NAME,
                MedEntry.MED_COLUMN_DOSAGE,
                MedEntry.MED_COLUMN_START_DATE,
                MedEntry.MED_COLUMN_END_DATE,
                MedEntry.MED_COLUMN_TYPE};

        Uri uri = MedEntry.CONTENT_URI;

        String selection = MedEntry.MED_COLUMN_NAME + "=?";
        String[] selectionArgs = {String.valueOf(searchText)};

        cursor = contentResolver.query(uri, mProjection, selection, selectionArgs, null);
//        cursor.close();

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
            case R.id.action_search:
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
