package com.example.android.med_manager;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.med_manager.data.MedContract.MedEntry;
import com.example.android.med_manager.ui.DetailActivity;

/**
 * Created by SOLARIN O. OLUBAYODE on 18/04/18.
 */

public class SearchAdapter extends CursorAdapter {

    private LayoutInflater mLayoutInflater;
        private Context mContext;
        private SearchView mSearchView;

        public SearchAdapter(Context context, Cursor cursor, SearchView searchView) {
            super(context, cursor, false);
            mContext = context;
            mSearchView = searchView;
            mLayoutInflater = LayoutInflater.from(context);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View view = mLayoutInflater.inflate(R.layout.med_item_card_view, parent, false);
            return view;
        }

        @Override
        public void bindView(View view, final Context context, Cursor cursor) {

            final String name = cursor.getString(cursor.getColumnIndex(MedEntry.MED_COLUMN_NAME));
            final String dosage = cursor.getString(cursor.getColumnIndex(MedEntry.MED_COLUMN_DOSAGE));
            final int idIndex = cursor.getInt(cursor.getColumnIndexOrThrow(MedEntry.MED_DB_DEFAULT_ID));
            final int type = cursor.getInt(cursor.getColumnIndexOrThrow(MedEntry.MED_COLUMN_TYPE));
            final String startDate = cursor.getString(cursor.getColumnIndexOrThrow(MedEntry.MED_COLUMN_START_DATE));
            final String endDate = cursor.getString(cursor.getColumnIndexOrThrow(MedEntry.MED_COLUMN_END_DATE));


            TextView nameTv =  view.findViewById(R.id.med_name_card);
            nameTv.setText(name);

            TextView dosageTv = view.findViewById(R.id.med_dosage_card);
            dosageTv.setText(dosage);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TextView pName = (TextView) view.findViewById(R.id.med_name_card);
                    mSearchView.setIconified(true);
                    //get contact details and display
                    Toast.makeText(context, " "+ pName.getText(),
                            Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(mContext, DetailActivity.class);
                    intent.setData(ContentUris.withAppendedId(MedEntry.CONTENT_URI,idIndex));
                    mContext.startActivity(intent);
                }
            });
        }
    }
