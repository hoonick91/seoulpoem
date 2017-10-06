package com.seoulprojet.seoulpoem.network;


import com.seoulprojet.seoulpoem.model.AddResult;
import com.seoulprojet.seoulpoem.model.DetailResult;
import com.seoulprojet.seoulpoem.model.GalleryResult;
import com.seoulprojet.seoulpoem.model.MainResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by pc on 2017-05-14.
 */

public interface NetworkService {


    //main 리스트 가져오기
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



}