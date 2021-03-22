package com.estethapp.media.db;


import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by DELL on 3/3/2017.
 */
@Table(name = "Comments")
public class Comments extends Model {

    @Column(name = "commentID")
    public int commentID;

    @Column(name = "comment")
    public String comment;

    @Column(name = "commentDate")
    public String commentDate;

    @Column(name = "isSender")
    public boolean isSender;

    @Column(name = "userID")
    public int userID;

    @Column(name = "chatID")
    public int chatID;

}
