package com.estethapp.media.view;


import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.TextView;

import com.estethapp.media.R;

public class TakePictureOptionsDailog extends DialogFragment {
    private View senderView;
    TakePictureOptionsDailogListener listener;
    private String Title;

    CheckBox individual;
    CheckBox institutional;


    public TakePictureOptionsDailog setTitle(String title) {
        Title = title;
        return  this;
    }

    // Empty constructor required for DialogFragment
    public TakePictureOptionsDailog() {

    }

    public static TakePictureOptionsDailog init(View senderView, TakePictureOptionsDailogListener listener) {
        TakePictureOptionsDailog dailogue = new TakePictureOptionsDailog();
        dailogue.senderView = senderView;
        dailogue.listener = listener;
        return dailogue;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View root = inflater.inflate(R.layout.layout_take_picture_options, container);
        return root;
    }

    @Override
    public void onViewCreated(View root, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(root, savedInstanceState);
        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        if(Title!=null && !Title.equals("")) // Setting Title If Exists
            ((TextView)root.findViewById(R.id.txtTitle)).setText(Title);

        root.findViewById(R.id.btnCamera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.camera();
                dismiss();
            }
        });


        root.findViewById(R.id.btnGallery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.gallery();
                dismiss();
            }
        });


    }
    @Override
    public void onStart() {
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        super.onStart();
    }

    public interface TakePictureOptionsDailogListener {
        public void camera();
        public void gallery();
    }
}