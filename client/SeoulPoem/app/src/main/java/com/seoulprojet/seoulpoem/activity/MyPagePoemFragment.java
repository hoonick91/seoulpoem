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

    private RecyclerView recyclerView;
    private RecyclerAdapter recyclerAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<PoemListData> poemListDatas;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

      View view = inflater.inflate(R.layout.mypage_poem_fragment, container, false);
      poemCount = (TextView)view.findViewById(R.id.poem_frag_count_txt);

        recyclerView = (RecyclerView)view.findViewById(R.id.poem_frag_rv);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        // make dummy
        makeDummy();

        // make adapter
        recyclerAdapter = new RecyclerAdapter(poemListDatas);
        recyclerView.setAdapter(recyclerAdapter);

        poemCount.setText("# 총 " + recyclerAdapter.getItemCount() + "개");

        return view;
    }

    //************dummy*************//
    public void makeDummy(){
        poemListDatas = new ArrayList<>();
        poemListDatas.add(new PoemListData("너와 나"));
        poemListDatas.add(new PoemListData("나야 나"));
        poemListDatas.add(new PoemListData("활활"));
    }

    /******************** adapter *****************************/
    class RecyclerAdapter extends RecyclerView.Adapter<MyViewHolder>{
        ArrayList<PoemListData> poemListDatas;

        public RecyclerAdapter(ArrayList<PoemListData> poemListDatas){
            this.poemListDatas = poemListDatas;
        }

        public void setAdapter(ArrayList<PoemListData> poemListDatas){
            this.poemListDatas = poemListDatas;
            notifyDataSetChanged();
        }

        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_poem_title, parent, false);
            MyViewHolder viewHolder = new MyViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            PoemListData poemListData = poemListDatas.get(position);

            holder.poemTitle.setText(poemListData.poemTitle);
        }

        @Override
        public int getItemCount() {
            return poemListDatas != null ? poemListDatas.size() : 0;
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
}
