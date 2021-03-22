package com.estethapp.media.db;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by Irfan Ali on 3/16/2017.
 */
@Table(name = "JoinGroupContact")
public class JoinGroupContact extends Model {

    @Column(name = "userID")
    public int userID;

    @Column(name = "userName")
    public String userName;

    @Column(name = "imgURL")
    public String imgURL;

    @Column(name = "chatID")
    public int chatID;

}
