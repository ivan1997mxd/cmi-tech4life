package com.estethapp.media.util;

import java.util.ArrayList;

/**
 * Created by Irfan Ali on 1/16/2017.
 */
public class MyContactsUtil {


    ArrayList<Contact> contactsList;
    public static MyContactsUtil instance;

    public static MyContactsUtil getInstance() {
        if (instance == null) {
            instance = new MyContactsUtil();
        }
        return instance;
    }


    public ArrayList<Contact> getContactsList() {
        return contactsList;
    }

    public void clearList() {
        if (contactsList != null) {
            contactsList.clear();
        }

    }

    public void setContactsList(ArrayList<Contact> contactsList) {
        this.contactsList = contactsList;
    }
}

