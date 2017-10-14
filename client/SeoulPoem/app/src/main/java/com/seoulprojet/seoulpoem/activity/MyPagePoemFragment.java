package com.seoulprojet.seoulpoem.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.seoulprojet.seoulpoem.R;
import com.seoulprojet.seoulpoem.model.MyPagePoemListData;
import com.seoulprojet.seoulpoem.model.MyPagePoemResult;
import com.seoulprojet.seoulpoem.network.ApplicationController;
import com.seoulprojet.seoulpoem.network.NetworkService;

import java.lang.reflect.Array;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by lynn on 2017-09-17.
 */

public class MyPagePoemFragment extends Fragment {

    private TextView poemCount;

    private RecyclerView recyclerView;
    private RecyclerAdapter recyclerAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private int poemNum = 0;

    // network
    NetworkService service;
    private ArrayList<MyPagePoemResult> myPagePoemResults;
    private ArrayList<MyPagePoemListData> myPagePoemListDatas;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

      View view = inflater.inflate(R.layout.mypage_poem_fragment, container, false);
      poemCount = (TextView)view.findViewById(R.id.poem_frag_count_txt);

        recyclerView = (RecyclerView)view.findViewById(R.id.poem_frag_rv);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        // service
       // service = ApplicationController.getInstance().getNetworkService();

        // networking
        // myPagePoemListDatas = new ArrayList<>();
        // getMyPoems();

        // make adapter
        recyclerAdapter = new RecyclerAdapter(myPagePoemListDatas);
        recyclerView.setAdapter(recyclerAdapter);
        poemCount.setText("# 총 " + poemNum + "개");

        return view;
    }

    //************dummy*************//
    /*public void makeDummy(){
        myPagePoemListDatas = new ArrayList<>();
        myPagePoemListDatas.add(new MyPagePoemListData(1, "너와 나"));
        myPagePoemListDatas.add(new MyPagePoemListData(2, "나야 나"));
        myPagePoemListDatas.add(new MyPagePoemListData(3, "활활"));
    }*/

    /******************** adapter *****************************/
    class RecyclerAdapter extends RecyclerView.Adapter<MyViewHolder>{
        ArrayList<MyPagePoemListData> myPagePoemListDatas;

        public RecyclerAdapter(ArrayList<MyPagePoemListData> myPagePoemListDatas){
            this.myPagePoemListDatas = myPagePoemListDatas;
        }

        public void setAdapter(ArrayList<MyPagePoemListData> myPagePoemListDatas){
            this.myPagePoemListDatas = myPagePoemListDatas;
            notifyDataSetChanged();
        }

        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_poem_title, parent, false);
            MyViewHolder viewHolder = new MyViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            MyPagePoemListData myPagePoemListData = myPagePoemListDatas.get(position);

            holder.poemTitle.setText(myPagePoemListData.title);
        }

        @Override
        public int getItemCount() {
            return myPagePoemListDatas != null ? myPagePoemListDatas.size() : 0;
        }
    }

    /********************* view holder ************************/
    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView poemTitle;

        public MyViewHolder(View itemView){
            super(itemView);

            poemTitle = (TextView)itemView.findViewById(R.id.item_poem_title_txt);
        }
    }

    /****************** 시 리스트 가져오기 **********************/

}
