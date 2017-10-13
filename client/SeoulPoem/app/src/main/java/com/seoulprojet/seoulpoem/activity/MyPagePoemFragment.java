package com.seoulprojet.seoulpoem.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.seoulprojet.seoulpoem.R;
import com.seoulprojet.seoulpoem.model.PoemListData;

import java.util.ArrayList;

/**
 * Created by lynn on 2017-09-17.
 */

public class MyPagePoemFragment extends Fragment {

    private TextView poemCount;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

      //  view = inflater.inflate(R.layout.mypage_poem_fragment, container, false);
      //  poemCount = (TextView)view.findViewById(R.id.poem_frag_count_txt);

        return inflater.inflate(R.layout.mypage_poem_fragment, container, false);
    }


}
