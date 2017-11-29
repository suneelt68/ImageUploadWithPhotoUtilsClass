package com.example.sunilkumar.imageuploadwithphotoutilsclass.imagehelperclasses;

import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;

import java.io.File;

/**
 * Created by sunilkumar on 24-11-2017.
 */

public class UploadHelper extends AppCompatActivity{

    private Context context;

    public UploadHelper(Context context){
        this.context = context;
    }


    public String getFilePathAsString(Uri uri){

        Uri selectedImage = uri;
        String[] filePathColumn = {MediaStore.Images.Media.DATA};

        Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        assert cursor != null;
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String mediaPath = cursor.getString(columnIndex);
       // str1.setText(mediaPath);
        // Set the Image in ImageView for Previewing the Media
       // imgView.setImageBitmap(BitmapFactory.decodeFile(mediaPath));
        cursor.close();

        return mediaPath;
    }


    public static double getFileSizeKiloBytes(String file) {

        File file1 = new File(file);

        return (double) file1.length() / 1024;
    }




}
