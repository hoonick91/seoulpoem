package com.seoulprojet.seoulpoem.network;

import android.app.Application;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by pc on 2017-05-09.
 */

public class ApplicationController extends Application {

    //전역
    private static ApplicationController instance;
    private static String baseUrl;
    private NetworkService networkService;


    public static ApplicationController getInstance() {
        return instance;
    }

    public NetworkService getNetworkService() {
        return networkService;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        ApplicationController.instance = this;
        buildService();
    }

    public void buildService() {
        Retrofit.Builder builder = new Retrofit.Builder();
        Retrofit retrofit = builder
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        networkService = retrofit.create(NetworkService.class);
    }
}