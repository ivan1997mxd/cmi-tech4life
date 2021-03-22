package com.estethapp.media;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.crashlytics.android.Crashlytics;
import com.estethapp.media.adapter.ChatAdapter;
import com.estethapp.media.db.ChatGroup;
import com.estethapp.media.db.Comments;
import com.estethapp.media.db.JoinGroupContact;
import com.estethapp.media.util.App;
import com.estethapp.media.util.ChatUtil;
import com.estethapp.media.util.Constants;
import com.estethapp.media.util.Contact;
import com.estethapp.media.util.MyContactsUtil;
import com.estethapp.media.util.Prefs;
import com.estethapp.media.util.UserModel;
import com.estethapp.media.util.Util;
import com.estethapp.media.view.ProgressDialogView;
import com.estethapp.media.web.HttpCaller;
import com.estethapp.media.web.WebURL;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import io.fabric.sdk.android.Fabric;

import static com.estethapp.media.util.App.isCommentDetail;
import static com.estethapp.media.util.App.isGroupChat;


/**
 * Created by Irfan Ali on 3/16/2017.
 */

public class ChatDetailActivity extends AppCompatActivity implements View.OnClickListener {

    ProgressDialogView dialogInstance = ProgressDialogView.getInstance();
    ProgressDialog progressDialog;
    MyContactsUtil contactsPickedUtil = MyContactsUtil.getInstance();
    private ListView chatGroupListView;
    private ChatAdapter chatAdapter;
    private ProgressBar downloadProgress;
    private TextView mTitle, mTxtGroupName;
    private ImageButton btnSend, btnPlay, btnDownload;
    ImageView btnAddContact;
    private EditText editTextChat;
    public static ChatGroup group;
    private List<Comments> chatComments;
    private UserModel profileModel;
    private SharedPreferences userPrefsInance;
    private Prefs prefs;
    private TextView emptyResults;
    String fileUrl = "";

    private ChatUtil chatUtil = ChatUtil.getInstance();

    long total = 0;
    int lenghtOfFile = 0;
    private int progress = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(getApplicationContext(), new Crashlytics());
        setContentView(R.layout.activity_chat_detail);
        contactsPickedUtil.clearList();
        App.chatDetailActivityContext = ChatDetailActivity.this;
        isCommentDetail = true;
        isGroupChat = false;
        prefs = Prefs.getInstance();
        Gson gson = new Gson();
        userPrefsInance = prefs.init(ChatDetailActivity.this);
        String json = userPrefsInance.getString(prefs.USERMODEL, "");
        profileModel = gson.fromJson(json, UserModel.class);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mTitle = (TextView) toolbar.findViewById(R.id.txt_groupname);
        mTxtGroupName = (TextView) toolbar.findViewById(R.id.txt_group_member);
        btnPlay = (ImageButton) toolbar.findViewById(R.id.btnPlay);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mTxtGroupName.setText(getString(R.string.txt_me) + " " + chatUtil.getGroupNamesList(group, profileModel.UserID));

        try {
            mTitle.setText(group.chatName.substring(0, group.chatName.indexOf(Constants.UDID)));
        } catch (Exception e) {
            mTitle.setText(group.chatName);
        }


        editTextChat = (EditText) findViewById(R.id.conversation_message);
        btnDownload = (ImageButton) findViewById(R.id.btn_download);
        btnAddContact = (ImageView) findViewById(R.id.add_button);
        downloadProgress = (ProgressBar) findViewById(R.id.downloadProgress);

        btnSend = (ImageButton) findViewById(R.id.btnSend);

        chatGroupListView = (ListView) findViewById(R.id.chatListview);
        emptyResults = (TextView) findViewById(R.id.emptyResults);
        chatComments = chatUtil.loadComments(group.chatID);
        if (chatComments.size() <= 0) {
            emptyResults.setVisibility(View.VISIBLE);
        } else {
            emptyResults.setVisibility(View.GONE);
        }
        chatAdapter = new ChatAdapter(ChatDetailActivity.this, chatComments);
        chatGroupListView.setAdapter(chatAdapter);
        updateLastItem();
        btnSend.setOnClickListener(this);
        btnPlay.setOnClickListener(this);

