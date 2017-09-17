package com.seoulprojet.seoulpoem.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import com.seoulprojet.seoulpoem.R;

public class WriterList extends AppCompatActivity {

    Button writerlist_hamburger_btn;
    Button writerlist_submit_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writer_list);

        /////////////////////////////////
        writerlist_hamburger_btn = (Button)findViewById(R.id.writerlist_hamburger_btn);
        writerlist_submit_btn = (Button)findViewById(R.id.writerlist_submit_btn);

        //////////////
    }
}
