package com.seoulprojet.seoulpoem.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.seoulprojet.seoulpoem.R;
import com.seoulprojet.seoulpoem.model.LoginResult;
import com.seoulprojet.seoulpoem.network.ApplicationController;
import com.seoulprojet.seoulpoem.network.NetworkService;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    private SignInButton google_btn;
    private int RC_SIGN_IN = 1000;
    private String TAG = "TAG";
    private TextView mStatusTextView;
    private String idToken = null;

    private String userGoogleEmail = null;
    private int loginType = -1;
    private String contentType = null;
    private String penName = null;

    private NetworkService service;
    private LoginResult loginResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // service
        service = ApplicationController.getInstance().getNetworkService();

        // google login
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        google_btn = (SignInButton) findViewById(R.id.sign_in_button);
        google_btn.setSize(SignInButton.SIZE_WIDE);
        google_btn.setScopes(gso.getScopeArray());

        findViewById(R.id.sign_in_button).setOnClickListener(this);
        mStatusTextView = (TextView) findViewById(R.id.login_tv);

        // facebook login
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);

        }
    }




    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticafted UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            // idToken = acct.getIdToken();
            // mStatusTextView.setText("ID token : " + idToken);
            penName = acct.getDisplayName();
            userGoogleEmail = acct.getEmail();
            Log.e("", "userEmail : " + userGoogleEmail);
            loginType = 1;


            postLogin();
            updateUI(true);
        } else {

            // Signed out, show unauthenticated UI.
            // mStatusTextView.setText("ID token : null");
            updateUI(false);
        }
    }

    private void updateUI(boolean signedIn) {
        if (signedIn) {
            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            mStatusTextView.setText(userGoogleEmail);
            Log.i("login success", "google success");
        } else {
            // findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            Log.i("login fail", "google fail");
        }
    }

    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getApplicationContext(), "" + connectionResult, Toast.LENGTH_SHORT).show();
    }

    /*************** post google login *******************/
    private void postLogin() {
        Call<LoginResult> requestLogin = service.postLogin(userGoogleEmail, loginType, "application/x-www-form-urlencoded");

        requestLogin.enqueue(new Callback<LoginResult>() {
            @Override
            public void onResponse(Call<LoginResult> call, Response<LoginResult> response) {
                if (response.isSuccessful()) {
                    loginResults = new LoginResult();
                    loginResults = response.body();

                    Log.i("success response", "success response");
                    Log.i("status", "status" + response.body().status);

                    // login 상태가 fail일 경우 회원가입(필명 입력)으로 이동
                    if(loginResults.status.equals("fail")){

                        Intent intent = new Intent(getApplicationContext(), LoginName.class);

                        intent.putExtra("userEmail", userGoogleEmail);
                        intent.putExtra("loginType", loginType);
                        intent.putExtra("penName", penName);
                        intent.putExtra("contentType", "application/x-www-form-urlencoded");

                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.putExtra("userEmail", userGoogleEmail);
                        intent.putExtra("loginType", loginType);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    Log.i("fail response", "code : " + response.code());
                }
            }

            @Override
            public void onFailure(Call<LoginResult> call, Throwable t) {
                Log.i("network fail", t.getMessage());
            }
        });
    }
}
