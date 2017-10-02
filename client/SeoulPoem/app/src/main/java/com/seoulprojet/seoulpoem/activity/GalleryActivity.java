package com.seoulprojet.seoulpoem.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.seoulprojet.seoulpoem.R;
import com.seoulprojet.seoulpoem.model.GalleryListData;

import java.util.ArrayList;

public class GalleryActivity extends AppCompatActivity {


    //tool_bar
    private ImageView ivHamberger, ivPictures, ivSearch;
    private TextView tvPlaceName;

    private RecyclerView recyclerView;
    private RecyclerAdapter recyclerAdapter;
    private GridLayoutManager layoutManager;

    private ArrayList<GalleryListData> gallerys;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        //findView
        findView();


        //makeDummy
        makeDummy();

        //레이아웃 매니저 설정
        recyclerView = (RecyclerView) findViewById(R.id.rvGallery);
        layoutManager = new GridLayoutManager(GalleryActivity.this, 3);
        recyclerView.setLayoutManager(layoutManager);

        //어뎁터 생성, 리사이클러뷰에 붙임
        recyclerAdapter = new RecyclerAdapter(gallerys);
        recyclerView.setAdapter(recyclerAdapter);
    }


    /***************************************findView***********************************************/
    public void findView() {
        ivHamberger = (ImageView) findViewById(R.id.ivHamberger);
        ivPictures = (ImageView) findViewById(R.id.ivPictures);
        ivSearch = (ImageView) findViewById(R.id.ivSearch);
        tvPlaceName = (TextView) findViewById(R.id.tvPlaceName);

    }

    /***************************************dummy data ***********************************************/
    public void makeDummy() {
        gallerys = new ArrayList<>();

        gallerys.add(new GalleryListData(R.drawable.testimg));
        gallerys.add(new GalleryListData(R.drawable.testimg02));
        gallerys.add(new GalleryListData(R.drawable.testimg03));
        gallerys.add(new GalleryListData(R.drawable.testimg04));
        gallerys.add(new GalleryListData(R.drawable.testimg05));
        gallerys.add(new GalleryListData(R.drawable.testimg));
        gallerys.add(new GalleryListData(R.drawable.testimg02));
        gallerys.add(new GalleryListData(R.drawable.testimg03));
        gallerys.add(new GalleryListData(R.drawable.testimg04));
        gallerys.add(new GalleryListData(R.drawable.testimg05));
        gallerys.add(new GalleryListData(R.drawable.testimg));
        gallerys.add(new GalleryListData(R.drawable.testimg02));
        gallerys.add(new GalleryListData(R.drawable.testimg03));
        gallerys.add(new GalleryListData(R.drawable.testimg04));
        gallerys.add(new GalleryListData(R.drawable.testimg05));
        gallerys.add(new GalleryListData(R.drawable.testimg));
        gallerys.add(new GalleryListData(R.drawable.testimg02));
        gallerys.add(new GalleryListData(R.drawable.testimg03));
        gallerys.add(new GalleryListData(R.drawable.testimg04));
        gallerys.add(new GalleryListData(R.drawable.testimg05));
    }



    /***********************************Adapter**********************************/
    class RecyclerAdapter extends RecyclerView.Adapter<MyViewHolder> {

        ArrayList<GalleryListData> galleryListDatas;


        public RecyclerAdapter(ArrayList<GalleryListData> galleryListDatas) {
            this.galleryListDatas = galleryListDatas;
        }

        public void setAdapter(ArrayList<GalleryListData> galleryListDatas) {
            this.galleryListDatas = galleryListDatas;
            notifyDataSetChanged();
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_photo, parent, false);
            MyViewHolder viewHolder = new MyViewHolder(view);
            return viewHolder;
        }


        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            final GalleryListData galleryListData = galleryListDatas.get(position);

            holder.ivPhoto.setImageResource(galleryListData.imgResourceID);

        }

        @Override
        public int getItemCount() {
            return gallerys.size();
        }
    }


    /**********************************ViewHolder********************************/
    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView ivPhoto;

        public MyViewHolder(View itemView) {
            super(itemView);

            ivPhoto = (ImageView) itemView.findViewById(R.id.ivPhoto);
        }
    }
}
