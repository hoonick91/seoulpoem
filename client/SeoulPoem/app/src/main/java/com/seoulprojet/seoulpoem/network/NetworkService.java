package com.seoulprojet.seoulpoem.network;


import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by pc on 2017-05-14.
 */

public interface NetworkService {

    //main 리스트 가져오기
    /* 준희
    @GET("/main")
    Call<MainResult> getPoems(@Query("tag") String tag);

    //갤러리 리스트 가져오기
    @GET("/main/all")
    Call<GalleryResult> getPhotos(@Query("tag") String tag);

    //세부 정보 가져오기
    @GET("/article/simple/{articleid}")
    Call<DetailResult> getDetail(@Path("articleid") int articleid);

    //작품 리스트 가져오기
    @GET("/article/simple/{articleid}")
    Call<AddResult> getWorks(@Path("articleid") int articleid);
    */

    // mypage 데이터 가져오기
}