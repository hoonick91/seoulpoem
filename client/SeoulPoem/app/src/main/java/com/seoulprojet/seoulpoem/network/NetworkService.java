package com.seoulprojet.seoulpoem.network;


import com.seoulprojet.seoulpoem.model.MyPagePhotoResult;
import com.seoulprojet.seoulpoem.model.MyPagePoemResult;
import com.seoulprojet.seoulpoem.model.MyPageResult;
import com.seoulprojet.seoulpoem.model.NoticeDetailResult;
import com.seoulprojet.seoulpoem.model.NoticeResult;
import com.seoulprojet.seoulpoem.model.TodayResult;
import com.seoulprojet.seoulpoem.model.WriterListResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
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

    // mypage poem
    @GET("/mypage/poem")
    Call<MyPagePoemResult> getMyPoem(@Header("email") String email,
                                     @Header("type") int type);

    @GET("/mypage/photo")
    Call<MyPagePhotoResult> getMyPhoto(@Header("email") String email,
                                       @Header("type") int type);

    // mypage 프로필 정보
    @GET("/mypage")
    Call<MyPageResult> getMyPage(@Header("email") String email,
                                 @Header("type") int type);

    // notice
    @GET("/notice")
    Call<NoticeResult> getNotice();

    // notice detail
    @GET("/notice/{idnotices}")
    Call<NoticeDetailResult> getNoticeDetail(@Path("idnotices") int idnotices);

    // writer list
    @GET("/author")
    Call<WriterListResult> getWriterList();

    // today seoul
    @GET("/subway")
    Call<TodayResult> getToday();
}