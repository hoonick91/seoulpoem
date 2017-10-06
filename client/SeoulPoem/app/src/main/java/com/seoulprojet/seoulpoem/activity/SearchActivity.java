package com.seoulprojet.seoulpoem.activity;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.seoulprojet.seoulpoem.R;
import com.seoulprojet.seoulpoem.model.SearchListData;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    RelativeLayout rlSearch, rlbg;
    TextView tv01, tv02;
    EditText et;

    //recycler
    private RecyclerView recyclerView;
    private RecyclerAdapter recyclerAdapter;
    private LinearLayoutManager layoutManager;
    private ArrayList<SearchListData> results;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //find view
        findView();

        //뒤로 가기
        toBack();

        //배경 누르면
        onTouchBg();

        //검색하면
        settingTextWatcher();


        //layout manager setting
        recyclerView = (RecyclerView) findViewById(R.id.rvWrites);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);


        //makeDummy
        makeDummy();

        //어뎁터 생성, 리사이클러뷰에 붙임
        recyclerAdapter = new RecyclerAdapter(results);
        recyclerView.setAdapter(recyclerAdapter);


    }

    /***************************************findView***********************************************/
    public void findView() {

        rlSearch = (RelativeLayout) findViewById(R.id.rlSearch);
        rlbg = (RelativeLayout) findViewById(R.id.rlbg);
        et = (EditText) findViewById(R.id.et);
        tv01 = (TextView) findViewById(R.id.tv01);
        tv02 = (TextView) findViewById(R.id.tv02);

    }


    /***********************************뒤로가기**********************************/
    public void toBack() {
        rlSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    /***********************************배경 터치**********************************/
    public void onTouchBg() {
        rlbg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
            }
        });
    }

    /***********************************검색하면**********************************/
    public void settingTextWatcher() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                tv01.setVisibility(View.INVISIBLE);
                tv02.setVisibility(View.INVISIBLE);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().equals("")) {
                    tv01.setVisibility(View.VISIBLE);
                    tv02.setVisibility(View.VISIBLE);
                }
            }
        };

        et.addTextChangedListener(textWatcher);

    }


    /***************************************dummy data ***********************************************/
    public void makeDummy() {
        results = new ArrayList<>();
        results.add(new SearchListData(R.drawable.testimg, "구덧흐얌", 13, 10, "구슬모아 당구장", "구슬모아구슬모아"));
        results.add(new SearchListData(R.drawable.testimg02, "구덧흐얌", 13, 10, "구슬모아 당구장", "구슬모아구슬모아"));
        results.add(new SearchListData(R.drawable.testimg03, "구덧흐얌", 13, 10, "구슬모아 당구장", "구슬모아구슬모아"));
        results.add(new SearchListData(R.drawable.testimg04, "구덧흐얌", 13, 10, "구슬모아 당구장", "구슬모아구슬모아"));
        results.add(new SearchListData(R.drawable.testimg05, "구덧흐얌", 13, 10, "구슬모아 당구장", "구슬모아구슬모아"));
        results.add(new SearchListData(R.drawable.testimg, "구덧흐얌", 13, 10, "구슬모아 당구장", "구슬모아구슬모아"));
    }


    /***********************************Adapter**********************************/
    class RecyclerAdapter extends RecyclerView.Adapter<MyViewHolder> {

        ArrayList<SearchListData> searchListDatas;

        public RecyclerAdapter(ArrayList<SearchListData> searchListDatas) {
            this.searchListDatas = searchListDatas;
        }

        public void setAdapter(ArrayList<SearchListData> searchListDatas) {
            this.searchListDatas = searchListDatas;
            notifyDataSetChanged();
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_writer, parent, false);
            MyViewHolder viewHolder = new MyViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {
            SearchListData searchListData = searchListDatas.get(position);

//
//            //leftImage
//            holder.ivLeftImg.setImageResource(workListData.leftImg);
//
//            //circleImage
//            holder.ivCirclerImg.setImageResource(workListData.circleImg);
//            //title
//            holder.tvTitle.setText(workListData.title);
//
//            //content
//            holder.tvContent.setText(workListData.content);


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
            return results != null ? results.size() : 0;
        }
    }


    /**********************************ViewHolder********************************/

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvPoemNum, tvPhotoNum;
        ImageView writerImg;


        public MyViewHolder(View itemView) {
            super(itemView);

            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvPhotoNum = (TextView) itemView.findViewById(R.id.tvPhotoNum);
            tvPoemNum = (TextView) itemView.findViewById(R.id.tvPoemNum);

        }
    }


}
