package com.estethapp.media.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.estethapp.media.R;
import com.estethapp.media.db.Comments;
import com.estethapp.media.db.JoinGroupContact;
import com.estethapp.media.util.ChatUtil;

import java.util.List;


/**
 * Created by Irfan Ali on 1/6/2017.
 */
public class ChatAdapter extends BaseAdapter {

    private Context context;
    private List<Comments> commentsList;

    public ChatAdapter(Context context, List<Comments> contactList) {
        this.context = context;
        this.commentsList = contactList;

    }


    @Override
    public int getCount() {
        return commentsList.size();
    }

    @Override
    public Comments getItem(int position) {
        return commentsList.get(position);
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
            final Comments comments = commentsList.get(position);
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.chat_row, null);
                holder = new ViewHolder();

                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.chatRightThreadLayout = (LinearLayout) convertView.findViewById(R.id.chat_right_layout);
            holder.chatLeftThreadLayout = (LinearLayout) convertView.findViewById(R.id.chat_left_layout);


            if (comments.isSender) {
                holder.chatRightThreadLayout.setVisibility(View.VISIBLE);
                holder.chatLeftThreadLayout.setVisibility(View.GONE);

                holder.userImage = (ImageView) convertView.findViewById(R.id.userImg_right);
                holder.userName = (TextView) convertView.findViewById(R.id.userName_right);
                holder.chatMessage = (TextView) convertView.findViewById(R.id.chat_msg_right);
                holder.messageDate = (TextView) convertView.findViewById(R.id.chat_date_right);

                if (comments != null) {

                    JoinGroupContact groupContact = getUserInfo(comments.userID);
                    if (comments.isSender) {
                        holder.userName.setText(context.getString(R.string.txt_me));
                    } else if (groupContact != null) {
                        String userName = groupContact.userName;
                        holder.userName.setText(userName);
                    }


                    holder.chatMessage.setText(comments.comment);
                    if (comments.commentDate != null) {
                        holder.messageDate.setText(comments.commentDate);
                    }


                    JoinGroupContact info = getUserInfo(comments.userID);
                    if (info != null) {
                        String userProfile = info.imgURL;
                        if (!userProfile.equals("")) {
                            Glide.with(context).load(userProfile)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .fitCenter().placeholder(R.mipmap.default_contact_img)
                                    .crossFade()
                                    .into(holder.userImage);
                        } else {
                            Glide.with(context).load(R.mipmap.default_contact_img)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .fitCenter().placeholder(R.mipmap.default_contact_img)
                                    .crossFade()
                                    .into(holder.userImage);
                        }
                    } else {
                        Glide.with(context).load(R.mipmap.default_contact_img)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .fitCenter().placeholder(R.mipmap.default_contact_img)
                                .crossFade()
                                .into(holder.userImage);
                    }


                }

            } else {
                holder.chatLeftThreadLayout.setVisibility(View.VISIBLE);
                holder.chatRightThreadLayout.setVisibility(View.GONE);


                holder.userImage = (ImageView) convertView.findViewById(R.id.userImg_left);
                holder.userName = (TextView) convertView.findViewById(R.id.userName_left);
                holder.chatMessage = (TextView) convertView.findViewById(R.id.chat_msg_left);
                holder.messageDate = (TextView) convertView.findViewById(R.id.chat_date_left);

                if (comments != null) {

                    JoinGroupContact groupContact = getUserInfo(comments.userID);
                    if (comments.isSender) {
                        holder.userName.setText(context.getString(R.string.txt_me));
                    } else if (groupContact != null) {
                        String userName = groupContact.userName;
                        holder.userName.setText(userName);
                    }


                    holder.chatMessage.setText(comments.comment);
                    if (comments.commentDate != null) {
                        holder.messageDate.setText(comments.commentDate);
                    }


                    JoinGroupContact info = getUserInfo(comments.userID);
                    if (info != null) {
                        String userProfile = info.imgURL;
                        if (!userProfile.equals("")) {
                            Glide.with(context).load(userProfile)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .crossFade()
                                    .into(holder.userImage);
                        } else {
                            Glide.with(context).load(R.mipmap.default_contact_img)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .fitCenter().placeholder(R.mipmap.default_contact_img)
                                    .crossFade()
                                    .into(holder.userImage);
                        }
                    } else {
                        Glide.with(context).load(R.mipmap.default_contact_img)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .fitCenter().placeholder(R.mipmap.default_contact_img)
                                .crossFade()
                                .into(holder.userImage);
                    }


                }
            }


        } catch (NullPointerException ex) {

        }

        return convertView;
    }

    public JoinGroupContact getUserInfo(int userID) {
        for (JoinGroupContact joinGroupContact : ChatUtil.getInstance().getTotalJoinGroupContact()) {
            if (joinGroupContact.userID == userID)
                return joinGroupContact;
        }
        return null;
    }

    public void updateComment(List<Comments> commentsList1) {
        if (commentsList != null) {
            commentsList.addAll(commentsList1);
        }
        notifyDataSetChanged();
    }


    class ViewHolder {
        TextView userName;
        TextView chatMessage;
        TextView messageDate;
        ImageView userImage;
        LinearLayout chatLeftThreadLayout;
        LinearLayout chatRightThreadLayout;
    }
}




