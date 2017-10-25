package com.seoulprojet.seoulpoem.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.seoulprojet.seoulpoem.R;
import com.seoulprojet.seoulpoem.model.AddArticleResult;
import com.seoulprojet.seoulpoem.model.DetailResult;
import com.seoulprojet.seoulpoem.model.MainResult;
import com.seoulprojet.seoulpoem.network.ApplicationController;
import com.seoulprojet.seoulpoem.network.NetworkService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity {

    /***************************************변수***********************************************/

    private ImageView ivShare, ivShare02, ivSetting;
    private AddWorkDialog addWorkDialog;
    private SettingDialog settingDialog;
    private SettingDialog02 settingDialog02;
    private InfoDialog infoDialog;
    private LinearLayout llPhoto;
    private NetworkService service;
    private ImageView ivPhoto, ivProfile;
    private TextView tvName, tvTags;
    private int articleId;
    private RelativeLayout llup;

    //유저 정보
    private String userEmail = null;
    private int loginType = 0;

    //작품담기 다이얼로그 리스너
    private View.OnClickListener addDialog_leftListener = new View.OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent(DetailActivity.this, AddActivity.class);
            intent.putExtra("userEmail", userEmail);
            intent.putExtra("loginType", loginType);
            startActivity(intent);
            addWorkDialog.dismiss();
        }
    };
    private View.OnClickListener addDialog_rightListener = new View.OnClickListener() {
        public void onClick(View v) {
            addWorkDialog.dismiss();
        }
    };


    //설정 - 상세정보/수정하기 있는 다이얼로그 => 수정하기 눌렀을 때
    private View.OnClickListener settingDialog_listener03 = new View.OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent(DetailActivity.this, WritePoemActivity.class);
            intent.putExtra("type", "" + articleId);
            intent.putExtra("userEmail", userEmail);
            intent.putExtra("loginType", loginType);
            startActivity(intent);
        }
    };

    //설정 - 상세정보/수정하기 있는 다이얼로그 => 상세정보 눌렀을 때
    private View.OnClickListener settingDialog_listener02 = new View.OnClickListener() {
        public void onClick(View v) {
            //원래 뜬 다이얼로그 없애고
            settingDialog.dismiss();
            infoDialog = new InfoDialog(DetailActivity.this);
            infoDialog.setCanceledOnTouchOutside(true);
            infoDialog.show();
        }
    };

    //설정 - 상세정보만 있는 다이얼로그
    private View.OnClickListener settingDialog_listener04 = new View.OnClickListener() {
        public void onClick(View v) {

            //원래 뜬 다이얼로그 없애고
            settingDialog02.dismiss();

            //상세정보 다이얼로그
            infoDialog = new InfoDialog(DetailActivity.this);
            infoDialog.setCanceledOnTouchOutside(true);
            infoDialog.show();
        }
    };

    //수정 가능 불가능
    int modifiable;

    //작품 상세 정보
    String tags, informs;

    /***************************************START***********************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        service = ApplicationController.getInstance().getNetworkService();

        //해당 프로젝트 id값, 유저 정보 가져오기
        Intent intent = getIntent();
        articleId = intent.getExtras().getInt("articleId");
        userEmail = intent.getExtras().getString("userEmail");
        loginType = intent.getExtras().getInt("loginType");


        //findView
        findView();

        //photo click
        clickPhoto();

        //up Button click
        clickUpButton();

        //뒤로가기
        goBack();

        //작품담기 다이얼로그
        addDialog();

        //설정 다이얼로그
        settingDialog();

        //상세정보 네트워크
        getDetail();

    }

    /***************************************findView***********************************************/
    public void findView() {
        ivShare = (ImageView) findViewById(R.id.ivShare);
        ivShare02 = (ImageView) findViewById(R.id.ivShare02);

        ivSetting = (ImageView) findViewById(R.id.ivSetting);

        llPhoto = (LinearLayout) findViewById(R.id.llPhoto);
        ivPhoto = (ImageView) findViewById(R.id.ivPhoto);
        ivProfile = (ImageView) findViewById(R.id.ivProfile);

        tvName = (TextView) findViewById(R.id.tvName);
        tvTags = (TextView) findViewById(R.id.tvTags);

        llup = (RelativeLayout) findViewById(R.id.llup);
    }


    /***************************************click photo***********************************************/
    public void clickPhoto() {

        llPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //담은 작품인지, 담지 않은 작품인지에 따라서 나누어야함
                //우측 상단 visible
                ivShare.setVisibility(v.VISIBLE);
                ivSetting.setVisibility(v.VISIBLE);

                //하단 visible
                RelativeLayout rlBottomPart = (RelativeLayout) findViewById(R.id.rlBottomPart);
                rlBottomPart.setVisibility(v.VISIBLE);
            }
        });
    }

    /***************************************작품 담기 다이얼로그***********************************************/

    public class AddWorkDialog extends Dialog {

        private TextView text01, text02, text03;
        private TextView mLeftButton, mRightButton;
        String str01, str02, str03;
        private View.OnClickListener addDialog_leftListener, addDialog_rightListener;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // 다이얼로그 외부 화면 흐리게 표현
            WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
            lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            lpWindow.dimAmount = 0.8f;
            getWindow().setAttributes(lpWindow);

            //view mapping
            setContentView(R.layout.dialog_share);

            //findView
            text01 = (TextView) findViewById(R.id.text01);
            text02 = (TextView) findViewById(R.id.text02);
            text03 = (TextView) findViewById(R.id.text03);
            mLeftButton = (TextView) findViewById(R.id.btnMove);
            mRightButton = (TextView) findViewById(R.id.btnBack);

            // 제목과 내용을 생성자에서 셋팅
            text01.setText(str01);
            text02.setText(str02);
            text03.setText(str03);

            // 클릭 이벤트 셋팅
            mLeftButton.setOnClickListener(addDialog_leftListener);
            mRightButton.setOnClickListener(addDialog_rightListener);
        }


        // 생성자
        public AddWorkDialog(Context context, String str01, String str02, String str03,
                             View.OnClickListener addDialog_leftListener,
                             View.OnClickListener addDialog_rightListener) {
            super(context, android.R.style.Theme_Translucent_NoTitleBar);
            this.str01 = str01;
            this.str02 = str02;
            this.str03 = str03;
            this.addDialog_leftListener = addDialog_leftListener;
            this.addDialog_rightListener = addDialog_rightListener;
        }
    }

    /***************************************상세정보/수정하기 있는 설정 다이얼로그***********************************************/

    public class SettingDialog extends Dialog {

        private TextView text02, text03;
        private String str02, str03;
        private View.OnClickListener settingDialog_listener02, settingDialog_listener03;
        private LinearLayout llRow02, llRow03;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // 다이얼로그 외부 화면 흐리게 표현
            WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
            lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            lpWindow.dimAmount = 0.8f;
            getWindow().setAttributes(lpWindow);

            //view mapping
            setContentView(R.layout.dialog_setting);

            //findView
            text02 = (TextView) findViewById(R.id.tv02);
            text03 = (TextView) findViewById(R.id.tv03);
            llRow02 = (LinearLayout) findViewById(R.id.llRow02);
            llRow03 = (LinearLayout) findViewById(R.id.llRow03);


            // 제목과 내용을 생성자에서 셋팅
            text02.setText(str02);
            text03.setText(str03);

            // 클릭 이벤트 셋팅
            llRow02.setOnClickListener(settingDialog_listener02);
            llRow03.setOnClickListener(settingDialog_listener03);
        }


        // 생성자
        public SettingDialog(Context context, String str02, String str03,
                             View.OnClickListener settingDialog_listener02,
                             View.OnClickListener settingDialog_listener03) {
            super(context, android.R.style.Theme_Translucent_NoTitleBar);
            this.str02 = str02;
            this.str03 = str03;
            this.settingDialog_listener02 = settingDialog_listener02;
            this.settingDialog_listener03 = settingDialog_listener03;
        }
    }

    /***************************************샹세 정보만 있는 설정 다이얼로그02***********************************************/

    public class SettingDialog02 extends Dialog {

        private TextView text02;
        private String str02;
        private View.OnClickListener settingDialog_listener02;
        private LinearLayout llRow02;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // 다이얼로그 외부 화면 흐리게 표현
            WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
            lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            lpWindow.dimAmount = 0.8f;
            getWindow().setAttributes(lpWindow);

            //view mapping
            setContentView(R.layout.dialog_setting02);

            //findView
            text02 = (TextView) findViewById(R.id.tv02);
            llRow02 = (LinearLayout) findViewById(R.id.llRow02);


            // 제목과 내용을 생성자에서 셋팅
            text02.setText(str02);

            // 클릭 이벤트 셋팅
            llRow02.setOnClickListener(settingDialog_listener02);
        }


        // 생성자
        public SettingDialog02(Context context, String str02,
                               View.OnClickListener settingDialog_listener02) {
            super(context, android.R.style.Theme_Translucent_NoTitleBar);
            this.str02 = str02;
            this.settingDialog_listener02 = settingDialog_listener02;
        }
    }


    /***************************************상세정보 다이얼로그***********************************************/

    public class InfoDialog extends Dialog {


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // 다이얼로그 외부 화면 흐리게 표현
            WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
            lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            lpWindow.dimAmount = 0.8f;
            getWindow().setAttributes(lpWindow);

            //view mapping
            setContentView(R.layout.dialog_info);

            //findView
            TextView tvTags = (TextView) findViewById(R.id.tvTags);
            TextView tvInform = (TextView) findViewById(R.id.tvInform);


            // 제목과 내용을 생성자에서 셋팅
            tvTags.setText(tags);
            tvInform.setText(informs);
        }


        // 생성자
        public InfoDialog(Context context) {
            super(context, android.R.style.Theme_Translucent_NoTitleBar);
        }
    }


    /***************************************툴바에서 뒤로가기 누르면***********************************************/
    public void goBack() {
        RelativeLayout rlback = (RelativeLayout) findViewById(R.id.rlBack);
        rlback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    /***************************************툴바에서 작품담기 누르면 ***********************************************/
    public void addDialog() {
        RelativeLayout rlShare = (RelativeLayout) findViewById(R.id.rlShare);
        rlShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //작품담기 네트워크
                addArticle();

                if(ivShare.getVisibility()==View.INVISIBLE){
                    //작품담기 다이얼로그 생성
                    addWorkDialog = new AddWorkDialog(DetailActivity.this,
                            "#작품 담기",
                            "작품 담기에서 작품을 뺐습니다.",
                            "작품 담기로 이동하시겠습니까?",
                            addDialog_leftListener,
                            addDialog_rightListener);
                }else{
                    //작품담기 다이얼로그 생성
                    addWorkDialog = new AddWorkDialog(DetailActivity.this,
                            "#작품 담기",
                            "작품 담기에 작품을 담았습니다.",
                            "작품 담기로 이동하시겠습니까?",
                            addDialog_leftListener,
                            addDialog_rightListener);
                }

                addWorkDialog.setCanceledOnTouchOutside(true);
                addWorkDialog.show();
            }
        });
    }

    /***************************************툴바에서 설정 누르면***********************************************/
    public void settingDialog() {
        RelativeLayout rlSetting = (RelativeLayout) findViewById(R.id.rlSetting);
        rlSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (modifiable == 1) {
                    //작품을 쓴 사람이 현재 사용자라면
                    settingDialog = new SettingDialog(DetailActivity.this,
                            "상세정보",
                            "수정하기",
                            settingDialog_listener02,
                            settingDialog_listener03);
                    settingDialog.setCanceledOnTouchOutside(true);
                    settingDialog.show();
                } else {
                    //작품을 쓴 쓴 사람이 현재 사용자가 아니라면
                    settingDialog02 = new SettingDialog02(DetailActivity.this,
                            "상세정보",
                            settingDialog_listener04);
                    settingDialog02.setCanceledOnTouchOutside(true);
                    settingDialog02.show();

                }
            }
        });
    }


    /*************************** 업버튼 눌렀을시 시 내용 보기 화면으로 넘어가기 **********************/
    public void clickUpButton() {
        llup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ReadingPoemActivity.class);
                intent.putExtra("articles_id", "" + articleId);
                intent.putExtra("userEmail", userEmail);
                intent.putExtra("loginType", loginType);
                startActivity(intent);
            }
        });
    }

    /*********************************** 작품 상세정보 가져오기*********************************/
    public void getDetail() {
        Call<DetailResult> requestDetail = service.getDetail(loginType, userEmail, articleId);

        requestDetail.enqueue(new Callback<DetailResult>() {
            @Override
            public void onResponse(Call<DetailResult> call, Response<DetailResult> response) {
                if (response.isSuccessful()) {
                    if (response.body().status.equals("success")) {


                        //사진 이미지
                        Glide.with(getApplicationContext())
                                .load(response.body().data.photo)
                                .into(ivPhoto);

                        //유저 이미지
                        Glide.with(getApplicationContext())
                                .load(response.body().data.writer.profile)
                                .into(ivProfile);

                        //유저 닉네임
                        tvName.setText(response.body().data.writer.pen_name.toString());

                        //태그
                        tvTags.setText(response.body().data.tags.toString());

                        //modifiable
                        modifiable = response.body().data.modifiable;

                        //태그정보
                        tags = response.body().data.tags;

                        //추가정보
                        informs = response.body().data.inform;
                    }
                }
            }


            @Override
            public void onFailure(Call<DetailResult> call, Throwable t) {
                Log.i("err", t.getMessage());
            }
        });
    }


    /*********************************** 작품 담기*********************************/
    public void addArticle() {
        Call<AddArticleResult> requestAdd = service.addArticle(loginType, userEmail, articleId);

        requestAdd.enqueue(new Callback<AddArticleResult>() {
            @Override
            public void onResponse(Call<AddArticleResult> call, Response<AddArticleResult> response) {
                if (response.isSuccessful()) {
                    if (response.body().status.equals("success")) {

                        if (response.body().mag.equals("bookmark delete")) {
                            //담기버튼 하얀색 이미지로
                            ivShare.setVisibility(View.VISIBLE);
                            ivShare02.setVisibility(View.INVISIBLE);
                        } else {
                            //담기버튼 파란색 이미지
                            ivShare.setVisibility(View.INVISIBLE);
                            ivShare02.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }


            @Override
            public void onFailure(Call<AddArticleResult> call, Throwable t) {
                Log.i("err", t.getMessage());
            }
        });
    }
}


