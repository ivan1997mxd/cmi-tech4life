package com.estethapp.media.covid.onboarding;


import android.Manifest;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.estethapp.media.R;
import com.estethapp.media.covid.BlueToothActivity;
import com.estethapp.media.covid.device.PermissionCallback;
import com.estethapp.media.covid.device.SystemInterface;

public class Bluetooth extends Fragment implements View.OnClickListener
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

        View fragment = inflater.inflate(R.layout.bluetooth_fragment, container, false);

        fragment.findViewById(R.id.bluetooth_allow_button).setOnClickListener(this);

        return fragment;
    }


    @Override
    public void onClick(View view)
    {
        if (view.getId() == R.id.bluetooth_allow_button)
        {
            if (getContext() instanceof BlueToothActivity)
            {
                SystemInterface.AcquirePermission((BlueToothActivity)getContext(), Manifest.permission.BLUETOOTH, new PermissionCallback()
                {
                    @Override
                    public void run(Boolean argument)
                    {
                        SystemInterface.AcquirePermission((BlueToothActivity)getContext(), Manifest.permission.BLUETOOTH_ADMIN, new PermissionCallback()
                        {
                            @Override
                            public void run(Boolean argument)
                            {
                                SystemInterface.AcquirePermission((BlueToothActivity)getContext(), Manifest.permission.ACCESS_FINE_LOCATION, new PermissionCallback()
                                {
                                    @Override
                                    public void run(Boolean argument)
                                    {
                                        if (argument) Parent.setCurrentItem(Parent.getCurrentItem() + 1);
                                    }
                                });
                            }
                        });
                    }
                });
            }
        }
    }
}
