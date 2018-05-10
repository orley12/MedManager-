package com.example.android.med_manager;

//import android.content.AsyncTaskLoader;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.AsyncTaskLoader;

import com.example.android.med_manager.data.MedContract.MedEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SOLARIN O. OLUBAYODE on 08/05/18.
 */

public class MedDataLoader extends AsyncTaskLoader<List<MedData>> {

    private Context mContext;

    public MedDataLoader(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<MedData> loadInBackground() {
        return getMedData();
    }

    private List<MedData> getMedData() {

        List<MedData> medDataList = new ArrayList<>();
        Cursor cursor = mContext.getContentResolver().query(MedEntry.CONTENT_URI,null,
                null,null,null);

        while (cursor.moveToNext()){
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(MedEntry.MED_DB_DEFAULT_ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(MedEntry.MED_COLUMN_NAME));
            int type = cursor.getInt(cursor.getColumnIndexOrThrow(MedEntry.MED_COLUMN_TYPE));
            int dosage = cursor.getInt(cursor.getColumnIndexOrThrow(MedEntry.MED_COLUMN_DOSAGE));
            long startDate = cursor.getLong(cursor.getColumnIndexOrThrow(MedEntry.MED_COLUMN_START_DATE));
            long endDate = cursor.getLong(cursor.getColumnIndexOrThrow(MedEntry.MED_COLUMN_END_DATE));

            medDataList.add(new MedData(id,name,type,dosage,startDate,endDate));

        }

        cursor.close();

        return medDataList;
    }
}