        if (group.userID == profileModel.UserID) {
            fileUrl = Util.getDirectory() + "/" + group.fileName;
            File isFileDownloaded = new File(fileUrl);
            if (isFileDownloaded.exists()) {
                btnPlay.setVisibility(View.VISIBLE);
            } else {
                fileUrl = Util.getRecvAudioFilesDirectory() + "/" + group.fileName;
                isFileDownloaded = new File(fileUrl);
                if (isFileDownloaded.exists()) {
                    btnPlay.setVisibility(View.VISIBLE);
                } else {
                    btnPlay.setVisibility(View.INVISIBLE);
                    btnDownload.setVisibility(View.VISIBLE);

                }


            }
        } else {
            fileUrl = Util.getRecvAudioFilesDirectory() + "/" + group.fileName;
            File isFileDownloaded = new File(fileUrl);
            if (isFileDownloaded.exists()) {
                btnPlay.setVisibility(View.VISIBLE);
            } else {
                btnPlay.setVisibility(View.INVISIBLE);
                btnDownload.setVisibility(View.VISIBLE);
            }
        }

        btnDownload.setOnClickListener(this);
        btnAddContact.setOnClickListener(this);

        hideSoftKeyboard();
    }

    public void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.btnSend:
                String msg = editTextChat.getText().toString();
                Comments comments = chatUtil.saveComment(msg, group.chatID, profileModel.UserID, true, chatUtil.getCurrentChatTime(), false);
                chatComments.add(comments);
                List<Comments> commentList = new ArrayList<>();
                commentList.add(comments);
                chatAdapter.updateComment(commentList);
                new sendComment(msg).execute();
                updateLastItem();

                editTextChat.setText("");
                break;

            case R.id.btnPlay:
                String exminingInfo = group.exminingInfo;
                Intent intent = new Intent(ChatDetailActivity.this, ActivityPlayer.class);
                intent.putExtra("Exmining", exminingInfo);
                intent.putExtra("dir", fileUrl);
                intent.putExtra("fileName", group.fileName);
                startActivity(intent);
                break;

            case R.id.btn_download:
                new DownloadFileFromURL(group.fileName).execute();
                break;

            case R.id.add_button:
                Intent inte = new Intent(ChatDetailActivity.this, ContactActivity.class);
                startActivityForResult(inte, 5000);
                break;
            default:
        }
    }

    public void updateLastItem() {
        chatGroupListView.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                chatGroupListView.setSelection(chatAdapter.getCount() - 1);
            }
        });

    }

    public void notifyByChangeGroup(final Comments comment) {
        runOnUiThread(new Runnable() {
            public void run() {

                chatComments = chatUtil.loadComments(group.chatID);

                if (chatComments.size() <= 0) {
                    emptyResults.setVisibility(View.VISIBLE);
                } else {
                    emptyResults.setVisibility(View.GONE);
                }

                List<Comments> groupList = new ArrayList<>();
                chatComments.add(comment);
                groupList.add(comment);
                //chatAdapter.updateComment(groupList);
                updateLastItem();
            }
        });
    }


    public void notifyByRecievedComment(final Comments comment) {
        runOnUiThread(new Runnable() {
            public void run() {
                chatComments = chatUtil.loadComments(group.chatID);

                if (chatComments.size() <= 0) {
                    emptyResults.setVisibility(View.VISIBLE);
                } else {
                    emptyResults.setVisibility(View.GONE);
                }
                List<Comments> groupList = new ArrayList<>();
                chatComments.add(comment);
                groupList.add(comment);
                chatAdapter.updateComment(groupList);
                updateLastItem();
            }
        });
    }


    private class sendComment extends AsyncTask<Void, Void, String> {
        String comment;

        public sendComment(String comment) {
            this.comment = comment;
        }

        @Override
        protected void onPostExecute(String result) {
        }

        @Override
        protected String doInBackground(Void... voids) {

//            HttpCaller
//                    .getInstance().uploadFile(filePath,"");

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("FromUserID", "" + profileModel.UserID);
                jsonObject.put("chatID", group.chatID);
                jsonObject.put("CreateDate", "" + chatUtil.getCurrentChatTime());
                jsonObject.put("comment", comment);
                jsonObject.put("NotificationType", "NEW_MESSAGE");
            } catch (JSONException e) {
                e.printStackTrace();
            }


            HttpCaller
                    .getInstance()
                    .sendNotification(jsonObject,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        if (response.getBoolean("Status")) {

                                        }
                                    } catch (Exception e) {
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    //   progressDialogDismiss();
                                }
                            });

            return "";
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    public void callDownloadAsync(String fileName) {
        new DownloadFileFromURL(fileName).execute();
    }

    public class DownloadFileFromURL extends AsyncTask<String, String, String> {

        String fileUrl;
        String fileName;

        public DownloadFileFromURL(String fileName) {
            this.fileName = fileName;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /**
         * Downloading file in background thread
         */
        @Override
        protected String doInBackground(String... f_url) {

            try {

                runOnUiThread(new Runnable() {
                    public void run() {
                        btnDownload.setVisibility(View.INVISIBLE);
                        downloadProgress.setVisibility(View.VISIBLE);
                    }
                });

                int count;
                fileUrl = WebURL.URL + "audiofiles/" + fileName;
                URL url = new URL(fileUrl);
                URLConnection conection = url.openConnection();
                conection.connect();
                // getting file length
                lenghtOfFile = conection.getContentLength();

                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                // Output stream to write file
                OutputStream output = new FileOutputStream(Util.getRecvAudioFilesDirectory() + "/" + fileName);

                byte data[] = new byte[1024];


                while ((count = input.read(data)) != -1) {
                    // writing data to file
                    total += count;

                    output.write(data, 0, count);
                }

                runOnUiThread(new Runnable() {
                    public void run() {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                downloadProgress.setVisibility(View.INVISIBLE);
                            }
                        });
                        btnPlay.setVisibility(View.VISIBLE);
                        btnDownload.setVisibility(View.INVISIBLE);

                    }
                });

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }

        protected void onProgressUpdate(String... progress) {

        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 5000 && resultCode == Activity.RESULT_OK) {
            new SendInvitation(group.chatID).execute();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    private class SendInvitation extends AsyncTask<Void, Void, String> {
        int chatGroupID;

        public SendInvitation(int chatGroupID) {
            this.chatGroupID = chatGroupID;
        }

        @Override
        protected void onPostExecute(String result) {
            mTxtGroupName.setText(getString(R.string.txt_me) + " " + chatUtil.getGroupNamesList(group, profileModel.UserID));
        }

        @Override
        protected String doInBackground(Void... voids) {


            JSONArray array = new JSONArray();

            for (Contact selectedContact : contactsPickedUtil.getContactsList()) {
                JSONObject favObject = new JSONObject();
                try {
                    favObject.put("UserID", Integer.parseInt(selectedContact.UserID));
                    favObject.put("ChatID", chatGroupID);
                    favObject.put("UserName", selectedContact.Name);
                    favObject.put("ImgUrl", selectedContact.ProfilePhoto);
                    array.put(favObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JoinGroupContact joinGroupContact1 = new JoinGroupContact();
                joinGroupContact1.userName = selectedContact.Name;
                joinGroupContact1.userID = Integer.parseInt(selectedContact.UserID);
                joinGroupContact1.chatID = chatGroupID;
                joinGroupContact1.imgURL = selectedContact.ProfilePhoto;
                joinGroupContact1.save();
            }

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("FromUserID", "" + profileModel.UserID);
                jsonObject.put("JoinedContact", array);
                jsonObject.put("NotificationType", "ADDED_IN_GROUP");
            } catch (JSONException e) {
                e.printStackTrace();
            }


            HttpCaller
                    .getInstance()
                    .sendNotification(jsonObject,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        progressDialogDismiss();
                                        if (response.getBoolean("Status")) {
                                            int groupID = Integer.parseInt(response.getString("GroupID"));
                                        }
                                    } catch (Exception e) {
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    progressDialogDismiss();
                                }
                            });

            return "";
        }

        @Override
        protected void onPreExecute() {
            progressDialogShow(getString(R.string.creating_group));

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    public void progressDialogShow(String txt) {
        progressDialog = dialogInstance.setProgressDialog(ChatDetailActivity.this, txt);
        progressDialog.show();
    }

    public void progressDialogDismiss() {
        progressDialog.dismiss();
    }

}
