package com.estethapp.media.covid.onboarding;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.estethapp.media.R;
import com.estethapp.media.covid.ui.OnboardingFragment;

public class SetupCompleted extends Fragment implements View.OnClickListener
{
    private OnboardingFragment Parent;

    public SetupCompleted(OnboardingFragment parent)
    {
        Parent = parent;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View fragment = inflater.inflate(R.layout.setup_complete_fragment, container, false);

        fragment.findViewById(R.id.setup_finished_button).setOnClickListener(this);

        return fragment;
    }

    @Override
    public void onClick(View view)
    {
        if (view.getId() == R.id.setup_finished_button)
        {
            Parent.setupFinished();
        }
    }
}
