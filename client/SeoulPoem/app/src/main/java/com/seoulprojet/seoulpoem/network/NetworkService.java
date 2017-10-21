package com.seoulprojet.seoulpoem.network;


import com.bumptech.glide.request.Request;
import com.seoulprojet.seoulpoem.model.ModifyPoem;
import com.seoulprojet.seoulpoem.model.ReadingPoem;
import com.seoulprojet.seoulpoem.model.SavePoemResult;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

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

    //작품 상세보기
    @GET("/article/{article_id}")
    Call<ReadingPoem> readPoem(@Header("email") String email,
                               @Header("type") int type,
                               @Path("article_id") int article_id);

    //작품 수정하기
    @Multipart
    @PUT("/article/{article_id}")
    Call<ModifyPoem> modifyPoem(@Header("email") String email,
                                @Header("type") int type,
                                @Path("article_id") int article_id,
                                @Part("content") RequestBody content,
                                @Part("tags") RequestBody tags,
                                @Part("inform") RequestBody inform,
                                @Part("sort") RequestBody sort,
                                @Part("color") RequestBody color,
                                @Part("underline") RequestBody underline,
                                @Part("inclination") RequestBody inclination,
                                @Part("bold") RequestBody bold,
                                @Part("background") RequestBody background,
                                @Part("font_size") RequestBody font_size,
                                @Part("title") RequestBody title,
                                @Part("date") RequestBody date);











}