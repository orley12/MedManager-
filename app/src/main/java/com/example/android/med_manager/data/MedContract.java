package com.example.android.med_manager.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by SOLARIN O. OLUBAYODE on 01/04/18.
 */

public class MedContract {

    public static final String PROFILE_PATH = "profile";

    public static final String MEDICATION_PATH = "medication";

    public static final String CONTENT_AUTHORITY = "com.example.android.med_manager";

    public static final String BASE_CONTENT_URI = "content://com.example.android.med_manager";


    MedContract() {
    }

    public static final class ProfileEntry implements BaseColumns {

        public static final String PROFILE_TABLE_NAME = "profile";
        public static final String PROFILE_DB_DEFAULT_ID = BaseColumns._ID;
        public static final String PROFILE_COLUMN_EMAIL = "email";
        public static final String PROFILE_COLUMN_NAME = "name";
        public static final String PROFILE_SURNAME_NAME = "surname";
        public static final String PROFILE_ID_GOOGLE = "id";
        public static final String PROFILE_USER_NAME = "username";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(Uri.parse(BASE_CONTENT_URI), PROFILE_PATH);

    }

    public static final class MedEntry implements BaseColumns {

        public static final String MED_TABLE_NAME = "medication";
        public static final String MED_DB_DEFAULT_ID = BaseColumns._ID;
        public static final String MED_COLUMN_NAME = "med_name";
        public static final String MED_COLUMN_TYPE = "med_type";
        public static final String MED_COLUMN_DESCRIPTION = "med_description";
        public static final String MED_COLUMN_DOSAGE = "med_dosage";
        public static final String MED_COLUMN_INTERVAL = "med_interval";
        public static final String MED_COLUMN_START_DATE = "med_start_date";
        public static final String MED_COLUMN_END_DATE = "med_end_date";
        public static final String MED_COLUMN_TAKEN_COUNT = "med_taken_count";
        public static final String MED_COLUMN_IGNORE_COUNT = "med_ignore_count";
        public static final String MED_COLUMN_REMINDER_COUNT = "med_reminder_count";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(Uri.parse(BASE_CONTENT_URI), MEDICATION_PATH);


        public static final int MED_TYPE_CAPSULES = 1;
        public static final int MED_TYPE_TABLETS = 2;
        public static final int MED_TYPE_SYRUP = 3;
        public static final int MED_TYPE_INHALER = 4;
        public static final int MED_TYPE_DROPS = 5;
        public static final int MED_TYPE_OINTMENT = 6;
        public static final int MED_TYPE_INJECTION = 7;
        public static final int MED_TYPE_OTHERS = 8;
    }


}
