package com.estethapp.media.covid.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.estethapp.media.R;
import com.estethapp.media.covid.BlueToothActivity;

import java.util.Stack;

public class HubFragment extends Fragment implements View.OnClickListener
{
    private MainFragment Parent;
    private Stack<Fragment> Tasks = new Stack<>();

    public HubFragment(MainFragment parent)
    {
        Parent = parent;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
//        SystemInterface.EnableBluetooth(this);
        final View fragment = inflater.inflate(R.layout.hub_fragment, container, false);

        fragment.findViewById(R.id.AccessDevice).setOnClickListener(this);
        fragment.findViewById(R.id.SelfAssess).setOnClickListener(this);
        fragment.findViewById(R.id.Consult).setOnClickListener(this);

        return fragment;
    }

    @Override
    public void onClick(View view)
    {
        Fragment child;

        switch (view.getId())
        {
            case R.id.AccessDevice:
                child = new DeviceStatisticsFragment(this);
                break;
            case R.id.SelfAssess:
                child = new SelfAssessmentFragment(this);
                break;
            case R.id.Consult:
                child = new ConsultFragment(this);
                break;
            default:
                return;
        }

        startTask(child);
    }

    public void finishTask()
    {
        if (!Tasks.empty())
        {
            Parent.setChild(Tasks.pop());
        }
    }

    public void startTask(Fragment fragment)
    {
        Tasks.push(Parent.getChild());
        Parent.setChild(fragment);
    }
}