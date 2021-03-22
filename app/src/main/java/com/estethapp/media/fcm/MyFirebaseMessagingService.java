/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.estethapp.media.fcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import androidx.core.app.NotificationCompat;
import android.util.Log;

import com.estethapp.media.ChatActivity;
import com.estethapp.media.R;
import com.estethapp.media.util.App;
import com.estethapp.media.util.ChatUtil;
import com.estethapp.media.util.FileDownloader;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private String NEWMESSAGE = "NEW_MESSAGE";
    private String ADD_IN_GROUP = "ADDED_IN_GROUP";
    private static final String TAG = "MyFirebaseMsgService";
    private ChatUtil chatUtil = ChatUtil.getInstance();
    FileDownloader fileDownloader =new FileDownloader();
    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }
        Map<String, String> data =remoteMessage.getData();
        String type =data.get("type");
        String value = (new ArrayList<String>(data.values())).get(0);

        //String type =(new ArrayList<String>(data.values())).get(2);
        String dataJson="";
        String title = "";

        if(type.equals("ADDED_IN_GROUP")){
            title =(new ArrayList<String>(data.values())).get(6);
            try{
                dataJson =(new ArrayList<String>(data.values())).get(7);
            }catch(Exception e){

            }
            try {
                JSONObject jsonObject = new JSONObject(dataJson);
                final String chatName = jsonObject.getString("ChatName");
                final String fileName = jsonObject.getString("fileName");
                String userID = jsonObject.getString("userID");
                String exminingInfo = jsonObject.getString("exminingInfo");
                String userCreatedName = jsonObject.getString("userCreatedName");
                int groupID = jsonObject.getInt("groupID");
                String createDate = jsonObject.getString("createDate");





                chatUtil.addGroup(chatName,fileName,exminingInfo,createDate,userID,groupID,true);
                String groupContacts = jsonObject.getString("JoinGroup");
                JSONObject  JoinGroup = new JSONObject(groupContacts);
                String groupValue =JoinGroup.getString("Contacts");
                JSONArray joinGroupArray = JoinGroup.getJSONArray("Contacts");
                for(int a=0;a<joinGroupArray.length();a++){
                    JSONObject object =joinGroupArray.getJSONObject(a);
                    int ChatID = object.getInt("ChatID");
                    int UserID = object.getInt("UserID");
                    String UserName = object.getString("UserName");
                    String imgUrl = object.getString("ImgUrl");
                    chatUtil.addJoinContact(ChatID,UserName,UserID,imgUrl);
                }

                if(App.isCommentDetail){
                    App.chatDetailActivityContext.callDownloadAsync(fileName);
                    //App.chatDetailActivityContext.downloadFile(chatName);
                }else{
                    new Thread(new Runnable() {
                        public void run() {
                            fileDownloader.downloadFile(fileName);
                        }
                    }).start();

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        else if(type.equals("NEW_MESSAGE")){
            try{
                title =(new ArrayList<String>(data.values())).get(5);
                dataJson =(new ArrayList<String>(data.values())).get(6);

                JSONObject jsonObject = new JSONObject(dataJson);
                int chatID = Integer.parseInt(jsonObject.getString("chatID"));
                String comment =jsonObject.getString("comment");
                int UserID = Integer.parseInt(jsonObject.getString("FromUserID"));
                chatUtil.saveComment(comment,chatID,UserID,false,"",true);
            }catch(Exception e){
                e.printStackTrace();
            }


        }





        if (type.equals(NEWMESSAGE)) {

        } else if (type.equals(ADD_IN_GROUP)) {

        }

        showNotification(title);
    }
    // [END receive_message]

    private void showNotification(String title) {



        Intent intent = new Intent(this, ChatActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                intent, 0);


        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_notification_icon)
                .setContentTitle(title)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setSound(defaultSoundUri);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notificationBuilder.build());
    }


}
