package com.estethapp.media.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.estethapp.media.R;
import com.estethapp.media.fragment.ContactFragment;
import com.estethapp.media.util.Chat;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Tech4Life on 3/27/2019.
 */

public class MainChatAdapter extends BaseAdapter {


    private Context context;
    private List<Chat> contactList;
    private ArrayList<Chat> arrlist;
    String myID;
    public MainChatAdapter(Context context, List<Chat> contactList,String myID) {
        this.context = context;
        this.contactList = contactList;
        this.arrlist = new ArrayList<Chat>();
        this.arrlist.addAll(contactList);
        this.myID = myID;
    }


    @Override
    public int getCount() {
        return contactList.size();
    }

    @Override
    public Chat getItem(int position) {
        return contactList.get(position);
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
            final MainChatAdapter.ViewHolder holder;
            //final int pos = position;

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.contact_row, null);
                holder = new MainChatAdapter.ViewHolder();

                convertView.setTag(holder);

            } else {
                holder = (MainChatAdapter.ViewHolder) convertView.getTag();
            }

            final Chat contact  =contactList.get(position);

            holder.txtName = (TextView) convertView.findViewById(R.id.txtName) ;
            holder.txtEmail = (TextView) convertView.findViewById(R.id.txtEmail) ;
            holder.imgContact = (ImageView) convertView.findViewById(R.id.profileImg) ;
            holder.removeContact = (ImageView) convertView.findViewById(R.id.removeContact) ;

            holder.txtName.setText(contact.chatID);
            holder.txtEmail.setText(contact.chatName);



        } catch (NullPointerException ex) {

        }

        return convertView;
    }

    private void showGPSDisabledAlertToUser(final Chat contact, final  int position) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder
                .setMessage(
                        context.getString(R.string.txt_confirm_delete))
                .setCancelable(false)
                .setPositiveButton(context.getString(R.string.txt_yes),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                JSONObject jsonbody = new JSONObject();
                                try {
                                    jsonbody.put("MyID"        ,""+myID);
                                    jsonbody.put("UserID"        ,""+contact.userID);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                contactList.remove(position);
                                notifyDataSetChanged();
                                new ContactFragment.removeContact(jsonbody).execute();
                            }
                        });
        alertDialogBuilder.setNegativeButton(context.getString(R.string.txt_no),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }




    class ViewHolder {

        TextView txtName;
        TextView txtEmail;
        ImageView imgContact;
        ImageView removeContact;

    }
}





