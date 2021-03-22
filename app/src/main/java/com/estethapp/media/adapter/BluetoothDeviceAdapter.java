package com.estethapp.media.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import  com.estethapp.media.R;
import  com.estethapp.media.util.BluetoothUtil;

/**
 * Created by Irfan Ali on 1/6/2017.
 */
public class BluetoothDeviceAdapter extends BaseAdapter {


    private Context context;
    private ArrayList<BluetoothUtil> blueList;
    public BluetoothDeviceAdapter(Context context, ArrayList<BluetoothUtil> blueList) {
        this.context = context;
        this.blueList = blueList;
    }


    @Override
    public int getCount() {
        return blueList.size();
    }

    @Override
    public BluetoothUtil getItem(int position) {
        return blueList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public int getItemViewType(int position) {

        return position;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        try {
            final ViewHolder holder;
            //final int pos = position;

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.blue_row, null);
                holder = new ViewHolder();

                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            BluetoothUtil device =blueList.get(position);

            holder.txtName = (TextView) convertView.findViewById(R.id.txtBlueName) ;
            holder.txtPairedUnPaired = (TextView) convertView.findViewById(R.id.txtPaird_Unpaired) ;

            holder.txtName.setText(device.getDeviceName());
            holder.txtPairedUnPaired.setText(device.isPaired() ? context.getString(R.string.txt_paired) : context.getString(R.string.txt_unpaired));


        } catch (NullPointerException ex) {

        }

        return convertView;
    }

    class ViewHolder {

        TextView txtName;
        TextView txtPairedUnPaired;
    }
}




