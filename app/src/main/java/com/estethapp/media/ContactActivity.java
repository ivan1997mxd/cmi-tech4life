package com.estethapp.media;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.crashlytics.android.Crashlytics;
import com.estethapp.media.adapter.AutoTextViewAdapter;
import com.estethapp.media.adapter.ContactPickerAdapter;
import com.estethapp.media.listener.IContactPickerListener;
import com.estethapp.media.util.Contact;
import com.estethapp.media.util.Helper;
import com.estethapp.media.util.MyContactsUtil;
import com.estethapp.media.util.Prefs;
import com.estethapp.media.util.UserModel;
import com.estethapp.media.view.ProgressDialogView;
import com.estethapp.media.view.ToastStyle;
import com.estethapp.media.web.HttpCaller;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.services.concurrency.AsyncTask;

/**
 * Created by Irfan Ali on 1/9/2017.
 */


public class ContactActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, IContactPickerListener {

    Context context;
    private AutoCompleteTextView edt_search;
    private ListView contactListView;
    private Button addBtn, cancelBtn;


    private ContactPickerAdapter contactAdapter;
    private SharedPreferences prefsInstance;
    private Prefs prefs;

    ArrayList<Contact> myContacts;
    ArrayList<Contact> AllContacts;
    ArrayList<String> tempContactList;
    private ArrayList<Contact> selectedContacts = new ArrayList<>();

    ProgressDialogView dialogInstance = ProgressDialogView.getInstance();
    ProgressDialog progressDialog;

    UserModel profileModel;
    TextView emptyText;

    MyContactsUtil contactsPickedUtil = MyContactsUtil.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(getApplicationContext(), new Crashlytics());
        setContentView(R.layout.activity_contactpick);

        context = ContactActivity.this;
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        prefs = Prefs.getInstance();
        prefsInstance = prefs.init(context);

        myContacts = new ArrayList<>();
        AllContacts = new ArrayList<>();
        tempContactList = new ArrayList<>();

        Gson gson = new Gson();
        String json = prefsInstance.getString(prefs.USERMODEL, "");
        profileModel = gson.fromJson(json, UserModel.class);

        contactListView = (ListView) findViewById(R.id.contactListview);
        addBtn = (Button) findViewById(R.id.addBtn);
        cancelBtn = (Button) findViewById(R.id.cancelBtn);

        if (tempContactList.size() <= 0) {
            new fetchAllContact(profileModel.UserID).execute();
        }


        contactAdapter = new ContactPickerAdapter(context, myContacts,this);
        contactListView.setAdapter(contactAdapter);
        loadContact();

        emptyText = (TextView) findViewById(R.id.emptyResults);
        emptyText.setVisibility(View.GONE);


