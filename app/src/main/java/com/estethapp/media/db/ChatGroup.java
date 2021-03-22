package com.estethapp.media.db;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.Date;

/**
 * Created by Irfan Ali on 3/16/2017.
 */

@Table(name = "ChatGroup")
public class ChatGroup {

    @Column(name = "chatID")
    public int chatID;

    @Column(name = "chatName")
    public String chatName;

    @Column(name = "createDate")
    public String createDate;

    @Column(name = "fileName")
    public String fileName;

    @Column(name = "userID")
    public int userID;

    @Column(name = "userCreatedName")
    public String userCreatedName;

    @Column(name = "updateDate")
    public Date updateDate;


    @Column(name = "exminingInfo")
    public String exminingInfo;

    @Column(name = "counter")
    public int counter;


}
