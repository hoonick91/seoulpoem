package com.seoulprojet.seoulpoem.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.seoulprojet.seoulpoem.R;

public class DetailActivity extends AppCompatActivity {

    /***************************************변수***********************************************/

    private ImageView ivShare, ivSetting;   //tool bar
    private AddWorkDialog addWorkDialog;     //작품 담기 다이얼로그
    private SettingDialog settingDialog;     //작품 담기 다이얼로그

    //작품담기 다이얼로그 리스너
    private View.OnClickListener addDialog_leftListener = new View.OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent(DetailActivity.this, AddActivity.class);
            startActivity(intent);
            addWorkDialog.dismiss();
        }
    };
    private View.OnClickListener addDialog_rightListener = new View.OnClickListener() {
        public void onClick(View v) {
            addWorkDialog.dismiss();
        }
    };

    //설정 다이얼로그 리스너
    private View.OnClickListener settingDialog_listener01 = new View.OnClickListener() {
        public void onClick(View v) {
            Toast.makeText(DetailActivity.this, "111", Toast.LENGTH_SHORT).show();
        }
    };

    private View.OnClickListener settingDialog_listener02 = new View.OnClickListener() {
        public void onClick(View v) {
            Toast.makeText(DetailActivity.this, "222", Toast.LENGTH_SHORT).show();

        }
    };

    private View.OnClickListener settingDialog_listener03 = new View.OnClickListener() {
        public void onClick(View v) {
            Toast.makeText(DetailActivity.this, "333", Toast.LENGTH_SHORT).show();

        }
    };

    /***************************************START***********************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        //findView
        findView();

        //photo click
        clickPhoto();

        //뒤로가기
        goBack();

        //작품담기 다이얼로그
        addDialog();

        //설정 다이얼로그
        settingDialog();
    }

    /***************************************findView***********************************************/
    public void findView() {
        //toolbar
        ivShare = (ImageView) findViewById(R.id.ivShare);
        ivSetting = (ImageView) findViewById(R.id.ivSetting);
    }


    /***************************************click photo***********************************************/
    public void clickPhoto() {
        LinearLayout llPhoto = (LinearLayout) findViewById(R.id.llPhoto);

        llPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

    /***************************************설정 다이얼로그***********************************************/

    public class SettingDialog extends Dialog {

        private TextView text01, text02, text03;
        private  String str01, str02, str03;
        private View.OnClickListener settingDialog_listener01, settingDialog_listener02, settingDialog_listener03;
        private  LinearLayout llRow01, llRow02, llRow03;

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
            text01 = (TextView) findViewById(R.id.tv01);
            text02 = (TextView) findViewById(R.id.tv02);
            text03 = (TextView) findViewById(R.id.tv03);
            llRow01 = (LinearLayout)findViewById(R.id.llRow01);
            llRow02 = (LinearLayout)findViewById(R.id.llRow02);
            llRow03 = (LinearLayout)findViewById(R.id.llRow03);


            // 제목과 내용을 생성자에서 셋팅
            text01.setText(str01);
            text02.setText(str02);
            text03.setText(str03);

            // 클릭 이벤트 셋팅
            llRow01.setOnClickListener(settingDialog_listener01);
            llRow02.setOnClickListener(settingDialog_listener02);
            llRow03.setOnClickListener(settingDialog_listener03);
        }


        // 생성자
        public SettingDialog(Context context, String str01, String str02, String str03,
                             View.OnClickListener settingDialog_listener01,
                             View.OnClickListener settingDialog_listener02,
                             View.OnClickListener settingDialog_listener03) {
            super(context, android.R.style.Theme_Translucent_NoTitleBar);
            this.str01 = str01;
            this.str02 = str02;
            this.str03 = str03;
            this.settingDialog_listener01 = settingDialog_listener01;
            this.settingDialog_listener02 = settingDialog_listener02;
            this.settingDialog_listener03 = settingDialog_listener03;
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
                addWorkDialog = new AddWorkDialog(DetailActivity.this,
                        "#작품 담기",
                        "작품 담기에 작품을 담았습니다.",
                        "작품 담기로 이동하시겠습니까?",
                        addDialog_leftListener,
                        addDialog_rightListener);
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
                settingDialog = new SettingDialog(DetailActivity.this,
                        "공유하기",
                        "상세정보",
                        "수정하기",
                        settingDialog_listener01,
                        settingDialog_listener02,
                        settingDialog_listener03);
                settingDialog.setCanceledOnTouchOutside(true);
                settingDialog.show();
            }
        });
    }
}
