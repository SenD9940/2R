package com.fbf.a2r;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class ShowHonorListActivity extends AppCompatActivity {
    private ImageView Honor_Indicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_honor_list);
        Honor_Indicator = findViewById(R.id.ImageView_HonorIndicator);
        Honor_Indicator.setBackgroundColor(Color.BLACK);

        final AlertDialog.Builder HonorInfo = new AlertDialog.Builder(this);
        HonorInfo.setTitle("명예의 전당 혜택");
        HonorInfo.setMessage("명예의 전당에 들면 광고 수익 20% 플러스!!!");
        HonorInfo.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onBackPressed();
            }
        });
    }

    public void onclick(View view) {
        switch (view.getId()){
        }
    }
}
