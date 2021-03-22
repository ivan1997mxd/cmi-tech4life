package com.estethapp.media;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.crashlytics.android.Crashlytics;
import com.estethapp.media.adapter.ChatGroupAdapter;
import com.estethapp.media.db.ChatGroup;
import com.estethapp.media.util.App;
import com.estethapp.media.util.ChatUtil;
import com.estethapp.media.util.Prefs;
import com.estethapp.media.util.UserModel;
import com.estethapp.media.view.ProgressDialogView;
import com.estethapp.media.web.HttpCaller;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.fabric.sdk.android.Fabric;

import static com.estethapp.media.util.App.isGroupChat;

/**
 * Created by Irfan Ali on 3/16/2017.
 */

public class ChatActivity extends AppCompatActivity {

    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
    private ListView chatGroupListView;
    private ProgressDialogView dialogInstance = ProgressDialogView.getInstance();
    private static ChatGroupAdapter chatGroupAdapter;
    private TextView mTitle, mTxtGroupName;
    private static Context context;
    List<ChatGroup> chatGroup = new ArrayList<>();
    private TextView emptyResults;
    private ImageButton btnRefresh;

    private UserModel profileModel;
    private SharedPreferences userPrefsInance;
    private Prefs prefs;

    private ChatUtil chatUtil = ChatUtil.getInstance();
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(getApplicationContext(), new Crashlytics());
        setContentView(R.layout.activity_chat);
        isGroupChat = true;

        App.chatActivityContext = ChatActivity.this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mTitle = (TextView) toolbar.findViewById(R.id.txt_groupname);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        context = getApplicationContext();


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        prefs = Prefs.getInstance();
        Gson gson = new Gson();
        userPrefsInance = prefs.init(ChatActivity.this);
        String json = userPrefsInance.getString(prefs.USERMODEL, "");
        profileModel = gson.fromJson(json, UserModel.class);

        emptyResults = (TextView) findViewById(R.id.emptyResults);
        btnRefresh = (ImageButton) findViewById(R.id.btnRefresh);
        chatGroupAdapter = null;
        chatGroupListView = (ListView) findViewById(R.id.chatListview);

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                requestForLoadChat(profileModel.UserID);
            }
        });


        populateList();

    }

    public List<ChatGroup> loadGroup() {
        Collections.sort(chatUtil.getTotalChatGroups(), new Comparator<ChatGroup>() {
            public int compare(ChatGroup o1, ChatGroup o2) {
                return o2.updateDate.compareTo(o1.updateDate);
            }
        });
        return chatUtil.getTotalChatGroups();
    }

    public void notifyByChangeGroup(final ChatGroup group) {
        runOnUiThread(new Runnable() {
            public void run() {

                chatGroup = loadGroup();
                if (chatGroup.size() <= 0) {
                    emptyResults.setVisibility(View.VISIBLE);
                } else {
                    emptyResults.setVisibility(View.GONE);
                    List<ChatGroup> groupList = new ArrayList<>();
                    groupList.add(group);
                    chatGroupAdapter.updateGroup(groupList);
                }

            }
        });
    }

    public void notifyByChangeCreateGroup(final ChatGroup group) {
        runOnUiThread(new Runnable() {
            public void run() {
                //    chatGroup = loadGroup();
                chatGroup = loadGroup();
                if (chatGroup == null) {
                    return;
                }
                if (chatGroup.size() <= 0) {
                    emptyResults.setVisibility(View.VISIBLE);
                } else {
                    emptyResults.setVisibility(View.GONE);
                    List<ChatGroup> groupList = new ArrayList<>();
                    groupList.add(group);
                    chatGroupAdapter.updateGroup(chatGroup);
                }

            }
        });
    }


    public void refreshChat(final ChatGroup group) {
        runOnUiThread(new Runnable() {
            public void run() {
                chatGroup = loadGroup();
                if (chatGroup == null) {
                    return;
                }
                if (chatGroup.size() <= 0) {
                    emptyResults.setVisibility(View.VISIBLE);
                } else {
                    emptyResults.setVisibility(View.GONE);
                    List<ChatGroup> groupList = new ArrayList<>();
                    groupList.add(group);
                    chatGroupAdapter.updateGroupCounter(groupList);
                }

            }
        });
    }


    public void notifyByChangeCounterGroup(final ChatGroup group) {
        runOnUiThread(new Runnable() {
            public void run() {
                chatGroup = loadGroup();
                if (chatGroup == null) {
                    return;
                }
                chatGroupAdapter.updateGroupCounter2(group);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void populateList() {
        isGroupChat = true;
        chatUtil.getTotalJoinGroupContact().clear();
        chatUtil.getTotalChatGroups().clear();
        chatUtil.getTotalChatComments().clear();

        //     chatGroup = loadGroup();
//        if (chatGroup == null) {
//            return;
//        }


        if (chatGroup.size() <= 0) {
            requestForLoadChat(profileModel.UserID);
        }

        if (chatGroup.size() <= 0) {
            emptyResults.setVisibility(View.VISIBLE);
        } else {
            emptyResults.setVisibility(View.GONE);
        }
        if (chatGroupAdapter == null) {
            chatGroupAdapter = new ChatGroupAdapter(ChatActivity.this, chatGroup);
            chatGroupListView.setAdapter(chatGroupAdapter);
        } else {
            chatGroupAdapter.updateGroup(null);
        }


        chatGroupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ChatGroup group = (ChatGroup) (chatGroupListView.getItemAtPosition(i));
                if (group.counter != 0) {
                    group.counter = 0;
                    chatUtil.getTotalChatGroups().set(chatUtil.getTotalChatGroups().indexOf(group), group);
                }
                ChatDetailActivity.group = group;
                startActivity(new Intent(ChatActivity.this, ChatDetailActivity.class));
            }
        });
    }

    public void requestForLoadChat(int userId) {

        if (chatUtil.getTotalJoinGroupContact() != null)
            chatUtil.getTotalJoinGroupContact().clear();
        if (chatUtil.getTotalChatGroups() != null)
            chatUtil.getTotalChatGroups().clear();
        if (chatUtil.getTotalChatComments() != null)
            chatUtil.getTotalChatComments().clear();

        progressDialogShow(getString(R.string.txt_loading_chat));
        HttpCaller
                .getInstance()
                .loadChat(userId,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    if (response.getBoolean("Status")) {
                                        JSONArray joinGroupArray = response.getJSONArray("Response");
                                        chatUtil.addLocalGroup(joinGroupArray);

                                        if (joinGroupArray.length() <= 0) {
                                            emptyResults.setVisibility(View.VISIBLE);
                                        } else {
                                            emptyResults.setVisibility(View.GONE);
                                        }

                                    } else {
                                        emptyResults.setVisibility(View.VISIBLE);
                                    }


                                    progressDialogDismiss();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    progressDialogDismiss();
                                    chatGroupAdapter.updateGroup(null);
                                }
                            }

                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                chatGroupAdapter.updateGroup(null);
                                progressDialogDismiss();
                            }
                        });
    }

    public void progressDialogShow(String txt) {
        progressDialog = dialogInstance.setProgressDialog(ChatActivity.this, txt);
        progressDialog.show();
    }

    public void progressDialogDismiss() {
        progressDialog.dismiss();
    }


}
