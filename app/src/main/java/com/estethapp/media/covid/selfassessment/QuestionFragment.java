package com.estethapp.media.covid.selfassessment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.estethapp.media.R;

public class QuestionFragment extends Fragment implements View.OnClickListener
{
    private ViewPager Parent;
    private Response Response;
    private String Title;
    private String Content;

    public QuestionFragment(String title, String content, Response response)
    {
        Response = response;
        Title = title;
        Content = content;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        if (container instanceof ViewPager)
        {
            Parent = (ViewPager)container;
        }

        View fragment = inflater.inflate(R.layout.assessment_fragment, container, false);

        TextView title = fragment.findViewById(R.id.questionText);
        TextView content = fragment.findViewById(R.id.detailText);

        title.setText(Title);
        content.setText(Content);

        fragment.findViewById(R.id.yes_button).setOnClickListener(this);
        fragment.findViewById(R.id.no_button).setOnClickListener(this);

        return fragment;
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.yes_button:
                Response.setAnswer(true);
                Parent.setCurrentItem(Parent.getCurrentItem() + 1);
                break;
            case R.id.no_button:
                Response.setAnswer(false);
                Parent.setCurrentItem(Parent.getCurrentItem() + 1);
                break;
        }
    }
}
