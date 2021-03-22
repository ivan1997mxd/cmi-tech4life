package com.estethapp.media.listener;

import com.estethapp.media.util.Contact;

public interface IContactPickerListener {
    void addContact(Contact contact);
    void removeContact(Contact contact);
}
