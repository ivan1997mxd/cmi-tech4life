package com.estethapp.media.fragment;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.estethapp.media.R;
import com.khizar1556.mkvideoplayer.MKPlayerActivity;

/**
 * Created by Tech4Life on 4/27/2019.
 */

public class VideoFragment extends Fragment {
    VideoFragment fragment;
    Context context;
    static VideoFragment instance;
    View root;
    String value;

    public VideoFragment() {
        // Required empty public constructor
    }

    public static VideoFragment newInstance() {
        VideoFragment instance = new VideoFragment();
               return instance;
    }

    public static VideoFragment getInstance() {
        return instance;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        instance = this;


        return inflater.inflate(R.layout.activity_play, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        context = getActivity();

        Bundle bundle=getArguments();
        String value = bundle.getString("dir");
        MKPlayerActivity.configPlayer(this.getActivity()).play(value);

        super.onViewCreated(view, savedInstanceState);
    }



}
