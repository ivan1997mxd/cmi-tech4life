package com.estethapp.media.covid.selfassessment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.estethapp.media.R;
import com.estethapp.media.covid.ui.SelfAssessmentFragment;

public class AssessmentEndFragment extends Fragment implements View.OnClickListener
{
    private SelfAssessmentFragment Parent;

    public AssessmentEndFragment(SelfAssessmentFragment parent)
    {
        Parent = parent;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View fragment = inflater.inflate(R.layout.assessment_finished_fragment, container, false);

        fragment.findViewById(R.id.assessment_finished_button).setOnClickListener(this);

        return fragment;
    }

    @Override
    public void onClick(View view)
    {
        if (view.getId() == R.id.assessment_finished_button)
        {
            Parent.finishAssessment();
        }
    }
}
