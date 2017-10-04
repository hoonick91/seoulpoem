package com.seoulprojet.seoulpoem.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.seoulprojet.seoulpoem.R;
import com.seoulprojet.seoulpoem.component.SeoulPoemApplication;

public class DetailActivity extends AppCompatActivity {

    private ImageView ivShare, ivSetting;   //toolabr
    private CustomDialog mCustomDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        //findView
        findView();

        //photo click
        clickPhoto();

        //go back
        goBack();

        //add work dialog
        addDialog();

        //setting dialog
        settingDialog();
    }


    private View.OnClickListener leftListener = new View.OnClickListener() {
        public void onClick(View v) {
//            Toast.makeText(getApplicationContext(), "왼쪽버튼 클릭",
//                    Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(DetailActivity.this, AddActivity.class);
            startActivity(intent);
            mCustomDialog.dismiss();
        }
    };

    private View.OnClickListener rightListener = new View.OnClickListener() {
        public void onClick(View v) {
            Toast.makeText(getApplicationContext(), "오른쪽버튼 클릭",
                    Toast.LENGTH_SHORT).show();
        }
    };


    /***************************************findView***********************************************/
    public void findView() {
        //toolbar
        ivShare = (ImageView) findViewById(R.id.ivShare);
        ivSetting = (ImageView) findViewById(R.id.ivSetting);
    }


    /***************************************click photo***********************************************/
    public void clickPhoto() {
        LinearLayout llback = (LinearLayout) findViewById(R.id.llPhoto);

        llback.setOnClickListener(new View.OnClickListener() {
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


    /***************************************go back***********************************************/
    public void goBack() {
        LinearLayout llback = (LinearLayout) findViewById(R.id.llBack);
        llback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    /***************************************add work***********************************************/
    public void addDialog() {
        LinearLayout llShare = (LinearLayout) findViewById(R.id.llShare);
        llShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCustomDialog = new CustomDialog(DetailActivity.this,
                        "#작품 담기", //제목
                        "작품 담기에 작품을 담았습니다", // 내용
                        "작품 담기로 이동하시겠습니까", // 내용
                        leftListener, // 왼쪽 버튼 이벤트
                        rightListener); // 오른쪽 버튼 이벤트
                mCustomDialog.show();
            }
        });
    }

    /***************************************setting***********************************************/
    public void settingDialog() {
        LinearLayout llback = (LinearLayout) findViewById(R.id.llSetting);
        llback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                //show dialog
//                SettingDialog settingDialog = new SettingDialog(SeoulPoemApplication.getSeoulPeomApplication());
//                settingDialog.show();
            }
        });
    }


//    /***************************************setting dialog***********************************************/
//
//    public class SettingDialog extends Dialog {
//
//
//        public SettingDialog(Context context) {
//            super(context);
//        }
//
//        @Override
//        protected void onCreate(Bundle savedInstanceState) {
//            super.onCreate(savedInstanceState);
//            requestWindowFeature(Window.FEATURE_NO_TITLE);
//            setContentView(R.layout.dialog_setting);
//
//
//            LinearLayout llMove = (LinearLayout) findViewById(R.id.llMove);
//            //detail 화면으로 이동
//            llMove.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Intent intent = new Intent(SeoulPoemApplication.getSeoulPeomApplication(), AddActivity.class);
//                    startActivity(intent);
//                    dismiss();
//                }
//            });
//
//        }
//    }

    public class CustomDialog extends Dialog {

        private TextView text01, text02, text03;
        private Button mLeftButton, mRightButton;
        String str01, str02, str03;
        private View.OnClickListener mLeftClickListener, mRightClickListener;

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
            mLeftButton = (Button) findViewById(R.id.btnMove);
            mRightButton = (Button) findViewById(R.id.btnBack);

            // 제목과 내용을 생성자에서 셋팅
            text01.setText(str01);
            text02.setText(str02);
            text03.setText(str03);

            // 클릭 이벤트 셋팅
            if (mLeftClickListener != null && mRightClickListener != null) {
                mLeftButton.setOnClickListener(mLeftClickListener);
                mRightButton.setOnClickListener(mRightClickListener);
            } else if (mLeftClickListener != null
                    && mRightClickListener == null) {
                mLeftButton.setOnClickListener(mLeftClickListener);
            } else {

            }
        }


        // 클릭버튼이 확인과 취소 두개일때 생성자 함수로 이벤트를 받는다
        public CustomDialog(Context context, String str01, String str02, String str03,
                            View.OnClickListener leftListener,
                            View.OnClickListener rightListener) {
            super(context, android.R.style.Theme_Translucent_NoTitleBar);
            this.str01 = str01;
            this.str02 = str02;
            this.str03 = str03;
            this.mLeftClickListener = leftListener;
            this.mRightClickListener = rightListener;
        }


    }
}
