package com.example.sunilkumar.imageuploadwithphotoutilsclass.RetrofitApiClasses;

import com.example.sunilkumar.imageuploadwithphotoutilsclass.model.ServerResponse;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiConfigInterface {
    @Multipart
    @POST("retrofit_example/upload_image.php")
   // @POST("AndroidImageUpload/upload.php")
    Call<ServerResponse> uploadFileToServer(@Part MultipartBody.Part file,
                                            @Part("name") RequestBody name);

    @Multipart
    @POST("AndroidImageUpload/upload.php")
    Call<String> uploadFileToLocal(@Part MultipartBody.Part file,
                                    @Part("name") RequestBody name);

    /*@Multipart
    @POST("ImageUpload")
    Call<ServerResponseKeshav> uploadFile(@Part MultipartBody.Part file,
                                    @Part("file") RequestBody name);*/

    @Multipart
    @POST("retrofit_example/upload_multiple_files.php")
    Call<ServerResponse> uploadMulFile(@Part MultipartBody.Part file1,
                                       @Part MultipartBody.Part file2);

  /*  @POST("retrofit_example/upload_image.php")
    Call<ServerResponse> postMeme(@Body RequestBody files);*/

    @POST("retrofit_example/upload_image.php")
    Call<ServerResponse> postMeme(@Body RequestBody files);

    @Multipart
    @POST("retrofit_example/upload_image.php")
    Call<String> uploadMultipleFilesToSever(@Part List<MultipartBody.Part> files,
                                                    @Part("name") RequestBody requestBody);

    @Multipart
    @POST("AndroidImageUpload/upload.php")
    Call<String> uploadMultipleFilesToLocal(@Part List<MultipartBody.Part> files,
                                                    @Part("name") RequestBody requestBody);



}
