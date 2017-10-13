package com.seoulprojet.seoulpoem.activity;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.seoulprojet.seoulpoem.R;

public class WriterApply extends AppCompatActivity {

    ImageButton writerapply_back_btn;
    ImageButton writerapply_ok_btn;
    EditText writerapply_message_et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writer_apply);

        //////////////////////////////////
        writerapply_back_btn = (ImageButton)findViewById(R.id.writerapply_back_btn);
        writerapply_ok_btn = (ImageButton)findViewById(R.id.writerapply_ok_btn);
        writerapply_message_et = (EditText)findViewById(R.id.writerapply_message_et);
        /////////////////////////////////

        writerapply_back_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        writerapply_ok_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
    }

    private void showDialog(){
        LayoutInflater dialog = LayoutInflater.from(this);
        final View dialogLayout = dialog.inflate(R.layout.writer_apply_dialog, null);
        final Dialog writerApplyDialog = new Dialog(this);

        writerApplyDialog.setContentView(dialogLayout);
        writerApplyDialog.show();

        ImageButton apply_btn = (ImageButton)dialogLayout.findViewById(R.id.writerapply_dialog_back_btn);

        apply_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), WriterList.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
