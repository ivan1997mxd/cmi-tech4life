package com.estethapp.media.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.estethapp.media.ActivityPlayer;
import com.estethapp.media.R;
import com.estethapp.media.db.ChatGroup;
import com.estethapp.media.util.Constants;
import com.estethapp.media.util.Helper;
import com.estethapp.media.util.Util;
import com.estethapp.media.view.ToastStyle;
import com.estethapp.media.web.HttpCaller;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


/**
 * Created by Irfan Ali on 1/6/2017.
 */
public class ChatGroupAdapter extends BaseAdapter {

    private Context context;
    private List<ChatGroup> contactListArray;


    public ChatGroupAdapter(Context context, List<ChatGroup> contactList) {
        this.context = context;
        this.contactListArray = contactList;
    }

    @Override
    public int getCount() {
        return contactListArray.size();
    }

    @Override
    public ChatGroup getItem(int position) {
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

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.group_chat_row, null);
                holder = new ViewHolder();

                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final ChatGroup group = contactListArray.get(position);

            holder.chatName = (TextView) convertView.findViewById(R.id.txtGroupName);
            holder.chatDate = (TextView) convertView.findViewById(R.id.txtGroupDate);
            holder.txtCounter = (TextView) convertView.findViewById(R.id.txtChatCounter);
            holder.removeChat = (ImageView) convertView.findViewById(R.id.removeChat);
            holder.txtCounter.setVisibility(View.INVISIBLE);
            if (group != null) {

                if (group.counter > 0) {
                    holder.txtCounter.setVisibility(View.VISIBLE);
                    holder.txtCounter.setText("" + group.counter);
                }

                try {
                    holder.chatName.setText(group.chatName.substring(0, group.chatName.indexOf(Constants.UDID)));
                } catch (Exception e) {
                    holder.chatName.setText(group.chatName);
                    Log.d("GROUPNAME PARSE", e.getMessage());
                }

                if (group.createDate != null) {
                    holder.chatDate.setText("" + group.createDate);
                }


            }

            holder.removeChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showGPSDisabledAlertToUser(group, position);
                }
            });
        } catch (NullPointerException ex) {

        }

        return convertView;
    }

    public void updateGroup(List<ChatGroup> groupList) {
        if (groupList != null) {
            contactListArray.addAll(groupList);
        }
        notifyDataSetChanged();
    }

    public void updateGroupCounter(List<ChatGroup> groupList) {
        if (groupList != null) {
//            contactListArray.clear();
            if (contactListArray.contains(groupList.get(0))) {
                contactListArray.set(contactListArray.indexOf(groupList.get(0)), groupList.get(0));
            } else
                contactListArray.add(groupList.get(0));
        }
        notifyDataSetChanged();
    }

    public void updateGroupCounter2(ChatGroup group) {
        if (group != null) {
//            contactListArray.clear();
            contactListArray.add(group);
        }
        notifyDataSetChanged();
    }

    class ViewHolder {
        TextView chatName;
        TextView chatDate;
        TextView txtCounter;
        ImageView removeChat;
    }

    private void showGPSDisabledAlertToUser(final ChatGroup chatGroup, final int position) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder
                .setMessage(
                        context.getString(R.string.txt_confirm_delete))
                .setCancelable(false)
                .setPositiveButton(context.getString(R.string.txt_yes),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (!Util.isInternetAvailable(context)) {
                                    Toast.makeText(context, context.getString(R.string.internet_not_available_try), Toast.LENGTH_SHORT).show();
                                } else {
                                    JSONObject jsonbody = new JSONObject();
                                    try {
                                        jsonbody.put("chatID", "" + contactListArray.get(position).chatID);
                                        HttpCaller
                                                .getInstance()
                                                .removeChatToNetwork(contactListArray.get(position).chatID,
                                                        new Response.Listener<JSONObject>() {
                                                            @Override
                                                            public void onResponse(JSONObject response) {
                                                                try {
                                                                    if (response.getBoolean("Status")) {
                                                                        Helper.showToast("" + response.getString("Message").toString(), ToastStyle.SUCCESS);
                                                                    } else {
                                                                        Helper.showToast("" + response.getString("Reason").toString(), ToastStyle.ERROR);
                                                                    }
                                                                } catch (Exception e) {
                                                                    e.printStackTrace();
                                                                }
                                                            }

                                                        },
                                                        new Response.ErrorListener() {
                                                            @Override
                                                            public void onErrorResponse(VolleyError error) {
                                                                System.out.println("");
                                                            }
                                                        }, true);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    contactListArray.remove(position);
                                    notifyDataSetChanged();
                                }
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


}




