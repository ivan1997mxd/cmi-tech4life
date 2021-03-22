package com.estethapp.media.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Irfan ali  on 29/12/16.
 */
public class UserModel {

    public int UserID;               // AutoGenerated by Database
    public String  ChatCode;             /* Chat Code Which was Registered to AppLozic Chat API/SDK
                                        (Chat Code would be the combination of UserCode and the SocialNetwork Code) For Facebook = FB and for LinkedIn = LI
                                        Would be Like : ${Usercode}FB*/
    public String Name;
    public String PatientID;
    public String Gender;
    public String ProfilePhoto;
    public String Email;          // Date Format:  (dd/MM/YYYY)
    public String Password;
    public String GCMToken;
    public List<Contact> ContactList = new ArrayList<Contact>();
}