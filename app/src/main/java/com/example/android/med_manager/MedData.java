package com.example.android.med_manager;

/**
 * Created by SOLARIN O. OLUBAYODE on 09/04/18.
 */

public class MedData {

    private long mId;

    private String mName;

    private int mType;

//    private String mDescription;

    private int mDosage;

    private long mStartDate;

    private long mEndDate;


    public MedData(long id, String name, int type, int dosage, long startDate, long endDate) {
        mId = id;
        mName = name;
        mType = type;
        mDosage = dosage;
        mStartDate = startDate;
        mEndDate = endDate;
    }

    public long getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public int getType() {
        return mType;
    }

    public int getDosage() {
        return mDosage;
    }

    public long getStartDate() {
        return mStartDate;
    }

    public long getEndDate() {
        return mEndDate;
    }

}
