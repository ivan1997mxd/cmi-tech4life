package com.estethapp.media;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.khizar1556.mkvideoplayer.MKPlayerActivity;

public class ActivityPlayer2 extends AppCompatActivity {


    private String imgurl;
    private String fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player2);

        String recordFileDir = getIntent().getStringExtra("dir");
        fileName = getIntent().getStringExtra("fileName");


        MKPlayerActivity.configPlayer(this).play(recordFileDir);

    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
