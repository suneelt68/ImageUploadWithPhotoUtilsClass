package com.example.sunilkumar.imageuploadwithphotoutilsclass.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.darsh.multipleimageselect.activities.AlbumSelectActivity;
import com.darsh.multipleimageselect.helpers.Constants;
import com.darsh.multipleimageselect.models.Image;
import com.example.sunilkumar.imageuploadwithphotoutilsclass.R;
import com.example.sunilkumar.imageuploadwithphotoutilsclass.RetrofitApiClasses.ApiConfigInterface;
import com.example.sunilkumar.imageuploadwithphotoutilsclass.RetrofitApiClasses.AppConfig;
import com.example.sunilkumar.imageuploadwithphotoutilsclass.model.ServerResponse;
import com.example.sunilkumar.imageuploadwithphotoutilsclass.imagehelperclasses.CameraPhoto;
import com.example.sunilkumar.imageuploadwithphotoutilsclass.imagehelperclasses.ImageLoader;
import com.example.sunilkumar.imageuploadwithphotoutilsclass.imagehelperclasses.UploadHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    public static int IMAGE=1,VIDEO=2,AUDIO=3;

    Thread mainThread;
    String filePathAsString="";
    double fileSize=0;

    //declare them as global variables
    CameraPhoto cameraPhoto;
    UploadHelper uploadHelper;
    final int CAMERA_REQUEST = 1100,GALLERY_REQUEST=111;

    ImageView image;
    LinearLayout linearLayout;
    Button selectImageButton,uploadImageButton, uploadMulImagesButton,selectMulImages,fromGalley,fromCamera,gotoVideoActivity;
    ArrayList<String> imagePathsList = new ArrayList<>();


    public static int REQUEST_ID_MULTIPLE_PERMISSIONS =1000;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        image = (ImageView) findViewById(R.id.image1);
        selectImageButton = (Button) findViewById(R.id.selectImage);
        selectMulImages = (Button) findViewById(R.id.selectMulImage);
        uploadImageButton = (Button) findViewById(R.id.uploadImage);
        uploadMulImagesButton = (Button) findViewById(R.id.uploadMultipleImage);
        fromCamera = (Button) findViewById(R.id.fromCameraButton);
        fromGalley = (Button) findViewById(R.id.fromGallery);
        gotoVideoActivity = (Button) findViewById(R.id.gotoVideoActivity);
        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading...");

        mainThread = Thread.currentThread();


        cameraPhoto = new CameraPhoto(getApplicationContext());
        uploadHelper = new UploadHelper(getApplicationContext());

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
             if (checkAndRequestPermissions()) {
            }
        }

        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                selectImageButton.setVisibility(View.GONE);
                linearLayout.setVisibility(View.VISIBLE);


            }
        });


        fromCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //call it to open the camera
                try {
                    linearLayout.setVisibility(View.GONE);
                    selectImageButton.setVisibility(View.VISIBLE);

                    startActivityForResult(cameraPhoto.getIntent(), CAMERA_REQUEST);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                cameraPhoto.addToGallery();

                // call to open gallery type

                /*Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, 1100);*/



               /* Intent intent = new Intent();
                intent.setType("image*//*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"), 0);*/
            }
        });

        fromGalley.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //call it to open the camera
                /*try {
                    startActivityForResult(cameraPhoto.getIntent(), CAMERA_REQUEST);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                cameraPhoto.addToGallery();*/

                // call to open gallery type
                linearLayout.setVisibility(View.GONE);
                selectImageButton.setVisibility(View.VISIBLE);
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, GALLERY_REQUEST);



               /* Intent intent = new Intent();
                intent.setType("image*//*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"), 0);*/
            }
        });

        selectMulImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AlbumSelectActivity.class);
                //set limit on number of images that can be selected, default is 10
                intent.putExtra(Constants.INTENT_EXTRA_LIMIT, 10);
                startActivityForResult(intent, 995);
            }
        });


        uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (filePathAsString.equals("")||filePathAsString==null){
                    Toast.makeText(MainActivity.this, "Please select image first", Toast.LENGTH_SHORT).show();
                }
                else {

                    uploadSingleFile(filePathAsString,fileSize,"image");

                }
            }
        });




        uploadMulImagesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (imagePathsList.size()>=1){
                    progressDialog.show();
                    progressDialog.setCancelable(false);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            uploadMultipleFilesForServer(imagePathsList);
                        }
                    }).start();

                }
                else {
                    Toast.makeText(MainActivity.this, "Please Select Some Images", Toast.LENGTH_SHORT).show();
                }
            }
        });

        gotoVideoActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,VideoActivity.class);
                startActivity(intent);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_REQUEST) {

                filePathAsString = cameraPhoto.getPath();
                fileSize = UploadHelper.getFileSizeKiloBytes(filePathAsString);
                try {
                    Bitmap bitmap = ImageLoader.init().from(filePathAsString).requestSize(512, 512).getBitmap();
                    image.setImageBitmap(bitmap); //imageView is your ImageView
                    selectImageButton.setText(filePathAsString+'\n'+"size of file is "+fileSize);
                    // filePathInString = uploadHelper.GetFilePathAsString()


                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
            else if (requestCode==GALLERY_REQUEST){

                Uri selectedImage = data.getData();
                filePathAsString = getFilePathAsString(selectedImage);
                fileSize = UploadHelper.getFileSizeKiloBytes(filePathAsString);

                try {
                    Bitmap bitmap = ImageLoader.init().from(filePathAsString).requestSize(512, 512).getBitmap();
                    image.setImageBitmap(bitmap); //imageView is your ImageView
                    selectImageButton.setText(filePathAsString+'\n'+"size of file is "+fileSize);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }

            else if (requestCode==995&&data!=null){


                ArrayList<Image> images = data.getParcelableArrayListExtra(Constants.INTENT_EXTRA_IMAGES);
                StringBuffer stringBuffer = new StringBuffer();
                for (int i = 0, l = images.size(); i < l; i++) {
                    stringBuffer.append(images.get(i).path + "\n");
                    imagePathsList.add(images.get(i).path);
                    selectMulImages.setText(stringBuffer.toString());
                }


            }
        }//end if resultCode
    }
    private void uploadMultipleFilesForServer(ArrayList<String> filesList){

        ApiConfigInterface getResponse = AppConfig.getRetrofit().create(ApiConfigInterface.class);
        // create list of file parts (photo, video, ...)
        List<MultipartBody.Part> parts = new ArrayList<>();

        if (filesList.size()>=1){
            for (String fileslistVar:filesList){
                parts.add(prepareFilePart("image", fileslistVar));
            }
        }
        RequestBody description = RequestBody.create(MediaType.parse("text/plain"),filePathAsString);
        Call<String> call = getResponse.uploadMultipleFilesToLocal(parts,description);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response!=null){
                    Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();
                    uploadMulImagesButton.setText("success");
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(MainActivity.this, ""+t.toString(), Toast.LENGTH_SHORT).show();
                uploadMulImagesButton.setText(t.toString());
                progressDialog.dismiss();
            }
        });



    }


    @NonNull
    private MultipartBody.Part prepareFilePart(String partName, String filePath) {
        // https://github.com/iPaulPro/aFileChooser/blob/master/aFileChooser/src/com/ipaulpro/afilechooser/utils/FileUtils.java
        // use the FileUtils to get the actual file by uri
       // File file = FileUtils.getFile(this, fileUri);

        File file = new File(filePath);
        // create RequestBody instance from file
        RequestBody requestFile = RequestBody.create(MediaType.parse("*/*"), file);

        // MultipartBody.Part is used to send also the actual file name
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }



    private void uploadMultipleFiles1(ArrayList<String> filesList){
        progressDialog.setCancelable(false);
        progressDialog.show();

        ArrayList<File> fileArrayList = new ArrayList<>();

        ApiConfigInterface getResponse = AppConfig.getRetrofit().create(ApiConfigInterface.class);

        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);

        for (int i=0;i<filesList.size();i++){
            File file = new File(filesList.get(i));
            fileArrayList.add(file);
        }

        for (int j=0;j<fileArrayList.size();j++){

            builder.addFormDataPart("image",fileArrayList.get(j).getName(),RequestBody.create(MediaType.parse("image/*"), fileArrayList.get(j)));
        }
        MultipartBody requestBody = builder.build();
        RequestBody desc = RequestBody.create(MediaType.parse("text/plain"),"aaa");
        Call<ServerResponse> call = getResponse.postMeme(requestBody);

        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                Toast.makeText(MainActivity.this, "yes", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, ""+t.toString(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });


    }


    private void uploadMulImages(ArrayList<String> imagePathsList) {

        progressDialog.show();







    }


    //**********222222 UNCOMMENT IF YOU ARE USING MULTIPLE IMAGE UPLOAD

  /*  @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            ArrayList<Image> images = data.getParcelableArrayListExtra(Constants.INTENT_EXTRA_IMAGES);
            StringBuffer stringBuffer = new StringBuffer();
            for (int i = 0, l = images.size(); i < l; i++) {
                stringBuffer.append(images.get(i).path + "\n");
            }
            Log.e("paths",stringBuffer.toString());

        }
    }*/


    //**********222222 UNCOMMENT IF YOU ARE USING MULTIPLE IMAGE UPLOAD


    // *******1111111 un comment if you are using normal intent


    //*******1111111 un comment if you are using normal intent


    private boolean checkAndRequestPermissions() {

        int reoordPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        int storagePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);

        List<String> listPermissionsNeeded = new ArrayList<>();

        if (reoordPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.RECORD_AUDIO);
        }
        if (storagePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }


    private void uploadSingleFile(String filePathAsString, double filesize, String typeOfFile) {

        progressDialog.show();



        // create the retrofit instance for interface for possible api calls
        String mediaPath = filePathAsString;

        // Map is used to multipart the file using okhttp3.RequestBody
        File file = new File(mediaPath);

        if (filesize>250){
            Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
            try {
                bitmap.compress(Bitmap.CompressFormat.JPEG,2,new FileOutputStream(file));
                Log.e("fileSize",""+UploadHelper.getFileSizeKiloBytes(filePathAsString));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        ApiConfigInterface getResponse = AppConfig.getRetrofit().create(ApiConfigInterface.class);

        final RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"),file);
        MultipartBody.Part fileToUplad = MultipartBody.Part.createFormData("image",file.getName(),requestBody);

        RequestBody description = RequestBody.create(MediaType.parse("text/plain"),file.getName());

        Call<String> call = getResponse.uploadFileToLocal(fileToUplad,description);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response!=null){
                   // Toast.makeText(MainActivity.this, "File Uploaded"+response.toString(), Toast.LENGTH_SHORT).show();
                    Toast.makeText(MainActivity.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
                    uploadImageButton.setText("file uploaded- check in 192.168.10.127/AndroidImageUpload/uploads");
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(MainActivity.this, ""+t.toString(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }

        });



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


}
