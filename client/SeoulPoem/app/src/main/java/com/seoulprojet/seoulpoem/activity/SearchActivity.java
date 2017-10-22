package com.seoulprojet.seoulpoem.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.seoulprojet.seoulpoem.R;
import com.seoulprojet.seoulpoem.model.SearchListDataArticle;
import com.seoulprojet.seoulpoem.model.SearchListDataAuthor;
import com.seoulprojet.seoulpoem.model.SearchResult;
import com.seoulprojet.seoulpoem.network.ApplicationController;
import com.seoulprojet.seoulpoem.network.NetworkService;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {

    RelativeLayout rlSearch, rlbg;
    TextView tv01, tv02;
    EditText et;

    //recycler
    private RecyclerView rvWriter, rvTtile;
    private RecyclerAdapterWriter raWriter;
    private RecyclerAdapterTitle raTtile;
    private LinearLayoutManager layoutManager01, layoutManager02;
    private ArrayList<SearchListDataAuthor> authors;
    private ArrayList<SearchListDataArticle> articles;
    private LinearLayout llTv01, llTv02, llRv01, llRv02;
    private RelativeLayout rlSearchButton;


    //네트워크
    NetworkService service;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


        //서비스 객체 초기화
        service = ApplicationController.getInstance().getNetworkService();

        //find view
        findView();

        //뒤로 가기
        toBack();

        //배경 누르면
        onTouchBg();


        //작가별 리사이클러뷰 설정
        rvWriter = (RecyclerView) findViewById(R.id.rvWrites);
        layoutManager01 = new LinearLayoutManager(this);
        layoutManager01.setOrientation(LinearLayoutManager.VERTICAL);
        rvWriter.setLayoutManager(layoutManager01);
        raWriter = new RecyclerAdapterWriter(authors);
        rvWriter.setAdapter(raWriter);


        //제목별 리사이클러뷰 설정
        rvTtile = (RecyclerView) findViewById(R.id.rvTitile);
        layoutManager02 = new LinearLayoutManager(this);
        layoutManager02.setOrientation(LinearLayoutManager.VERTICAL);
        rvTtile.setLayoutManager(layoutManager02);
        raTtile = new RecyclerAdapterTitle(articles);
        rvTtile.setAdapter(raTtile);


        authors = new ArrayList<>();
        articles = new ArrayList<>();

        //검색 버튼 누르면
        search();

    }

    /***************************************findView***********************************************/
    public void findView() {

        rlSearch = (RelativeLayout) findViewById(R.id.rlSearch);
        rlbg = (RelativeLayout) findViewById(R.id.rlbg);
        et = (EditText) findViewById(R.id.et);
        tv01 = (TextView) findViewById(R.id.tv01);
        tv02 = (TextView) findViewById(R.id.tv02);
        llTv01 = (LinearLayout) findViewById(R.id.llText01);
        llTv02 = (LinearLayout) findViewById(R.id.llText02);
        llRv01 = (LinearLayout) findViewById(R.id.llRv01);
        llRv02 = (LinearLayout) findViewById(R.id.llRv02);
        rlSearchButton = (RelativeLayout) findViewById(R.id.rlSearchButton);
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


    /***********************************Adapter**********************************/
    class RecyclerAdapterWriter extends RecyclerView.Adapter<MyViewHolderWriter> {

        ArrayList<SearchListDataAuthor> searchListDataAuthors;

        public RecyclerAdapterWriter(ArrayList<SearchListDataAuthor> searchListDataAuthors) {
            this.searchListDataAuthors = searchListDataAuthors;
        }

        public void setAdapter(ArrayList<SearchListDataAuthor> searchListDataAuthors) {
            this.searchListDataAuthors = searchListDataAuthors;
            notifyDataSetChanged();
        }

        @Override
        public MyViewHolderWriter onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_writer, parent, false);
            MyViewHolderWriter viewHolder = new MyViewHolderWriter(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolderWriter holder, int position) {
            SearchListDataAuthor searchListDataAuthor = searchListDataAuthors.get(position);


            //사진 이미지
            Glide.with(getApplicationContext())
                    .load(searchListDataAuthor.profile)
                    .into(holder.ivLeftImg);

            //title
            holder.tvName.setText(searchListDataAuthor.name_);

            //content
            holder.tvPhotoNum.setText(String.valueOf(searchListDataAuthor.ac));


            //content
            holder.tvPoemNum.setText(String.valueOf(searchListDataAuthor.pc));


        }

        @Override
        public int getItemCount() {
            return authors != null ? authors.size() : 0;
        }
    }


    /**********************************ViewHolder********************************/

    class MyViewHolderWriter extends RecyclerView.ViewHolder {

        ImageView ivLeftImg;
        TextView tvName, tvPhotoNum, tvPoemNum;


        public MyViewHolderWriter(View itemView) {
            super(itemView);
            ivLeftImg = (ImageView) itemView.findViewById(R.id.ivLeftImg);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvPhotoNum = (TextView) itemView.findViewById(R.id.tvPhotoNum);
            tvPoemNum = (TextView) itemView.findViewById(R.id.tvPoemNum);

        }
    }


    /***********************************Adapter**********************************/
    class RecyclerAdapterTitle extends RecyclerView.Adapter<MyViewHolderTitle> {

        ArrayList<SearchListDataArticle> searchListDataArticles;

        public RecyclerAdapterTitle(ArrayList<SearchListDataArticle> searchListDataArticles) {
            this.searchListDataArticles = searchListDataArticles;
        }

        public void setAdapter(ArrayList<SearchListDataArticle> searchListDataArticles) {
            this.searchListDataArticles = searchListDataArticles;
            notifyDataSetChanged();
        }

        @Override
        public MyViewHolderTitle onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_title, parent, false);
            MyViewHolderTitle viewHolder = new MyViewHolderTitle(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolderTitle holder, int position) {
            SearchListDataArticle searchListDataArticle = searchListDataArticles.get(position);


            Log.d("test",searchListDataArticle.title );
            Log.d("test",searchListDataArticle.contents );
            holder.tvTitle.setText(searchListDataArticle.title);
            holder.tvContent.setText(searchListDataArticle.contents);
            holder.itemView.setTag(searchListDataArticle.idarticles);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(),ReadingPoemActivity.class);
                    intent.putExtra("articles_id",holder.itemView.getTag().toString());
                    startActivity(intent);
                }
            });

        }

        @Override
        public int getItemCount() {
            return articles != null ? articles.size() : 0;
        }
    }


    /**********************************ViewHolder********************************/

    class MyViewHolderTitle extends RecyclerView.ViewHolder {

        TextView tvTitle, tvContent;


        public MyViewHolderTitle(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitlee);
            tvContent = (TextView) itemView.findViewById(R.id.tvContentt);

        }
    }


    /***********************************검색 결과 가져오기*********************************/
    public void getResults(String tag) {
        Call<SearchResult> requestSearchResult = service.getSearchResults(tag);

        requestSearchResult.enqueue(new Callback<SearchResult>() {
            @Override
            public void onResponse(Call<SearchResult> call, Response<SearchResult> response) {
                if (response.isSuccessful()) {
                    if (response.body().status.equals("success")) {

                        if(response.body().author_list.size()==0 && response.body().article_list.size()==0 ) {
                            llTv01.setVisibility(View.VISIBLE);
                            llTv02.setVisibility(View.VISIBLE);
                        }else if(response.body().author_list.size()!=0 && response.body().article_list.size()==0 ) {
                            llTv02.setVisibility(View.VISIBLE);
                        }else if(response.body().author_list.size()==0 && response.body().article_list.size()!=0 ){
                            llTv01.setVisibility(View.VISIBLE);
                        }else{
                            llTv01.setVisibility(View.INVISIBLE);
                            llTv02.setVisibility(View.INVISIBLE);
                        }


                        llRv01.setVisibility(View.VISIBLE);
                        llRv02.setVisibility(View.VISIBLE);

                        authors = response.body().author_list;
                        raWriter.setAdapter(authors);

                        articles = response.body().article_list;
                        raTtile.setAdapter(articles);

                    }
                }
            }


            @Override
            public void onFailure(Call<SearchResult> call, Throwable t) {
                Log.i("err", t.getMessage());
            }
        });
    }


    /***********************************검색 버튼 클릭**********************************/
    public void search() {
        rlSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getResults(et.getText().toString());
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
            }
        });
    }


}
