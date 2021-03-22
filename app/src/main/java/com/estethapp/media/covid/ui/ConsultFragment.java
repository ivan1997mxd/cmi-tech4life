package com.estethapp.media.covid.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.estethapp.media.R;
import com.estethapp.media.covid.device.SystemInterface;

import java.util.Date;
import java.util.Stack;

public class ConsultFragment extends Fragment implements View.OnClickListener
{
    private HubFragment Parent;
    private Stack<Fragment> Tasks = new Stack<>();

    public ConsultFragment(HubFragment parent)
    {
        Parent = parent;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        final View fragment = inflater.inflate(R.layout.consult_fragment, container, false);

        fragment.findViewById(R.id.BeginCall).setOnClickListener(this);
        fragment.findViewById(R.id.ScheduleCall).setOnClickListener(this);
        fragment.findViewById(R.id.BackButton).setOnClickListener(this);

        return fragment;
    }

    @Override
    public void onClick(View view)
    {
        Context context = getContext();

        switch (view.getId())
        {
            case R.id.BeginCall:
                if (context != null) SystemInterface.CallNumber(context, "+12065550100");
                Parent.finishTask();
                break;
            case R.id.ScheduleCall:
                if (context != null)
                {
                    Intent intent = new Intent(Intent.ACTION_INSERT)
                            .setData(CalendarContract.Events.CONTENT_URI)
                            .putExtra(CalendarContract.Events.TITLE, "Consultation")
                            .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, new Date().getTime());

                    context.startActivity(intent);
                }
                Parent.finishTask();
                break;
            case R.id.BackButton:
                Parent.finishTask();
                break;
        }
    }
}
