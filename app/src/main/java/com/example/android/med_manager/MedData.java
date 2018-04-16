package com.example.android.med_manager;

/**
 * Created by SOLARIN O. OLUBAYODE on 09/04/18.
 */

public class MedData {

    private long mIdIndex;

    private int mIntervalTime;

    private long mStartDate;

    private long mEndDate;

    public MedData(long idIndex, int intervalTime, long startDate, long endDate) {
        mIdIndex = idIndex;
        mIntervalTime = intervalTime;
        mStartDate = startDate;
        mEndDate = endDate;
    }

    public long getIdIndex() {
        return mIdIndex;
    }

    public int getIntervalTime() {
        return mIntervalTime;
    }

    public long getStartDate() {
        return mStartDate;
    }

    public long getEndDate() {
        return mEndDate;
    }
}
