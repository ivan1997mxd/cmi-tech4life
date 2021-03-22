package com.estethapp.media.covid.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.estethapp.media.R;
import com.estethapp.media.covid.device.BluetoothDeviceInfo;
import com.estethapp.media.covid.device.Callback;
import com.estethapp.media.covid.device.SystemInterface;

import java.util.ArrayList;

public class DeviceListAdapter extends RecyclerView.Adapter<DeviceListAdapter.ViewHolder> implements Callback<String>
{
    private final ArrayList<String> Devices = new ArrayList<>();
    //    private final ArrayList<String> DeviceNames = new ArrayList<>();
    private final Callback<String> OnSelected;
    private Context context;

    public DeviceListAdapter(Callback<String> onSelected)
    {
        OnSelected = onSelected;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        context = parent.getContext();
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.device_list_item_fragment, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        BluetoothDeviceInfo info = SystemInterface.GetDeviceInfo(Devices.get(position));

        holder.getNameView().setText(info.getName());
        holder.getAddressView().setText(info.getAddress());
        holder.getTypeView().setText(info.getType());

        holder.itemView.setOnClickListener(new ClickHandler(this, position));
    }

    @Override
    public int getItemCount()
    {
        return Devices.size();
    }

    @Override
    public void run(String argument)
    {
        if (!Devices.contains(argument))
        {
            Devices.add(argument);
            notifyItemInserted(Devices.size() - 1);
        }
    }


    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
        }

        public TextView getNameView()
        {
            return (TextView)itemView.findViewById(R.id.BluetoothDeviceName);
        }

        public TextView getAddressView()
        {
            return (TextView)itemView.findViewById(R.id.BluetoothDeviceAddress);
        }

        public TextView getTypeView()
        {
            return (TextView)itemView.findViewById(R.id.BluetoothDeviceType);
        }
    }

    private static class ClickHandler implements View.OnClickListener
    {
        private DeviceListAdapter Parent;
        private int Index;

        public ClickHandler(DeviceListAdapter parent, int index)
        {
            Parent = parent;
            Index = index;
        }

        @Override
        public void onClick(View view)
        {
            if (Parent.OnSelected != null) Parent.OnSelected.run(Parent.Devices.get(Index));
        }
    }
}