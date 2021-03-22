package com.estethapp.media;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import androidx.viewpager.widget.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.estethapp.media.adapter.PlayerPositionViewPagerAdapter;
import com.estethapp.media.helper.ExminatingHelper;
import com.estethapp.media.helper.ExminigPostion;
import com.estethapp.media.helper.Exmining;
import com.estethapp.media.util.ChatUtil;
import com.estethapp.media.view.AudioWife;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
//import io.fabric.sdk.android.Fabric;

/**
 * Created by Irfan Ali on 1/3/2017.
 */
public class ActivityChatPlayer extends Activity implements View.OnClickListener {

    private ImageView btnPlay, btnPause;
    SeekBar seekbar;
    private Handler bodyPositionHandler = new Handler();
    public TextView runTime;
    public TextView totalTime;
    private boolean isPlayed = false;
    private String filePath = null;

    TextView txtRecordStatus;
    ExminatingHelper exminatingHelper = ExminatingHelper.getInstance();
    ViewPager viewPager;
    PlayerPositionViewPagerAdapter playerPositionViewPagerAdapter;
    ArrayList<ExminigPostion> exminigFrontPostionHashMap;
    ArrayList<ExminigPostion> exminigBackPostionHashMap;
    Exmining exminingInstance;
    boolean isFrontPlaying = true;

    public String exminingInfo;
    AudioWife audioWifeInstance;

    private ChatUtil chatUtil = ChatUtil.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //      Fabric.with(getApplicationContext(), new Crashlytics());
        setContentView(R.layout.activity_player);

        txtRecordStatus = (TextView) findViewById(R.id.txt_recordStatus);

        filePath = getIntent().getStringExtra("FileUrl");

        exminingInfo = getIntent().getStringExtra("Exmining");

        exminingInstance = new Gson().fromJson(exminingInfo, Exmining.class);


        viewPager = (ViewPager) findViewById(R.id.viewpager);
        playerPositionViewPagerAdapter = new PlayerPositionViewPagerAdapter(ActivityChatPlayer.this);
        viewPager.setAdapter(playerPositionViewPagerAdapter);

        startPostionFromPref();

        seekbar = (SeekBar) findViewById(R.id.media_seekbar);
        btnPlay = (ImageView) findViewById(R.id.play);
        btnPause = (ImageView) findViewById(R.id.pause);
        //btnPlay.setBackgroundResource(R.mipmap.play_ic);


        File file = new File(filePath);
        if (file.exists()) {
            file.setReadable(true, false);
        }


        audioWifeInstance = AudioWife.getInstance()
                .init(ActivityChatPlayer.this, file)
                .setPlayView(btnPlay)
                .setPauseView(btnPause)
                .setSeekBar(seekbar)
                .setRuntimeView(runTime)
                .setTotalTimeView(totalTime);

        seekbar.setClickable(false);

        AudioWife.getInstance().addOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {

                // bodyPositionHandler.removeMessages(0);
                //resetPlayer();
            }
        });

        AudioWife.getInstance().addOnPlayClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    bodyPositionHandler.removeMessages(0);
                    isFrontPlaying = true;
                    backCount = 0;
                    frontCount = 1;


                    if (exminigFrontPostionHashMap != null) {
                        if (!exminigFrontPostionHashMap.isEmpty()) {
                            // myHandler.postDelayed(UpdateSongTime,100);
                            try {
                                viewPager.setCurrentItem(0, true);
                                ExminigPostion postions = exminigFrontPostionHashMap.get(0);
                                txtRecordStatus.setText(exminatingHelper.frontPositionNames[0]);
                                int time = (postions.endTime - postions.startTime) * 1000;
                                PlayerPositionViewPagerAdapter.showSelectedFrontPostionAnimation("" + postions.bodyPostion);
                                bodyPositionHandler.postDelayed(bodyPositionTime, time);
                            } catch (Exception e) {

                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        AudioWife.getInstance().addOnPauseClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

            }
        });


    }

    public void startPostionFromPref() {
        if (exminingInstance != null) {
            exminigFrontPostionHashMap = exminingInstance.frontPostion;
            exminigBackPostionHashMap = exminingInstance.backPostion;
        } else {
            Toast.makeText(ActivityChatPlayer.this, R.string.couldnt_find_position_of_files, Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.imgShare) {
            Intent intent = new Intent(ActivityChatPlayer.this, ContactActivity.class);
            startActivityForResult(intent, 5000);
        }
    }


    @Override
    public void onDestroy() {
        audioWifeInstance.getPlayerInstance().stop();
        super.onDestroy();
    }

    int frontCount = 1;
    int backCount = 0;


    private Runnable bodyPositionTime = new Runnable() {
        public void run() {
                if (exminingInstance.isFront && isFrontPlaying && exminigFrontPostionHashMap.size() > frontCount) {
                    if (exminigFrontPostionHashMap.size() >= 1 && exminigFrontPostionHashMap.size() > frontCount) {
                        viewPager.setCurrentItem(0, true);
                        ExminigPostion postions = exminigFrontPostionHashMap.get(frontCount);
                        int bodyPostion = postions.bodyPostion - 1;
                        txtRecordStatus.setText(exminatingHelper.frontPositionNames[bodyPostion]);
                        int time = (postions.endTime - postions.startTime) * 1000;
                        String keyPostion = "" + postions.bodyPostion;
                        PlayerPositionViewPagerAdapter.showSelectedFrontPostionAnimation(keyPostion);
                        bodyPositionHandler.postDelayed(bodyPositionTime, time);
                        frontCount++;
                    } else {
                        isFrontPlaying = false;
                        frontCount = 0;
                    }
                } else if (exminingInstance.isBack) {
                    try {
                        if (exminigBackPostionHashMap.size() >= 1 && exminigBackPostionHashMap.size() >= backCount) {
                            viewPager.setCurrentItem(1, true);
                            ExminigPostion postions = exminigBackPostionHashMap.get(backCount);
                            int bodyPostion = postions.bodyPostion - 1;
                            txtRecordStatus.setText(exminatingHelper.backPostionNames[bodyPostion]);
                            int time = (postions.endTime - postions.startTime) * 1000;
                            String keyPostion = "" + postions.bodyPostion;
                            PlayerPositionViewPagerAdapter.showSelectedBackPostionAnimation(keyPostion);
                            bodyPositionHandler.postDelayed(bodyPositionTime, time);
                            backCount++;
                        } else {
                            isFrontPlaying = true;
                            backCount = 0;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
        }
    };


}
