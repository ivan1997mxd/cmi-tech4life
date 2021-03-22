package com.estethapp.media.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.crashlytics.android.Crashlytics;
import com.estethapp.media.R;
import com.estethapp.media.adapter.AutoEditTextContactAdapter;
import com.estethapp.media.adapter.AutoTextViewAdapter;
import com.estethapp.media.adapter.ContactAdapter;
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

public class ContactFragment extends Fragment implements AdapterView.OnItemClickListener {
    ContactFragment fragment;
    private static Context context;
    static ContactFragment instance;
    View root;
    private AutoCompleteTextView edt_search;
    private ListView contactListView;


    private ContactAdapter contactAdapter = null;
    private SharedPreferences prefsInstance;
    private static Prefs prefs;

    ArrayList<Contact> myContacts;
    ArrayList<Contact> AllContacts;
    ArrayList<String> tempContactList;
    MyContactsUtil contactsPickedUtil = MyContactsUtil.getInstance();
    TextView emptyText;

    private static ProgressDialogView dialogInstance = ProgressDialogView.getInstance();
    private static ProgressDialog progressDialog;

    UserModel profileModel;
    private AutoEditTextContactAdapter cotnactSearchAdapter;

    public ContactFragment() {
        // Required empty public constructor
    }

    public static ContactFragment newInstance(Bundle bundle) {
        ContactFragment instance = new ContactFragment();
        return instance;
    }

    public static ContactFragment getInstance() {
        return instance;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        instance = this;
        return inflater.inflate(R.layout.activity_contact, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        context = getActivity();
        root = view;
        fragment = this;
        Fabric.with(context, new Crashlytics());

        prefs = Prefs.getInstance();
        prefsInstance = prefs.init(context);


        Gson gson = new Gson();
        String json = prefsInstance.getString(prefs.USERMODEL, "");
        profileModel = gson.fromJson(json, UserModel.class);

        loadMyContact();


        if (tempContactList == null) {
            new fetchAllContact().execute();
        }

        myContacts = new ArrayList<>();
        AllContacts = new ArrayList<>();
        tempContactList = new ArrayList<>();
        contactListView = (ListView) root.findViewById(R.id.contactListview);

        emptyText = (TextView) root.findViewById(R.id.emptyResults);
        emptyText.setVisibility(View.GONE);


        edt_search = (AutoCompleteTextView) root.findViewById(R.id.edit_text_contact_search);
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
        edt_search.setOnItemClickListener(this);

        contactAdapter = new ContactAdapter(context, myContacts, "" + profileModel.UserID);
        contactListView.setAdapter(contactAdapter);


        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        // TODO Auto-generated method stub


        if (AllContacts != null) {
            edt_search.setText("");
            if (AllContacts.size() > 0) {

                Contact selectedLedgersObject = (Contact) arg0.getItemAtPosition(arg2);
                if (isAlreadyAdded(selectedLedgersObject.UserID)) {
                    String test = selectedLedgersObject.Name.toLowerCase(Locale.getDefault());
                    contactAdapter.filter(test);
                } else {
                    JSONObject jsonbody = new JSONObject();
                    try {
                        jsonbody.put("MyID", "" + profileModel.UserID);
                        jsonbody.put("UserID", "" + selectedLedgersObject.UserID);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    new SyncContact(jsonbody).execute();

                    emptyText.setVisibility(View.GONE);
                    addMyContact(selectedLedgersObject);
                }


            }
        }
    }


    public void loadMyContact() {
        progressDialogShow(context.getString(R.string.txt_contacts_loading));
        HttpCaller
                .getInstance()
                .mycontactCall(context, "" + profileModel.UserID,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    progressDialog.dismiss();
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
                                                contact.UserID = jsonObject.getString("ContactID");
                                                myContacts.add(contact);
                                            }
                                        }
                                        contactsPickedUtil.setContactsList(myContacts);
                                        contactAdapter.setItems(myContacts);
                                        if (myContacts.size() > 0) {
                                            contactAdapter.notifyDataSetChanged();
/*                                            contactAdapter = new ContactAdapter(context,myContacts,""+profileModel.UserID);
                                            contactListView.setAdapter(contactAdapter);*/
                                        } else {
                                            emptyText.setVisibility(View.VISIBLE);
                                        }

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


    class fetchAllContact extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {

            HttpCaller
                    .getInstance()
                    .contactCall(context, profileModel.UserID,
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
//                                            ArrayAdapter<String> adapter = new ArrayAdapter<String>
//                                                    (context,android.R.layout.simple_list_item_1,tempContactList);
                                            AutoTextViewAdapter adapter = new AutoTextViewAdapter(getActivity(), R.layout.autotextview_row, R.id.itemname, AllContacts);
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
            progressDialogShow(context.getString(R.string.txt_upload_contacts));
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


    public static class removeContact extends AsyncTask<Void, Void, Void> {

        JSONObject jsonObject;

        public removeContact(JSONObject json) {
            this.jsonObject = json;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            HttpCaller
                    .getInstance()
                    .removeContatToNetwork(context, jsonObject,
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

    public void progressDialogShow(String txt) {
        progressDialog = dialogInstance.setProgressDialog(context, txt);
        progressDialog.show();
    }

    public void progressDialogDismiss() {
        progressDialog.dismiss();
    }


    public boolean isAlreadyAdded(String userid) {
        for (Contact contact : myContacts) {
            if (contact.UserID.equals(userid)) {
                return true;
            }
        }
        return false;
    }
}
