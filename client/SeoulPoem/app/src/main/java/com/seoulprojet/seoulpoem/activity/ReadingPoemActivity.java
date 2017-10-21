package com.seoulprojet.seoulpoem.activity;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.seoulprojet.seoulpoem.R;
import com.seoulprojet.seoulpoem.model.ReadingPoem;
import com.seoulprojet.seoulpoem.network.ApplicationController;
import com.seoulprojet.seoulpoem.network.NetworkService;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.graphics.Typeface.BOLD;
import static android.graphics.Typeface.BOLD_ITALIC;
import static android.graphics.Typeface.ITALIC;
import static android.graphics.Typeface.NORMAL;

public class ReadingPoemActivity extends AppCompatActivity {

    //네트워킹
    NetworkService service;
    int articles_id;

    TextView poem_title;
    TextView poem_content;
    ImageView poem_img;
    ImageButton poem_path;
    ImageButton download;
    ImageButton another;
    RelativeLayout title_layout;
    TextView writer_name;
    TextView poem_tags;
    CircleImageView writer_img;
    LinearLayout reading_background;
    RecyclerView recyclerPoem;
    MyRecyclerAdapter recyclerAdapter;

    boolean openPoem = false;
    boolean style_check[];
    String photo;
    ArrayList<ReadingPoem.Otherinfo> another_photo;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading_poem);

        String temp = getIntent().getStringExtra("articles_id");
        articles_id  = Integer.parseInt(temp);
        setId();
        setRecycularView();
        setClick();
        initNetwork();
        getInfo();

    }

    private void setId(){
        poem_title = (TextView)findViewById(R.id.poem_title);
        poem_content = (TextView)findViewById(R.id.poem_content);
        poem_img = (ImageView)findViewById(R.id.poem_img);
        poem_path = (ImageButton)findViewById(R.id.poem_path);
        another = (ImageButton)findViewById(R.id.another);
        title_layout = (RelativeLayout)findViewById(R.id.title_layout);
        writer_name = (TextView)findViewById(R.id.writer_name);
        poem_tags = (TextView)findViewById(R.id.poem_tags);
        writer_img = (CircleImageView)findViewById(R.id.writer_img);
        reading_background = (LinearLayout)findViewById(R.id.reading_background);
        recyclerPoem = (RecyclerView)findViewById(R.id.recyclerPoem);
        download = (ImageButton)findViewById(R.id.download);

        style_check = new boolean[]{false,false,false};

    }

    //버튼 클릭 모션
    private void setClick(){
        poem_path.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!openPoem) {
                    ViewGroup.LayoutParams params = poem_img.getLayoutParams();
                    params.height = poem_img.getHeight() / 4;
                    poem_img.setLayoutParams(params);
                    poem_img.setScaleType(ImageView.ScaleType.CENTER_CROP);

                    RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams) title_layout.getLayoutParams();
                    params2.topMargin = title_layout.getTop() / 3;
                    title_layout.setLayoutParams(params2);

                    poem_title.setTextSize(18);
                    poem_path.setImageResource(R.drawable.path3);
                    openPoem = true;
                }else{
                    ViewGroup.LayoutParams params = poem_img.getLayoutParams();
                    params.height = poem_img.getHeight() * 4;
                    poem_img.setLayoutParams(params);
                    poem_img.setScaleType(ImageView.ScaleType.FIT_CENTER);

                    RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams) title_layout.getLayoutParams();
                    params2.topMargin = title_layout.getTop() * 3;
                    title_layout.setLayoutParams(params2);

                    poem_title.setTextSize(30);
                    poem_path.setImageResource(R.drawable.path2);
                    openPoem = false;
                }

            }
        });
        another.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //더보기 dailog
            }
        });
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //다운받기 dialog
            }
        });
    }

    /****************************************네트워크 초기화*****************************************/
    private void initNetwork() {
        service = ApplicationController.getInstance().getNetworkService();
    }

    /****************************************서버통신 정보 받음**************************************/
    private void getInfo(){

        Call<ReadingPoem> request = service.readPoem("godz33@naver.com",1, articles_id);
        request.enqueue(new Callback<ReadingPoem>() {
            @Override
            public void onResponse(Call<ReadingPoem> call, Response<ReadingPoem> response) {
                if (response.isSuccessful()) {
                    poem_title.setText(response.body().article.title);
                    poem_content.setText(response.body().article.content);

                    photo = response.body().article.photo;
                    Glide.with(ReadingPoemActivity.this)
                            .load(photo)
                            .into(poem_img);
                    Glide.with(ReadingPoemActivity.this)
                            .load(response.body().article.user.profile)
                            .into(writer_img);
                    poem_tags.setText(response.body().article.tags);
                    writer_name.setText(response.body().article.user.pen_name);
                    if(response.body().article.background.equals("4"))
                        reading_background.setBackgroundResource(R.drawable.paper4);
                    else if(response.body().article.background.equals("3"))
                        reading_background.setBackgroundResource(R.drawable.paper3);
                    else if(response.body().article.background.equals("2"))
                        reading_background.setBackgroundResource(R.drawable.paper2);
                    else
                        reading_background.setBackgroundResource(R.drawable.paper1);

                    if (response.body().article.setting.bold == 1){
                        if( response.body().article.setting.inclination == 1){
                            poem_content.setTypeface(null, BOLD_ITALIC);
                            style_check[0] = true;
                            style_check[1] = true;

                        }else {
                            style_check[0] = true;
                            poem_content.setTypeface(null, BOLD);
                        }
                    }else{
                        if( response.body().article.setting.inclination == 1){
                            style_check[1] = true;
                            poem_content.setTypeface(null, ITALIC);
                        }else {
                            poem_content.setTypeface(null, NORMAL);
                        }
                    };

                    if(response.body().article.setting.underline == 1){
                        style_check[2] = true;
                        poem_content.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
                    }
                    else {
                        poem_content.setPaintFlags(0);
                    }

                    if(response.body().article.setting.color == 1)
                        poem_content.setTextColor(Color.parseColor("#ffffff"));
                    else if(response.body().article.setting.color == 2)
                        poem_content.setTextColor(Color.parseColor("#773511"));
                    else  if(response.body().article.setting.color == 3)
                        poem_content.setTextColor(Color.parseColor("#765745"));
                    else  if(response.body().article.setting.color == 4)
                        poem_content.setTextColor(Color.parseColor("#888888"));
                    else
                        poem_content.setTextColor(Color.parseColor("#000000"));

                    if(response.body().article.setting.sort==1){
                        poem_content.setGravity(Gravity.LEFT);
                    }else if(response.body().article.setting.sort==2){
                        poem_content.setGravity(Gravity.RIGHT);
                    }else if(response.body().article.setting.sort ==3){
                        poem_content.setGravity(Gravity.CENTER_HORIZONTAL);
                    }else{
                        poem_content.setGravity(Gravity.NO_GRAVITY);
                    };

                        another_photo = response.body().article.user.others;
                    recyclerAdapter.setAdapter(another_photo);
                    recyclerAdapter.notifyDataSetChanged();



                } else {
                    Log.e("err",response.message());
                }
            }

            @Override
            public void onFailure(Call<ReadingPoem> call, Throwable t) {

            }
        });

    }
    /**********************리사이클러 뷰 셋팅작업  여기서부터 끝까지 ******************************/
    private void setRecycularView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerPoem.setLayoutManager(layoutManager);

        another_photo = new ArrayList<>();
        recyclerAdapter = new MyRecyclerAdapter(another_photo);
        recyclerPoem.setAdapter(recyclerAdapter);
    }

    /***********************************Adapter**********************************/
    class MyRecyclerAdapter extends RecyclerView.Adapter<MyViewHolder> {

        ArrayList<ReadingPoem.Otherinfo> another_photo;

        public MyRecyclerAdapter(ArrayList<ReadingPoem.Otherinfo> another_photo) {
            this.another_photo = another_photo;
        }

        public void setAdapter(ArrayList<ReadingPoem.Otherinfo> another_photo) {
            this.another_photo = another_photo;
            notifyDataSetChanged();
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_another_poem, parent, false);
            MyViewHolder viewHolder = new MyViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {
            ReadingPoem.Otherinfo otherinfo = another_photo.get(position);

            Glide.with(ReadingPoemActivity.this)
                    .load(otherinfo.photo)
                    .into(holder.another_poem_img);
            holder.itemView.setTag(otherinfo.idarticles);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ReadingPoemActivity.this, ReadingPoemActivity.class);
                    intent.putExtra("articles_id",holder.itemView.getTag().toString());
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return another_photo.size();
        }
    }


    /**********************************ViewHolder********************************/

    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView another_poem_img;

        public MyViewHolder(View itemView) {
            super(itemView);
            another_poem_img = (ImageView)itemView.findViewById(R.id.another_poem_img);
        }
    }



}
