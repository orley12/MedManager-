package com.example.android.med_manager.utilities;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by SOLARIN O. OLUBAYODE on 29/07/18.
 */

public class FileUtils {


    private static String mExternal_path = Environment.getExternalStorageDirectory() + File.separator +".medmanager";

    public static String getPath(Context context, Uri uri) {

        String path = null;

        if("content".equalsIgnoreCase(uri.getScheme())){
            String[] projection = {"_data"};
            Cursor cursor = null;


            try{
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if(cursor.moveToFirst()){
                    path =  cursor.getString(column_index);
                }
                cursor.close();
            }catch (Exception e){
                Log.e("getPathError", "getPath: " +e.getMessage() );
            }
        }else if("file".equalsIgnoreCase(uri.getScheme())){
            path =  uri.getPath();
        }

        return "file:" + path;
    }

    //check if provided file path is an image or not
    public static boolean isImageFile(String filePath){
        return ("jpg".equalsIgnoreCase(getFileExtension(filePath))
                || "png".equalsIgnoreCase(getFileExtension(filePath)) || "jpeg".equalsIgnoreCase(getFileExtension(filePath)));
    }

    //get file extension/format from filepath
    @Nullable
    public static String getFileExtension( @NonNull String filePath){
        int strLength = filePath.lastIndexOf(".");
        if(strLength > 0) {
            String extension = filePath.substring(strLength + 1).toLowerCase();
            return extension;
        }else {
            return null;
        }
    }

    public static File saveImage(Context context, Bitmap image){

        createFolderIfNotExist();

        String file_path = mExternal_path + File.separator +"medmnger_"+ getDateString() + ".png";
        File file = null;
        try {
            file = new File(file_path);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.PNG, 0, baos);
            byte[] bitmapData = baos.toByteArray();

            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bitmapData);
            fos.flush();
            fos.close();

            Log.e("FileUtilsClass", "saveStampedImage: "+file.getPath());

        }catch (Exception e){
            Log.e("FileUtilsClass", "saveStampedImage: ", e );
        }

        return file;
    }

    private static String getDateString() {

        Date date = Calendar.getInstance().getTime();

        String format = "ddMMyyyy_HHmmss";
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.US);

        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+1"));

        return String.format("%s%s", "", dateFormat.format(date));
    }

    private static void createFolderIfNotExist() {
        File folder = new File(mExternal_path);

        if(!folder.exists()){
            folder.mkdir();
        }
    }
}
