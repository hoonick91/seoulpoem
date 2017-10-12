package com.seoulprojet.seoulpoem.activity;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.seoulprojet.seoulpoem.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by minjeong on 2017-09-17.
 */


public class WritePoemActivity extends AppCompatActivity {

    RelativeLayout background;

    Button page_back;

    RelativeLayout toolbar;
    LinearLayout main_toolbar;
    TextView font_button;
    TextView effect_button;
    TextView sort_button;

    LinearLayout sort_toolbar;
    RelativeLayout back1;
    Button text_left;
    Button text_right;
    Button text_center;
    Button text_default;

    LinearLayout font_toolbar;
    RelativeLayout back2;
    Spinner size_spinner;

    LinearLayout effect_toolbar;
    RelativeLayout back3;
    Button bold;
    Button italic;
    Button underline;
    Spinner paint_spinner;
    AdapterSpinner adapterSpinner;
    boolean style_check[];

    RelativeLayout back;
    RelativeLayout finish;
    Button[] tag;
    int[] tagid;

    EditText write_title;
    LinearLayout write_content_wrap;
    EditText write_content;
    Button[] paper;
    int[] paperid;
    int[] paper_resource;

    TextView select_tag;
    EditText write_tag;
    EditText write_detail;

    int check_cnt = 1;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_poem);
        setId();
        CheckSelectedTag();
        addTitleTagListener();
        changeBackground();


    }


    //xml의 id값들을 java파일의 변수와 연결
    public void setId(){
        background = (RelativeLayout) findViewById(R.id.background);
        back = (RelativeLayout) findViewById(R.id.back);
        finish = (RelativeLayout)findViewById(R.id.finish);

        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),PreviewAcitivity.class);
                startActivity(intent);
            }
        });

        tag = new Button[10];
        tagid = new int[]{R.id.tag1,R.id.tag2,R.id.tag3,R.id.tag4,R.id.tag5,R.id.tag6,R.id.tag7,R.id.tag8,R.id.tag9,R.id.tag10};

        for(int i=0; i<10;i++){
            tag[i] = (Button)findViewById(tagid[i]);
            tag[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.getTag().toString().equals("0")){ //태그 추가인 경우
                        v.setTag("1");
                        check_cnt++;
                        CheckSelectedTag();
                    }
                    else{ // 태그 제거인 경우
                        if(check_cnt >= 2) { //선택되어있는 태그가 2개 이상일 경우.1개일 때는 태그제거 금지.
                            v.setTag("0");
                            check_cnt--;
                            CheckSelectedTag();
                        }
                    }
                }
            });
        }

        write_title = (EditText)findViewById(R.id.write_title);
        write_content_wrap = (LinearLayout)findViewById(R.id.write_content_wrap) ;
        write_content = (EditText)findViewById(R.id.write_content);

                InputMethodManager controlManager = (InputMethodManager) getSystemService(Service.INPUT_METHOD_SERVICE);
                SoftKeyboard mSoftKeyboard = new SoftKeyboard(write_content_wrap, controlManager);
                mSoftKeyboard.setSoftKeyboardCallback(new SoftKeyboard.SoftKeyboardChanged() {
                    @Override
                    public void onSoftKeyboardHide() {
                        new Handler(Looper.getMainLooper())
                                .post(new Runnable() {
                                    @Override
                                    public void run() {
                                        // 키보드 내려왔을때
                                        toolbar.setVisibility(View.GONE);
                                    }
                                });
                    }

                    @Override
                    public void onSoftKeyboardShow() {
                        new Handler(Looper.getMainLooper())
                                .post(new Runnable() {
                                    @Override
                                    public void run() {
                                        // 키보드 올라왔을때
                                        toolbar.setVisibility(View.VISIBLE);
                                    }
                                });
                    }
                });


        paper = new Button[4];
        paperid = new int[]{R.id.paper1,R.id.paper2,R.id.paper3,R.id.paper4};

        for(int i=0;i<4;i++){
            paper[i] = (Button)findViewById(paperid[i]);
        }
        paper_resource = new int[]{Color.BLACK, Color.RED, Color.BLUE, Color.YELLOW};

        select_tag = (TextView)findViewById(R.id.select_tag);
        write_tag = (EditText)findViewById(R.id.write_tag);
        write_detail = (EditText)findViewById(R.id.write_detail);

        toolbar = (RelativeLayout)findViewById(R.id.keyboard_toolbar);

        main_toolbar = (LinearLayout)findViewById(R.id.main_toolbar);
        font_button = (TextView)findViewById(R.id.font_button);
        font_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main_toolbar.setVisibility(View.GONE);
                font_toolbar.setVisibility(View.VISIBLE);
            }
        });
        effect_button = (TextView)findViewById(R.id.effect_button);
        effect_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main_toolbar.setVisibility(View.GONE);
                effect_toolbar.setVisibility(View.VISIBLE);
            }
        });
        sort_button = (TextView)findViewById(R.id.sort_button);
        sort_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main_toolbar.setVisibility(View.GONE);
                sort_toolbar.setVisibility(View.VISIBLE);
            }
        });


        font_toolbar = (LinearLayout)findViewById(R.id.font_toolbar);
        back2 = (RelativeLayout) findViewById(R.id.back2);
        back2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                font_toolbar.setVisibility(View.GONE);
                main_toolbar.setVisibility(View.VISIBLE);
            }
        });
        size_spinner = (Spinner)findViewById(R.id.size_spinner);
        size_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                write_content.setTextSize(Integer.parseInt(parent.getItemAtPosition(position).toString()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        page_back = (Button)findViewById(R.id.page_back);
        page_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        sort_toolbar = (LinearLayout)findViewById(R.id.sort_toolbar);
        back1 = (RelativeLayout)findViewById(R.id.back1);
        back1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sort_toolbar.setVisibility(View.GONE);
                main_toolbar.setVisibility(View.VISIBLE);
            }
        });
        text_center = (Button)findViewById(R.id.text_center);
        text_center.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                write_content.setGravity(Gravity.CENTER_HORIZONTAL);
            }
        });
        text_left = (Button)findViewById(R.id.text_left);
        text_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                write_content.setGravity(Gravity.LEFT);
            }
        });
        text_right = (Button)findViewById(R.id.text_right);
        text_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                write_content.setGravity(Gravity.RIGHT);
            }
        });
        text_default = (Button)findViewById(R.id.text_default);
        text_default.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                write_content.setGravity(Gravity.NO_GRAVITY);
            }
        });

        effect_toolbar = (LinearLayout)findViewById(R.id.effect_toolbar);
        back3 = (RelativeLayout)findViewById(R.id.back3);
        back3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                effect_toolbar.setVisibility(View.GONE);
                main_toolbar.setVisibility(View.VISIBLE);
            }
        });
        style_check = new boolean[]{false,false,false};
        bold = (Button)findViewById(R.id.bold);
        bold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(style_check[0] == false) {
                    Log.e("굵게현재상황 : ",""+style_check[0]);
                    if(style_check[1]==false)
                        write_content.setTypeface(null, Typeface.BOLD);
                    else
                        write_content.setTypeface(null, Typeface.BOLD_ITALIC);
                    style_check[0] = true;
                }else{
                    if(style_check[1]==false)
                        write_content.setTypeface(null, Typeface.NORMAL);
                    else
                        write_content.setTypeface(null, Typeface.ITALIC);
                    style_check[0] = false;
                }

            }
        });

        italic = (Button)findViewById(R.id.italic);
        italic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(style_check[1] == false) {
                    Log.e("기울임현재상황 : ",""+style_check[1]);
                    if(style_check[0]==false)
                        write_content.setTypeface(null, Typeface.ITALIC);
                    else
                        write_content.setTypeface(null, Typeface.BOLD_ITALIC);
                    style_check[1] = true;
                }else{
                    if(style_check[0]==false)
                        write_content.setTypeface(null, Typeface.NORMAL);
                    else
                        write_content.setTypeface(null, Typeface.BOLD);
                    style_check[1] = false;
                }

            }
        });
        underline = (Button)findViewById(R.id.underline);
        underline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("밑줄현재상황 : ",""+style_check[2]);
                if(style_check[2] == false) {
                    write_content.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
                    style_check[2] = true;
                }else{
                    write_content.setPaintFlags(0);
                    style_check[2] = false;
                }

            }
        });
        paint_spinner = (Spinner)findViewById(R.id.paint_spinner);
        List<String> data = new ArrayList<>();
        data.add("빨강");
        data.add("검정");
        adapterSpinner = new AdapterSpinner(this, data);
        paint_spinner.setAdapter(adapterSpinner);



    }

  /*  버튼을 눌러 선택한 태그들을 확인하여  태그 edittext에 입력하는 부분.
    버튼의 태그가 0일땐 선택되지 않은 것, 1일때는 선택된 태그(배경이미지와 글자색도 변경됨.)
    선택된것이 1개 이상일 때 작동*/
    public void CheckSelectedTag(){
        select_tag.setText("");
        if(write_title.getText().length() > 0)
        select_tag.setText("#"+write_title.getText().toString()+" ");
        for(int i=0;i<10;i++){
            if(tag[i].getTag().toString().equals("1")){ //선택되어있는 경우
                tag[i].setBackgroundResource(R.drawable.check128);
                tag[i].setTextColor(Color.WHITE);
                String tag_text = select_tag.getText().toString();
                select_tag.setText("#"+tag[i].getText().toString()+" "+tag_text);
            }
            else{ //선택되어있지 않은 경우
                tag[i].setBackgroundResource(R.drawable.rectangle_path);
                tag[i].setTextColor(Color.parseColor("#95989a"));
            }
        }

    }

    //제목 작성지 필수태그에 제목 추가하기
    public void  addTitleTagListener(){
        write_title.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //입력되는 텍스트에 변화가 있을 때
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // 입력이 끝났을 때
                CheckSelectedTag();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 입력하기 전에
            }
        });

    }

    public void changeBackground(){

        for(int i=0; i<4;i++){
            paper[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //후에 색이 아닌 이미지로 바꿔야 함.
                    background.setBackgroundColor(paper_resource[Integer.parseInt(v.getTag().toString())-1]);
                   
                }
            });
        }
    }

    public class AdapterSpinner extends BaseAdapter {

        Context context;
        List<String> data;
        LayoutInflater inflater;

        public AdapterSpinner(Context context, List<String> data){
            this.context = context;
            this.data = data;
            inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            if(data!=null) return data.size();
            else return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView==null) {
                convertView = inflater.inflate(R.layout.spinner_normal, parent, false);
            }

            if(data!=null){
                //데이터세팅
                String text = data.get(position);
                int color;
                if(text.equals("빨강")){
                    color = Color.RED;
                }else{
                    color = Color.BLACK;
                }
                ((ImageView)convertView.findViewById(R.id.spinnerColor)).setBackgroundColor(color);
                write_content.setTextColor(color);
            }

            return convertView;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            if(convertView==null){
                convertView = inflater.inflate(R.layout.spinner_dropdown, parent, false);
            }

            //데이터세팅
            String text = data.get(position);
            ((TextView)convertView.findViewById(R.id.spinnerText)).setText(text);

            return convertView;
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
    }

    public void 

}
