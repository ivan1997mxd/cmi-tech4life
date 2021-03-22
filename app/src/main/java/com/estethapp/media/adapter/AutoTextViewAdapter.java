package com.estethapp.media.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.estethapp.media.R;
import com.estethapp.media.util.Contact;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ali Hassan on 3/29/2017.
 */

public class AutoTextViewAdapter extends ArrayAdapter<Contact> {

    Context context;
    List<Contact> arrayList,tempItems, suggestions;

    public AutoTextViewAdapter(Context context, int resource, int textViewResourceId, List<Contact> arrayList){
        super(context,resource,textViewResourceId,arrayList);
        this.arrayList = arrayList;
        tempItems = new ArrayList<Contact>(arrayList);
        this.context = context;
        suggestions = new ArrayList<Contact>();
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Contact getItem(int i) {
        return arrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.autotextview_row, viewGroup, false);
        }

        TextView txtName = (TextView) view.findViewById(R.id.itemname);
        TextView txtEmail = (TextView) view.findViewById(R.id.item_email);
        Contact modelInstance = arrayList.get(position);
        txtName.setText(modelInstance.Name);
        txtEmail.setText(modelInstance.Email);


        return view;
    }
    @Override
    public Filter getFilter() {
        return nameFilter;
    }

    Filter nameFilter = new Filter() {
        @Override
        public CharSequence convertResultToString(Object resultValue) {

            String str = "";
            if(resultValue instanceof Contact){
                Contact contact =((Contact) resultValue);
                str = contact.Name;
            }
            return str;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                suggestions.clear();
                for (Contact contactInstance : tempItems) {
                    String str = contactInstance.Name;
                    if (str.toLowerCase().contains(constraint.toString().toLowerCase())) {
                        suggestions.add(contactInstance);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            List<Contact> filterList = (ArrayList<Contact>) results.values;
            if (results != null && results.count > 0) {
                clear();
                for (Contact contact : filterList) {
                    add(contact);
                    notifyDataSetChanged();
                }
            }
        }
    };

}
