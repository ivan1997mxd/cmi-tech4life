package com.estethapp.media.view;

/**
 * Created by Irfan Ali on 1/20/2017.
 */
import android.content.Context;
import android.util.AttributeSet;

import com.estethapp.media.util.Contact;
//AutoCompleteTextView tha by kk 5/12/2020
public class CustomAutoCompleteTextView extends androidx.appcompat.widget.AppCompatAutoCompleteTextView {

    public CustomAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /** Returns the country name corresponding to the selected item */
    @Override
    protected CharSequence convertSelectionToString(Object selectedItem) {
        /** Each item in the autocompetetextview suggestion list is a hashmap object */
        Contact hm = (Contact) selectedItem;
        return hm.PatientID;
    }
}
