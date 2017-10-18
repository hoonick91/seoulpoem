package com.seoulprojet.seoulpoem.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.seoulprojet.seoulpoem.R;

/**
 * Created by lynn on 2017-09-17.
 */

public class MyPagePhotoFragment extends Fragment{

    private TextView numTV;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.mypage_photo_fragment, container, false);
        numTV = (TextView)view.findViewById(R.id.mypage_photo_num_tv);

        return view;
    }
}
