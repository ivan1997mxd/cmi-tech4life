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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.estethapp.media.R;
import com.estethapp.media.fragment.ContactFragment;
import com.estethapp.media.util.Contact;

/**
 * Created by Irfan Ali on 1/6/2017.
 */
public class ContactAdapter extends BaseAdapter {


    private Context context;
    private List<Contact> contactList;
    private ArrayList<Contact> arrlist;
    String myID;

    public ContactAdapter(Context context, List<Contact> contactList, String myID) {
        this.context = context;
        this.contactList = contactList;
        this.arrlist = new ArrayList<Contact>();
        this.arrlist.addAll(contactList);
        this.myID = myID;
    }


    @Override
    public int getCount() {
        return contactList.size();
    }

    @Override
    public Contact getItem(int position) {
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
            final ViewHolder holder;
            //final int pos = position;

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.contact_row, null);
                holder = new ViewHolder();

                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final Contact contact = contactList.get(position);

            holder.txtName = (TextView) convertView.findViewById(R.id.txtName);
            holder.txtEmail = (TextView) convertView.findViewById(R.id.txtEmail);
            holder.imgContact = (ImageView) convertView.findViewById(R.id.profileImg);
            holder.removeContact = (ImageView) convertView.findViewById(R.id.removeContact);

            holder.txtName.setText(contact.Name);
            holder.txtEmail.setText(contact.Email);


            Glide.with(context).load(contact.ProfilePhoto)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .fitCenter().placeholder(R.mipmap.default_contact_img)
                    .crossFade()
                    .into(holder.imgContact);

            holder.removeContact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showGPSDisabledAlertToUser(contact, position);
                }
            });

        } catch (NullPointerException ex) {

        }

        return convertView;
    }

    private void showGPSDisabledAlertToUser(final Contact contact, final int position) {

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
                                    jsonbody.put("MyID", "" + myID);
                                    jsonbody.put("UserID", "" + contact.UserID);
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


    public void filter(String test) {
        test = test.toLowerCase(Locale.getDefault());

        contactList.clear();

        if (test.length() == 0) {
            contactList.addAll(arrlist);

        } else {
            for (Contact contact : arrlist) {
                if (contact.Name.toLowerCase(Locale.getDefault()).contains
                        (test)) {
                    contactList.add(contact);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void setItems(ArrayList<Contact> myContacts) {
        arrlist.addAll(myContacts);
    }

    public void setItem(Contact contact) {
        arrlist.add(contact);
    }

    class ViewHolder {

        TextView txtName;
        TextView txtEmail;
        ImageView imgContact;
        ImageView removeContact;

    }
}




