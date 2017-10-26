package com.seoulprojet.seoulpoem.activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.seoulprojet.seoulpoem.R;
import com.seoulprojet.seoulpoem.component.Preview;
import com.seoulprojet.seoulpoem.model.GalleryListData;
import com.seoulprojet.seoulpoem.model.GalleryResult;
import com.seoulprojet.seoulpoem.network.ApplicationController;
import com.seoulprojet.seoulpoem.network.NetworkService;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GalleryActivity extends AppCompatActivity {


    //tool_bar
    private RelativeLayout rlBack, rlSearch;
    private TextView tvPlaceName;

    private RecyclerView recyclerView;
    private RecyclerAdapter recyclerAdapter;
    private GridLayoutManager layoutManager;

    private ArrayList<GalleryListData> gallerys;

    private RelativeLayout rlToWrite;

    //유저 정보
    private String userEmail = null;
    private int loginType = 0;

    //네트워크
    NetworkService service;

    //글쓰기 화면 정보
    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_ALBUM = 2;
    private static final int CROP_FROM_CAMERA = 3;
    Uri photoUri, albumUri;
    private String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};
    private static final int MULTIPLE_PERMISSIONS = 101;

    private String mCurrentPhotoPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        //유저 정보 가져오기
        Intent intent = getIntent();
        userEmail = intent.getExtras().getString("userEmail");
        loginType = intent.getExtras().getInt("loginType");


        //서비스 객체 초기화
        service = ApplicationController.getInstance().getNetworkService();

        //findView
        findView();


        //레이아웃 매니저 설정
        recyclerView = (RecyclerView) findViewById(R.id.rvGallery);
        layoutManager = new GridLayoutManager(GalleryActivity.this, 3);
        recyclerView.setLayoutManager(layoutManager);

        gallerys = new ArrayList<>();

        //어뎁터 생성, 리사이클러뷰에 붙임
        recyclerAdapter = new RecyclerAdapter(gallerys);
        recyclerView.setAdapter(recyclerAdapter);

        //네트워크
        getPhotos();

        //메인으로
        toMain();

        //검색으로
        toSearch();

        //작성하기로
        toWrite();


    }


    /***************************************findView***********************************************/
    public void findView() {
        rlBack = (RelativeLayout) findViewById(R.id.rlBack);
        rlSearch = (RelativeLayout) findViewById(R.id.rlSearch);
        tvPlaceName = (TextView) findViewById(R.id.tvPlaceName);
        rlToWrite = (RelativeLayout) findViewById(R.id.rlToWrite);

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

            Glide.with(getApplicationContext())
                    .load(galleryListData.photo)
                    .into(holder.ivPhoto);

            //detail 화면으로 이동
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(GalleryActivity.this, DetailActivity.class);
                    intent.putExtra("articleId", galleryListData.idarticles);
                    intent.putExtra("userEmail", userEmail);
                    intent.putExtra("loginType", loginType);
                    startActivity(intent);
                }
            });

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


    /***********************************갤러리 리스트 가져오기*********************************/
    public void getPhotos() {
        Call<GalleryResult> requestGalleryLists = service.getPhotos("그리움");
        requestGalleryLists.enqueue(new Callback<GalleryResult>() {
            @Override
            public void onResponse(Call<GalleryResult> call, Response<GalleryResult> response) {
                if (response.isSuccessful()) {
                    if (response.body().sucess.equals("success")) {
                        gallerys = response.body().data;
                        recyclerAdapter.setAdapter(gallerys);
                    }
                }
            }


            @Override
            public void onFailure(Call<GalleryResult> call, Throwable t) {
                Log.i("err", t.getMessage());
            }
        });
    }


    /**********************************to main**********************************/
    public void toMain() {
        rlBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //main 이동
                Intent intent = new Intent(GalleryActivity.this, MainActivity.class);
                intent.putExtra("userEmail", userEmail);
                intent.putExtra("loginType", loginType);
                startActivity(intent);
                finish();
            }
        });
    }


    /**********************************search**********************************/
    public void toSearch() {
        rlSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //갤러리로 이동
                Intent intent = new Intent(GalleryActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });
    }

    /**********************************Write**********************************/
    public void toWrite() {
        rlToWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             dialog();
            }
        });
    }

    public void dialog(){
        final CharSequence[] items = {"카메라", "갤러리"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);     // 여기서 this는 Activity의 this

        // 여기서 부터는 알림창의 속성 설정
        builder.setTitle("")        // 제목 설정
                .setItems(items, new DialogInterface.OnClickListener(){    // 목록 클릭시 설정
                    public void onClick(DialogInterface dialog, int index){
                        if(index == 0) { // 카메라 클릭시
                            takePhoto();

                        }else{ //갤러리 클릭시
                            goToAlbum();
                            Log.e("**갤러리","갤러리시작");

                        }
                    }
                });

        AlertDialog dialog = builder.create();    // 알림창 객체 생성
        dialog.show();    // 알림창 띄우기

    }

    private void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException e) {
            Toast.makeText(GalleryActivity.this, "이미지 처리 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            finish();
            e.printStackTrace();
        }
        if (photoFile != null) {
            photoUri = FileProvider.getUriForFile(GalleryActivity.this,
                    "com.seoulprojet.seoulpoem.activity.provider", photoFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(intent, PICK_FROM_CAMERA);
        }
    }

    private boolean checkPermissions() {
        int result;
        List<String> permissionList = new ArrayList<>();
        for (String pm : permissions) {
            result = ContextCompat.checkSelfPermission(this, pm);
            if (result != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(pm);
            }
        }
        if (!permissionList.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionList.toArray(new String[permissionList.size()]), MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("HHmmss").format(new Date());
        String imageFileName = "seoulpoem_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/SeoulPoem/");
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    private void goToAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
        Log.e("**gotoalbum","startActivity");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++) {
                        if (permissions[i].equals(this.permissions[0])) {
                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                showNoPermissionToastAndFinish();
                            }
                        } else if (permissions[i].equals(this.permissions[1])) {
                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                showNoPermissionToastAndFinish();

                            }
                        } else if (permissions[i].equals(this.permissions[2])) {
                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                showNoPermissionToastAndFinish();

                            }
                        }
                    }
                } else {
                    showNoPermissionToastAndFinish();
                }
                return;
            }
        }
    }

    private void showNoPermissionToastAndFinish() {
        Toast.makeText(this, "권한 요청에 동의 해주셔야 이용 가능합니다. 설정에서 권한 허용 하시기 바랍니다.", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (requestCode == PICK_FROM_ALBUM) {
            if (data == null) {
                return;
            }
            photoUri = data.getData();
            Log.e("**Before crop photoUri",""+photoUri);
            getImageNameToUri(photoUri);

            cropImage();
        } else if (requestCode == PICK_FROM_CAMERA) {
            cropImage();
            MediaScannerConnection.scanFile(GalleryActivity.this,
                    new String[]{photoUri.getPath()}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                        }
                    });
        } else if (requestCode == CROP_FROM_CAMERA) {
            photoUri=data.getData();
            Log.e("**final crop photoUri",""+photoUri);
            Toast.makeText(GalleryActivity.this,"사진이 저장되었습니다.",Toast.LENGTH_LONG).show();
            Intent intent = new Intent(GalleryActivity.this, WritePoemActivity.class);
            intent.putExtra("type","0");
            startActivity(intent);
        }
    }

    //Android N crop image
    public void cropImage() {
        //    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { //버전이 누가보다 크거나 같을 때
        this.grantUriPermission("com.android.camera", photoUri,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        //    }
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(photoUri, "image/*");

        List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, 0);
        //   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { //버전이 누가보다 크거나 같을 때
        grantUriPermission(list.get(0).activityInfo.packageName, photoUri,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        //   }
        int size = list.size();
        if (size == 0) {
            Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_SHORT).show();
            return;
        } else {
            Toast.makeText(this, "용량이 큰 사진의 경우 시간이 오래 걸릴 수 있습니다.", Toast.LENGTH_SHORT).show();

            //   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { //버전이 누가보다 크거나 같을 때
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            //   }
            intent.putExtra("crop", "true");
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("scale", true);
            File croppedFileName = null;
            try {
                croppedFileName = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            File folder = new File(Environment.getExternalStorageDirectory() + "/SeoulPoem/");
            Log.e("**folder",folder.getPath());
            File tempFile = new File(folder.toString(), croppedFileName.getName());
            Log.e("**tempFile",tempFile.getPath());

            albumUri = Uri.fromFile(croppedFileName);
            Log.e("**albumUri",""+albumUri);

            Preview.photo_location = tempFile.getPath();
            Preview.photoName = tempFile.getName();

            photoUri = FileProvider.getUriForFile(GalleryActivity.this,
                    "com.seoulprojet.seoulpoem.activity.provider", tempFile);


            Log.e("**aftercropphotoUripath",""+photoUri);

            Preview.photo = photoUri;


            //  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//버전이 누가보다 크거나 같을 때
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            //  }

            intent.putExtra("return-data", false);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());

            Intent i = new Intent(intent);
            ResolveInfo res = list.get(0);
            //      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//버전이 누가보다 크거나 같을 때
            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            i.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            grantUriPermission(res.activityInfo.packageName, photoUri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            //    }
            i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            startActivityForResult(i, CROP_FROM_CAMERA);
        }
    }

    public String getImageNameToUri(Uri data) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(data, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        String imgPath = cursor.getString(column_index);
        String imgName = imgPath.substring(imgPath.lastIndexOf("/") + 1);

        return imgName;
    }

}
