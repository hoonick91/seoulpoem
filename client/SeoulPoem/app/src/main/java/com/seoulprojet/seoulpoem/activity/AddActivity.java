package com.seoulprojet.seoulpoem.activity;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.seoulprojet.seoulpoem.R;
import com.seoulprojet.seoulpoem.model.WorkListData;

import java.util.ArrayList;


public class AddActivity extends AppCompatActivity {


    //tool_bar
    private ImageView ivHamberger;
    private TextView tvWorkNum;


    private RecyclerView recyclerView;
    private RecyclerAdapter recyclerAdapter;
    private LinearLayoutManager layoutManager;
    private ArrayList<WorkListData> works;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);


        //findView
        findView();

        //layout manager setting
        recyclerView = (RecyclerView) findViewById(R.id.rvWorks);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);


        //makeDummy
        makeDummy();

        //작품 개수 설정
        tvWorkNum.setText(String.valueOf(works.size()));


        //어뎁터 생성, 리사이클러뷰에 붙임
        recyclerAdapter = new RecyclerAdapter(works);
        recyclerView.setAdapter(recyclerAdapter);

    }


    /***************************************findView***********************************************/
    public void findView() {
        ivHamberger = (ImageView) findViewById(R.id.ivHamberger);
        tvWorkNum = (TextView)findViewById(R.id.tvWorkNum);
    }


    /***************************************dummy data ***********************************************/
    public void makeDummy() {
        works = new ArrayList<>();
        works.add(new WorkListData(R.drawable.testimg, R.drawable.testimg02, "title01", "content01"));
        works.add(new WorkListData(R.drawable.testimg02, R.drawable.testimg03, "title02", "content02"));
        works.add(new WorkListData(R.drawable.testimg03, R.drawable.testimg04, "title03", "content03"));
        works.add(new WorkListData(R.drawable.testimg04, R.drawable.testimg05, "title04", "content04"));
        works.add(new WorkListData(R.drawable.testimg, R.drawable.testimg02, "title05", "content01dsfa"));
        works.add(new WorkListData(R.drawable.testimg02, R.drawable.testimg03, "title06", "content02fsd"));
        works.add(new WorkListData(R.drawable.testimg03, R.drawable.testimg04, "title07", "content03fsadf"));
        works.add(new WorkListData(R.drawable.testimg04, R.drawable.testimg05, "title08", "content04fsaddsa"));
        works.add(new WorkListData(R.drawable.testimg, R.drawable.testimg02, "title09", "content01aa"));
        works.add(new WorkListData(R.drawable.testimg02, R.drawable.testimg03, "title01", "content0210"));
    }


    /***********************************Adapter**********************************/
    class RecyclerAdapter extends RecyclerView.Adapter<MyViewHolder> {

        ArrayList<WorkListData> workListDatas;

        public RecyclerAdapter(ArrayList<WorkListData> workListDatas) {
            this.workListDatas = workListDatas;
        }

        public void setAdapter(ArrayList<WorkListData> workListDatas) {
            this.workListDatas = workListDatas;
            notifyDataSetChanged();
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_work_add, parent, false);
            MyViewHolder viewHolder = new MyViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {
            WorkListData workListData = workListDatas.get(position);


            //leftImage
            holder.ivLeftImg.setImageResource(workListData.leftImg);

            //circleImage
            holder.ivCirclerImg.setImageResource(workListData.circleImg);
            //title
            holder.tvTitle.setText(workListData.title);

            //content
            holder.tvContent.setText(workListData.content);


//            //상세 프로필로 이동
//            //클릭시 상세화면으로 이동, 클릭한 프로젝트 아이디 전달
//            holder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(PeopleActivity.this, OtherUserPage.class);
//                    intent.putExtra("userID", Integer.parseInt(holder.tvUserID.getText().toString()));
//                    Log.d("userID", "people목록에서 보내는 id 값 : " + Integer.toString(userID));
//                    startActivity(intent);
//                }
//            });
        }

        @Override
        public int getItemCount() {
            return works != null ? works.size() : 0;
        }
    }


    /**********************************ViewHolder********************************/

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvContent;
        ImageView ivLeftImg, ivCirclerImg;


        public MyViewHolder(View itemView) {
            super(itemView);

            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvContent = (TextView) itemView.findViewById(R.id.tvContent);
            ivLeftImg = (ImageView) itemView.findViewById(R.id.ivLeftImg);
            ivCirclerImg = (ImageView) itemView.findViewById(R.id.ivCirclerImg);
        }
    }


}
