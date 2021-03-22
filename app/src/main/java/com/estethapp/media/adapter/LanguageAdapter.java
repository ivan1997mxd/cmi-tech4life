package com.estethapp.media.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import com.estethapp.media.R;
import com.estethapp.media.util.App;
import com.franmontiel.localechanger.LocaleChanger;
import com.franmontiel.localechanger.utils.ActivityRecreationHelper;

public class LanguageAdapter extends ArrayAdapter<String> {

    private RadioButton rb;
    private Context context;
    private Activity activity;

    public LanguageAdapter(Context context, Activity activity, String[] users) {
        super(context, 0, users);
        this.activity = activity;
    }


    @Override

    public View getView(final int position, View convertView, ViewGroup parent) {

        String text = getItem(position);

        if (convertView == null) {

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_language, parent, false);

        }

        TextView tvName = convertView.findViewById(R.id.tvName);
        rb = convertView.findViewById(R.id.rbCheck);

        tvName.setText(text);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rb.performClick();
                LocaleChanger.setLocale(App.SUPPORTED_LOCALES.get(position));
                ActivityRecreationHelper.recreate(activity, false);
            }
        });

        return convertView;
    }

}
