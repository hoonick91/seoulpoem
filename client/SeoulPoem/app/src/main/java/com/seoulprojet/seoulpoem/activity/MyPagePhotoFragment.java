package com.seoulprojet.seoulpoem.activity;

import android.app.Application;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.seoulprojet.seoulpoem.R;
import com.seoulprojet.seoulpoem.model.MyPagePhotoListData;
import com.seoulprojet.seoulpoem.model.MyPagePhotoResult;
import com.seoulprojet.seoulpoem.network.ApplicationController;
import com.seoulprojet.seoulpoem.network.NetworkService;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by lynn on 2017-09-17.
 */

public class MyPagePhotoFragment extends Fragment{

    private TextView numTV;
    private View view;
    private String userEmail = null;
    private int loginType = 0;
    // network
    private NetworkService service;
    private ArrayList<MyPagePhotoListData> photoListDatas;

    // gridview
    GridView gridView;
    GridViewAdapter gridViewAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {


        view = inflater.inflate(R.layout.mypage_photo_fragment, container, false);
        numTV = (TextView)view.findViewById(R.id.mypage_photo_num_tv);

        gridView = (GridView)view.findViewById(R.id.mypage_photo_gv);

        Bundle extra = getArguments();
        userEmail = extra.getString("userEmail");
        loginType = extra.getInt("loginType");


        // service
        service = ApplicationController.getInstance().getNetworkService();
        photoListDatas = new ArrayList<>();
        getPhoto();

        return view;
    }

    /***************** grid view adapter ********************/
    private class GridViewAdapter extends BaseAdapter{
        ArrayList<MyPagePhotoListData> photoListDatas;
        Context mContext;

        private GridViewAdapter(Context context, ArrayList<MyPagePhotoListData> photoListDatas){
            //inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.photoListDatas = photoListDatas;
            this.mContext = context;
        }

        @Override
        public int getCount() {
            return photoListDatas.size();
        }

        @Override
        public Object getItem(int position) {
            return photoListDatas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View grid;
            ImageView imageView;

            grid = LayoutInflater.from(mContext).inflate(R.layout.list_item_mypage_photo, null);

            if(photoListDatas.get(position).idpoem < 0){
                ImageView hamburgerView = (ImageView)grid.findViewById(R.id.item_mypage_hamburger);
                hamburgerView.setVisibility(grid.VISIBLE);
            }

            imageView = (ImageView)grid.findViewById(R.id.item_mypage_myphoto);
            Glide.with(grid.getContext())
                    .load(photoListDatas.get(position).photo)
                    .into(imageView);

            return grid;
        }
    }

    /***************** 사진 리스트 가져오기 *****************/
    private void getPhoto(){
        Call<MyPagePhotoResult> requestPhoto = service.getMyPhoto(userEmail, loginType);

        requestPhoto.enqueue(new Callback<MyPagePhotoResult>() {
            @Override
            public void onResponse(Call<MyPagePhotoResult> call, Response<MyPagePhotoResult> response) {
                if(response.isSuccessful()){
                    Log.i("success network", "");
                    photoListDatas = response.body().msg.photos;
                    numTV.setText("# 총 " + response.body().msg.counts + "장");

                    // adapter
                    gridViewAdapter = new GridViewAdapter(getActivity().getApplicationContext(), photoListDatas);
                    gridView.setAdapter(gridViewAdapter);
                }
            }

            @Override
            public void onFailure(Call<MyPagePhotoResult> call, Throwable t) {
                Log.i("my page photo error", t.getMessage());
            }
        });
    }
}
