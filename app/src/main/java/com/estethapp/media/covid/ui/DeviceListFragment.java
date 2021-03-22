package com.estethapp.media.covid.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.estethapp.media.R;
import com.estethapp.media.covid.device.Callback;
import com.estethapp.media.covid.device.DeviceValues;
import com.estethapp.media.covid.device.SaveData;
import com.estethapp.media.covid.device.SystemInterface;

public class DeviceListFragment extends Fragment implements View.OnClickListener, Callback<String>
{
    private HubFragment Hub;
    private DeviceStatisticsFragment Parent;
    private DeviceListAdapter Adapter;

    private final Runnable OnConnected;
    private final Callback<DeviceValues> OnDataReceived;
    private final Runnable OnDisconnected;

    public DeviceListFragment(HubFragment hub, DeviceStatisticsFragment parent, Runnable onConnected, Callback<DeviceValues> onDataReceived, Runnable onDisconnected)
    {
        Hub = hub;
        Parent = parent;
        Adapter = new DeviceListAdapter(this);
        OnConnected = onConnected;
        OnDataReceived = onDataReceived;
        OnDisconnected = onDisconnected;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        final View fragment = inflater.inflate(R.layout.device_list_fragment, container, false);

        fragment.findViewById(R.id.BackButton).setOnClickListener(this);
        RecyclerView list = ((RecyclerView)fragment.findViewById(R.id.DeviceList));
        list.setAdapter(Adapter);
        list.setLayoutManager(new LinearLayoutManager(getContext()));

        if (SystemInterface.InitializeBluetooth(getContext(), Adapter, OnConnected, OnDataReceived, OnDisconnected))
        {
            SystemInterface.StartBluetoothScan();
        }

        return fragment;
    }

    @Override
    public void onClick(View view)
    {
        if (view.getId() == R.id.BackButton)
        {
            SystemInterface.StopBluetoothScan();
            Hub.finishTask();
        }
    }

    @Override
    public void run(String argument)
    {
        SaveData.SetDeviceInfo(getContext(), argument);
        SystemInterface.StopBluetoothScan();
        Toast.makeText(getActivity(),"Connecting with new device",Toast.LENGTH_SHORT).show();
        Hub.finishTask();

    }
}