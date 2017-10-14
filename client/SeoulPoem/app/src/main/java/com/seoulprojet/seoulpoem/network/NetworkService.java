package com.seoulprojet.seoulpoem.network;


import com.seoulprojet.seoulpoem.model.SavePoemResult;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

import static com.seoulprojet.seoulpoem.component.Preview.photo;

/**
 * Created by pc on 2017-05-14.
 */

public interface NetworkService {


    //작품 저장
    @Multipart
    @POST("/article")
    Call<SavePoemResult> savePoem(@Header("email") String email,
                                  @Header("type") int type,
                                  @Part MultipartBody.Part photo,
                                  @Part("title") RequestBody title,
                                  @Part("font_size") RequestBody font_size,
                                  @Part("bold") RequestBody bold,
                                  @Part("inclination") RequestBody inclination,
                                  @Part("underline") RequestBody underline,
                                  @Part("color") RequestBody color,
                                  @Part("sortinfo") RequestBody sortinfo,
                                  @Part("content") RequestBody content,
                                  @Part("tags") RequestBody tags,
                                  @Part("inform") RequestBody inform,
                                  @Part("background") RequestBody background);

}