        edt_search = (AutoCompleteTextView) findViewById(R.id.edit_text_contact_search);
        edt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    String test = edt_search.getText().toString().toLowerCase(Locale.getDefault());
                    contactAdapter.filter(test);
                } catch (Exception ex) {
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSoftKeyboard();
                if (selectedContacts != null) {
                    contactsPickedUtil.setContactsList(getSelectedContacts());
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    Toast.makeText(ContactActivity.this, getString(R.string.txt_add_contact), Toast.LENGTH_SHORT).show();
                }

            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        edt_search.setOnItemClickListener(this);

        hideSoftKeyboard();
    }

    public void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }


    public ArrayList<Contact> getSelectedContacts() {
        return selectedContacts;
    }

    @Override
    public void addContact(Contact contact) {
        selectedContacts.add(contact);
    }

    @Override
    public void removeContact(Contact contact) {
        selectedContacts.remove(contact);
    }


    public void loadContact() {
        progressDialogShow(getString(R.string.txt_contacts_loading));
        HttpCaller
                .getInstance()
                .mycontactCall(context, "" + profileModel.UserID,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    progressDialog.dismiss();
                                    if (response.getBoolean("Status")) {

                                        Gson gson = new Gson();
                                        JSONArray jArray = response.getJSONArray("Response");
                                        if (jArray != null) {
                                            for (int i = 0; i < jArray.length(); i++) {
                                                JSONObject jsonObject = (JSONObject) jArray.get(i);
                                                Contact contact = new Contact();
                                                contact.Name = jsonObject.getString("Name");
                                                contact.PatientID = jsonObject.getString("PatientID");
                                                contact.ProfilePhoto = jsonObject.getString("ProfilePhoto");
                                                contact.Email = jsonObject.getString("Email");
                                                contact.UserID = jsonObject.getString("ContactID");
                                                myContacts.add(contact);
                                            }
                                        }

                                        contactsPickedUtil.setContactsList(myContacts);
                                        contactAdapter.setItems(myContacts);
                                        contactAdapter.notifyDataSetChanged();
                                    } else {
                                        emptyText.setVisibility(View.VISIBLE);
                                        Helper.showToast("" + response.getString("Reason").toString(), ToastStyle.ERROR);
                                    }


                                } catch (Exception e) {
                                    progressDialog.dismiss();
                                    e.printStackTrace();
                                }

                            }

                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                System.out.println("");
                                progressDialog.dismiss();
                            }
                        }, true);

    }

    public boolean isAlreadyAdded(String userid) {
        for (Contact contact : myContacts) {
            if (contact.UserID.equals(userid)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        if (AllContacts != null) {
            if (AllContacts.size() > 0) {

                Contact selectedLedgersObject = (Contact) arg0.getItemAtPosition(arg2);
                if (!isAlreadyAdded(selectedLedgersObject.UserID)) {
                    JSONObject jsonbody = new JSONObject();
                    try {
                        jsonbody.put("MyID", "" + profileModel.UserID);
                        jsonbody.put("UserID", "" + selectedLedgersObject.UserID);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    new SyncContact(jsonbody).execute();
                    addMyContact(selectedLedgersObject);
                    emptyText.setVisibility(View.GONE);
                } else {
                    String test = selectedLedgersObject.Name.toLowerCase(Locale.getDefault());
                    contactAdapter.filter(test);
                }


            }
        }
    }

    class SyncContact extends AsyncTask<Void, Void, JSONObject> {

        JSONObject jsonObject;

        public SyncContact(JSONObject jsonObject) {
            this.jsonObject = jsonObject;
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {

            HttpCaller
                    .getInstance()
                    .addContatToNetwork(context, jsonObject,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        progressDialog.dismiss();
                                        if (response.getBoolean("Status")) {
                                            Helper.showToast("" + response.getString("Message").toString(), ToastStyle.SUCCESS);
                                        } else {
                                            Helper.showToast("" + response.getString("Reason").toString(), ToastStyle.ERROR);
                                        }
                                    } catch (Exception e) {
                                        progressDialog.dismiss();
                                        e.printStackTrace();
                                    }
                                }

                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    System.out.println("");
                                    progressDialog.dismiss();
                                }
                            }, true);


            return null;
        }

        @Override
        protected void onPostExecute(JSONObject response) {
            super.onPostExecute(jsonObject);
        }

        @Override
        protected void onPreExecute() {
            progressDialogShow(getString(R.string.txt_upload_contacts));
        }
    }

    class fetchAllContact extends AsyncTask<Void, Void, Void> {
        int userID;

        public fetchAllContact(int userID1) {
            userID = userID1;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            HttpCaller
                    .getInstance()
                    .contactCall(context, userID,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        if (response.getBoolean("Status")) {
                                            JSONArray jArray = response.getJSONArray("Response");
                                            if (jArray != null) {
                                                for (int i = 0; i < jArray.length(); i++) {
                                                    JSONObject jsonObject = (JSONObject) jArray.get(i);
                                                    Contact contact = new Contact();
                                                    contact.Name = jsonObject.getString("Name");
                                                    contact.PatientID = jsonObject.getString("PatientID");
                                                    contact.ProfilePhoto = jsonObject.getString("ProfilePhoto");
                                                    contact.Email = jsonObject.getString("Email");
                                                    contact.UserID = jsonObject.getString("UserID");
                                                    AllContacts.add(contact);
                                                    tempContactList.add(contact.PatientID);
                                                }
                                            }
                                            contactsPickedUtil.setContactsList(AllContacts);
                                            /*ArrayAdapter<String> adapter = new ArrayAdapter<String>
                                                    (context,android.R.layout.simple_list_item_1,tempContactList);*/
                                            AutoTextViewAdapter adapter = new AutoTextViewAdapter(ContactActivity.this, R.layout.autotextview_row, R.id.itemname, AllContacts);
                                            edt_search.setAdapter(adapter);
                                        } else {
                                            //Helper.showToast(""+response.getString("Reason").toString(), ToastStyle.ERROR);
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

            return null;
        }
    }


    public int getArrayIndex(String userid) {
        for (int x = 0; x < AllContacts.size(); x++) {
            Contact contact = AllContacts.get(x);
            if (contact.UserID == userid) {
                return x;
            }
        }
        return -1;
    }

    public void addMyContact(Contact contact) {
        ArrayList<Contact> contactArrayList = contactsPickedUtil.getContactsList();
        contactArrayList.add(contact);
        myContacts.add(contact);
        //contactsPickedUtil.setContactsList(contactArrayList);
        if (contactAdapter != null) {
            contactAdapter.setItem(contact);
            contactAdapter.notifyDataSetChanged();
        }
    }


    public String getContactId(String username) {
        for (Contact contact : AllContacts) {
            if (contact.PatientID.equals(username)) {
                return contact.UserID;
            }
        }
        return "";
    }

    public void progressDialogShow(String txt) {
        progressDialog = dialogInstance.setProgressDialog(ContactActivity.this, txt);
        progressDialog.show();
    }
}
