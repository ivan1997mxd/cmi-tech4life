package com.estethapp.media.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.estethapp.media.ContactActivity;
import com.estethapp.media.R;
import com.estethapp.media.listener.IContactPickerListener;
import com.estethapp.media.util.Contact;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Irfan Ali on 1/6/2017.
 */
public class ContactPickerAdapter extends BaseAdapter {


    private Context context;
    private List<Contact> contactListArray;
    private ArrayList<Contact> arrlist;
    private IContactPickerListener listener;
    private List<Contact> checkedList;
    private boolean isChecked = false;
    public boolean selectedContact[];

    public ContactPickerAdapter(Context context, List<Contact> contactList, IContactPickerListener listener) {
        this.context = context;
        this.contactListArray = contactList;
        this.arrlist = new ArrayList<Contact>();
        this.arrlist.addAll(contactList);
        checkedList = new ArrayList<>();
        this.listener = listener;
    }


    @Override
    public int getCount() {
        return contactListArray.size();
    }

    @Override
    public Contact getItem(int position) {
        return contactListArray.get(position);
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
                convertView = inflater.inflate(R.layout.contact_picker_row, null);
                holder = new ViewHolder();

                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final Contact contact = contactListArray.get(position);
            isChecked = false;

            holder.txtName = (TextView) convertView.findViewById(R.id.txtName);
            holder.txtEmail = (TextView) convertView.findViewById(R.id.txtEmail);
            holder.imgContact = (ImageView) convertView.findViewById(R.id.profileImg);
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);

            if (contact != null) {
                holder.txtName.setText(contact.Name);
                holder.txtEmail.setText(contact.Email);


                Glide.with(context).load(contact.ProfilePhoto)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .fitCenter().placeholder(R.mipmap.default_contact_img)
                        .crossFade()
                        .into(holder.imgContact);
            }


            holder.checkBox.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    CheckBox cb = (CheckBox) v;
                    if (cb.isChecked()) {
                        listener.addContact(contactListArray.get(position));
                        checkedList.add(contactListArray.get(position));
                    } else {
                        listener.removeContact(contactListArray.get(position));
                    }
                }
            });

            if (contact != null)
                for (Contact contact1 : checkedList) {
                    if (contact.UserID.equals(contact1.UserID)) {
                        isChecked = true;
                        break;
                    }
                }

            if (isChecked)
                holder.checkBox.setChecked(true);
            else
                holder.checkBox.setChecked(false);


        } catch (NullPointerException ex) {

        }

        return convertView;
    }


    public void filter(String test) {
        test = test.toLowerCase(Locale.getDefault());

        contactListArray.clear();

        if (test.length() == 0) {
            contactListArray.addAll(arrlist);

        } else {
            for (Contact contact : arrlist) {
                if (contact.Name.toLowerCase(Locale.getDefault()).contains
                        (test)) {
                    contactListArray.add(contact);
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
        CheckBox checkBox;
    }
}




