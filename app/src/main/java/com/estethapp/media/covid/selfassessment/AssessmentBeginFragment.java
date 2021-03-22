package com.estethapp.media.covid.selfassessment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.estethapp.media.R;

public class AssessmentBeginFragment extends Fragment implements View.OnClickListener
{
    private ViewPager Parent;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        if (container instanceof ViewPager)
        {
            Parent = (ViewPager)container;
        }

        View fragment = inflater.inflate(R.layout.assessment_begin_fragment, container, false);

        fragment.findViewById(R.id.assessment_start_button).setOnClickListener(this);

        return fragment;
    }

    @Override
    public void onClick(View view)
    {
        if (view.getId() == R.id.assessment_start_button)
        {
            Parent.setCurrentItem(Parent.getCurrentItem() + 1);
        }
    }
}
