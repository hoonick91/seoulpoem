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
    private String contentType;
    private RequestBody penName;

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
        loginType = intent.getExtras().getInt("type");
        contentType = intent.getExtras().getString("contentType");

        // find view
        tempView = (TextView)findViewById(R.id.login_name_temp_tv);
        next_btn = (ImageButton)findViewById(R.id.login_name_next_btn);
        inputName_et = (EditText)findViewById(R.id.login_name_et);

        // service
        service = ApplicationController.getInstance().getNetworkService();

        next_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                penName = RequestBody.create(MediaType.parse("part/x-www-form-urlencoded"), "" + inputName_et.getText().toString());
                postName();
            }
        });
    }

    /***************** post name ******************/
    private void postName(){
        Call<SignInResult> requestName = service.postName(loginType, userEmail, penName);

        requestName.enqueue(new Callback<SignInResult>() {
            @Override
            public void onResponse(Call<SignInResult> call, Response<SignInResult> response) {
                if(response.isSuccessful()){
                    tempView.setText(response.body().status);
                }
                else{
                    Log.i("", "응답코드 " + response.code());
                }
            }

            @Override
            public void onFailure(Call<SignInResult> call, Throwable t) {
                Log.i("call fail", t.getMessage());
            }
        });
    }
}
