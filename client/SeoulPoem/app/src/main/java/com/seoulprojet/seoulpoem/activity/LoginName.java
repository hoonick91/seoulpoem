package com.seoulprojet.seoulpoem.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.seoulprojet.seoulpoem.R;
import com.seoulprojet.seoulpoem.model.LoginPenName;
import com.seoulprojet.seoulpoem.model.SignInResult;
import com.seoulprojet.seoulpoem.network.ApplicationController;
import com.seoulprojet.seoulpoem.network.NetworkService;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginName extends AppCompatActivity {

    private NetworkService service;
    private String userEmail;
    private int loginType;
    private String userName;
    private String contentType;
    private RequestBody penName;
    private LoginPenName loginPenName;

    private TextView tempView;
    private ImageButton next_btn;
    private EditText inputName_et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_name);

        // get intent
        Intent intent = getIntent();
        userEmail = intent.getExtras().getString("userEmail");
        loginType = intent.getExtras().getInt("loginType");
        contentType = intent.getExtras().getString("contentType");
        userName = intent.getExtras().getString("penName");

        // find view
        tempView = (TextView)findViewById(R.id.login_name_temp_tv);
        next_btn = (ImageButton)findViewById(R.id.login_name_next_btn);
        inputName_et = (EditText)findViewById(R.id.login_name_et);

        inputName_et.setHint(userName);

        // service
        service = ApplicationController.getInstance().getNetworkService();

        next_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                loginPenName = new LoginPenName();

                if(inputName_et.getText() == null){
                    inputName_et.setText(userName);
                }
                loginPenName.pen_name = inputName_et.getText().toString();
                postName();
            }
        });
    }

    /***************** post name ******************/
    private void postName(){
        Call<SignInResult> requestName = service.postName(loginType, userEmail, loginPenName);

        requestName.enqueue(new Callback<SignInResult>() {
            @Override
            public void onResponse(Call<SignInResult> call, Response<SignInResult> response) {
                if(response.code()==401) {
                    tempView.setText("중복");
                }

                else if(response.code() == 403){
                    tempView.setText("제대로 안 보내짐");
                }

                else if(response.code() == 500){
                    tempView.setText("DBerror");
                }
                else{
                    Intent intent = new Intent(getApplicationContext(), MyPage.class);
                    intent.putExtra("userEmail", userEmail);
                    intent.putExtra("loginType", loginType);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<SignInResult> call, Throwable t) {
                Log.i("call fail", t.getMessage());
            }
        });
    }
}
