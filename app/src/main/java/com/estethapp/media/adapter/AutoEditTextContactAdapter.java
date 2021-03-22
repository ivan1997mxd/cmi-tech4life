package com.estethapp.media.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import com.estethapp.media.R;
import com.estethapp.media.util.Contact;

/**
 * Created by Irfan Ali on 1/6/2017.
 */
public class AutoEditTextContactAdapter extends BaseAdapter {


    private Context context;
    private List<Contact> contactList;
    private ArrayList<Contact> arrlist;

    public AutoEditTextContactAdapter(Context context, List<Contact> contactList) {
        this.context = context;
        this.contactList = contactList;
        this.arrlist = new ArrayList<Contact>();
        this.arrlist.addAll(contactList);

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
                convertView = inflater.inflate(R.layout.auto_edittextcontact_row, null);
                holder = new ViewHolder();

                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Contact contact =contactList.get(position);

            holder.txtName = (TextView) convertView.findViewById(R.id.txtName) ;
            holder.txtEmail = (TextView) convertView.findViewById(R.id.txtEmail) ;

            holder.txtName.setText(contact.Name);
            holder.txtEmail.setText(contact.UserID);

        } catch (NullPointerException ex) {

        }


        return convertView;
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
            notifyDataSetChanged();
        }
    }

    class ViewHolder {

        TextView txtName;
        TextView txtEmail;

    }
}




