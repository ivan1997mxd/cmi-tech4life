package com.estethapp.media.util;

import com.estethapp.media.db.ChatGroup;
import com.estethapp.media.db.Comments;
import com.estethapp.media.db.JoinGroupContact;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Irfan Ali on 3/18/2017.
 */

public class ChatUtil {

    Calendar cal;
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
    private List<ChatGroup> totalChatGroups = new ArrayList<>();
    private List<Comments> totalChatComments = new ArrayList<>();
    private List<JoinGroupContact> totalJoinGroupContact = new ArrayList<>();

    private static ChatUtil chatUtil;

    public static ChatUtil getInstance() {
        if (chatUtil == null) {
            chatUtil = new ChatUtil();
        }
        return chatUtil;
    }

    private ChatUtil() {

    }

    public List<ChatGroup> getTotalChatGroups() {
        return totalChatGroups;
    }

    public List<Comments> getTotalChatComments() {
        return totalChatComments;
    }

    public List<JoinGroupContact> getTotalJoinGroupContact() {
        return totalJoinGroupContact;
    }

    //    public void addGroup(String chatName, String fileName, String exminingInfo, String createDate, String userID, int groupID, boolean isComesFromNotification) {
//        ChatGroup comments = new Select().all().from(ChatGroup.class).where("chatName = ?", chatName).executeSingle();
//        if (comments == null) {
//            ChatGroup chatGroup = new ChatGroup();
//            chatGroup.chatID = groupID;
//            chatGroup.chatName = "" + chatName;
//            chatGroup.createDate = createDate;
//            chatGroup.updateDate = new Date();
//            chatGroup.fileName = fileName;
//            chatGroup.userID = Integer.parseInt(userID);
//            chatGroup.exminingInfo = exminingInfo;
//            if (isComesFromNotification) {
//                chatGroup.counter = 1;
//            }
//            chatGroup.save();
//
//            if (App.isGroupChat) {
//                App.chatActivityContext.notifyByChangeGroup(chatGroup);
//            }
//        }
//    }

    public void addGroup(String chatName, String fileName, String exminingInfo, String createDate, String userID, int groupID, boolean isComesFromNotification) {
        ChatGroup comments = getChatGroupByChatName(chatName);
        if (comments == null) {
            ChatGroup chatGroup = new ChatGroup();
            chatGroup.chatID = groupID;
            chatGroup.chatName = "" + chatName;
            chatGroup.createDate = createDate;
            chatGroup.updateDate = new Date();
            chatGroup.fileName = fileName;
            chatGroup.userID = Integer.parseInt(userID);
            chatGroup.exminingInfo = exminingInfo;
            if (isComesFromNotification) {
                chatGroup.counter = 1;
            }
            getTotalChatGroups().add(chatGroup);

            if (App.isGroupChat) {
                App.chatActivityContext.notifyByChangeGroup(chatGroup);
            }
        }
    }

//    public void refreshChatGroup(String chatName, String fileName, String exminingInfo, String createDate, String userID, int groupID, boolean isComesFromNotification) {
//        ChatGroup comments = new Select().all().from(ChatGroup.class).where("chatName = ?", chatName).executeSingle();
//        if (comments == null) {
//            ChatGroup chatGroup = new ChatGroup();
//            chatGroup.chatID = groupID;
//            chatGroup.chatName = "" + chatName;
//            chatGroup.createDate = createDate;
//            chatGroup.updateDate = new Date();
//            chatGroup.fileName = fileName;
//            chatGroup.userID = Integer.parseInt(userID);
//            chatGroup.exminingInfo = exminingInfo;
//            if (isComesFromNotification) {
//                chatGroup.counter = 1;
//            }
//            chatGroup.save();
//
//            if (App.isGroupChat) {
//                App.chatActivityContext.refreshChat(chatGroup);
//            }
//        }
//    }

    public void refreshChatGroup(String chatName, String fileName, String exminingInfo, String createDate, String userID, int groupID, boolean isComesFromNotification) {
        ChatGroup comments = getChatGroupByChatName(chatName);
        if (comments == null) {
            ChatGroup chatGroup = new ChatGroup();
            chatGroup.chatID = groupID;
            chatGroup.chatName = "" + chatName;
            chatGroup.createDate = createDate;
            chatGroup.updateDate = new Date();
            chatGroup.fileName = fileName;
            chatGroup.userID = Integer.parseInt(userID);
            chatGroup.exminingInfo = exminingInfo;
            if (isComesFromNotification) {
                chatGroup.counter = 1;
            }
            getTotalChatGroups().add(chatGroup);

            if (App.isGroupChat) {
                App.chatActivityContext.refreshChat(chatGroup);
            }
        }
    }

    private ChatGroup getChatGroupByChatName(String chatName) {
        for (ChatGroup chatGroup : getTotalChatGroups()) {
            if (chatGroup.chatName.equals(chatName))
                return chatGroup;
        }
        return null;
    }

//    public int getUniqueChatID() {
//        int size = new Select().all().from(Comments.class).execute().size();
//        size = size + 1;
//        return size;
//    }

    public int getUniqueChatID() {
        int size = getTotalChatComments().size();
        size = size + 1;
        return size;
    }

//    public List<Comments> loadComments(int chatID) {
//        return new Select().all().from(Comments.class).where("chatID = ?", chatID).execute();
//    }
//
//    public List<JoinGroupContact> loadJoinContact(int chatID) {
//        return new Select().all().from(JoinGroupContact.class).where("chatID = ?", chatID).execute();
//    }

    public List<Comments> loadComments(int chatID) {
        List<Comments> comments = new ArrayList<>();
        for (Comments comment : getTotalChatComments()) {
            if (comment.chatID == chatID)
                comments.add(comment);
        }

        return comments;
    }

    public List<JoinGroupContact> loadJoinContact(int chatID) {
        List<JoinGroupContact> joinGroupContacts = new ArrayList<>();
        for (JoinGroupContact groupContact : getTotalJoinGroupContact()) {
            if (groupContact.chatID == chatID)
                joinGroupContacts.add(groupContact);
        }

        return joinGroupContacts;
    }

    public String getGroupNamesList(ChatGroup group, int userID) {
        String names = "";
        List<JoinGroupContact> contacts = loadJoinContact(group.chatID);
        if (contacts != null) {
            for (JoinGroupContact contact : contacts) {
                if (contact.userID != userID) {
                    names += "," + contact.userName;
                }
            }
        }

        return names;
    }

//    public void addJoinContact(int ChatID, String UserName, int UserID, String imrUrl) {
//        try { //+UserID+" and chatID = "+ChatID
//            JoinGroupContact contact = new Select().all().from(JoinGroupContact.class).where("userID = " + ChatID + " AND chatID = " + ChatID).executeSingle();
//            if (contact == null) {
//                JoinGroupContact joinGroupContact1 = new JoinGroupContact();
//                joinGroupContact1.userName = UserName;
//                joinGroupContact1.userID = UserID;
//                joinGroupContact1.chatID = ChatID;
//                joinGroupContact1.imgURL = imrUrl;
//                joinGroupContact1.save();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }

    public void addJoinContact(int ChatID, String UserName, int UserID, String imrUrl) {
        try { //+UserID+" and chatID = "+ChatID
            JoinGroupContact contact = getGroupContactByUserIdAndChatId(UserID, ChatID);
            if (contact == null) {
                JoinGroupContact joinGroupContact1 = new JoinGroupContact();
                joinGroupContact1.userName = UserName;
                joinGroupContact1.userID = UserID;
                joinGroupContact1.chatID = ChatID;
                joinGroupContact1.imgURL = imrUrl;
                getTotalJoinGroupContact().add(joinGroupContact1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private JoinGroupContact getGroupContactByUserIdAndChatId(int userId, int chatId) {
        for (JoinGroupContact joinGroupContact : getTotalJoinGroupContact()) {
            if (joinGroupContact.userID == userId && joinGroupContact.chatID == chatId)
                return joinGroupContact;
        }

        return null;
    }

    public String getCurrentChatTime() {
        cal = Calendar.getInstance();
        String date = sdf.format(cal.getTime());
        return date;
    }

    //    public Comments saveComment(String comment, int groupID, int userID, boolean isSender, String CommentDate, boolean isComeFromNotification) {
//
//        Comments comments = new Comments();
//        comments.commentID = getUniqueChatID();
//        if (isComeFromNotification) {
//            comments.commentDate = getCurrentChatTime();
//        } else {
//            comments.commentDate = CommentDate;
//        }
//        comments.comment = comment;
//        comments.chatID = groupID;
//        comments.isSender = isSender;
//        comments.userID = userID;
//        comments.save();
//
//        if (isComeFromNotification) {
//            try {
//                ChatGroup chatGroup = new Select().all().from(ChatGroup.class).where("chatID = ?", groupID).executeSingle();
//                chatGroup.updateDate = new Date();
//                chatGroup.counter = ++chatGroup.counter;
//                chatGroup.save();
//                if (App.isGroupChat) {
//                    App.chatActivityContext.notifyByChangeCounterGroup(chatGroup);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//        if (App.isCommentDetail && App.chatDetailActivityContext.group.chatID == comments.chatID) {
//            if (isSender) {
//                App.chatDetailActivityContext.notifyByChangeGroup(comments);
//            } else {
//                App.chatDetailActivityContext.notifyByRecievedComment(comments);
//            }
//        }
//
//        return comments;
//    }

    public Comments saveComment(String comment, int groupID, int userID, boolean isSender,
                                String CommentDate, boolean isComeFromNotification) {

        Comments comments = new Comments();
        comments.commentID = getUniqueChatID();
        if (isComeFromNotification) {
            comments.commentDate = getCurrentChatTime();
        } else {
            comments.commentDate = CommentDate;
        }
        comments.comment = comment;
        comments.chatID = groupID;
        comments.isSender = isSender;
        comments.userID = userID;
        getTotalChatComments().add(comments);

        if (isComeFromNotification) {
            try {
                HashMap<Integer, ChatGroup> chatGroupWithId = getChatGroupByGroupId(groupID);
                if (chatGroupWithId != null) {
                    ChatGroup chatGroup = chatGroupWithId.get(chatGroupWithId.keySet().toArray()[0]);
                    chatGroup.updateDate = new Date();
                    chatGroup.counter = ++chatGroup.counter;
                    getTotalChatGroups().set((int) chatGroupWithId.keySet().toArray()[0], chatGroup);
                    if (App.isGroupChat) {
                        App.chatActivityContext.notifyByChangeCounterGroup(chatGroup);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (App.isCommentDetail && App.chatDetailActivityContext.group.chatID == comments.chatID) {
            if (isSender) {
                App.chatDetailActivityContext.notifyByChangeGroup(comments);
            } else {
                App.chatDetailActivityContext.notifyByRecievedComment(comments);
            }
        }

        return comments;
    }

    private HashMap<Integer, ChatGroup> getChatGroupByGroupId(int groupID) {
        for (int i = 0; i < getTotalChatGroups().size(); i++) {
            if (getTotalChatGroups().get(i).chatID == groupID) {
                HashMap<Integer, ChatGroup> map = new HashMap<>();
                map.put(i, getTotalChatGroups().get(i));
                return map;
            }
        }
//        for (ChatGroup chatGroup : ChatUtil.totalChatGroups) {
//            if (chatGroup.chatID == groupID)
//                return chatGroup;
//        }
        return null;
    }

    public void addGroup(JSONArray joinGroupArray) {
        try {
            for (int a = 0; a < joinGroupArray.length(); a++) {
                JSONObject object = joinGroupArray.getJSONObject(a);
                int ChatID = object.getInt("chatID");
                int UserID = object.getInt("userID");
                String UserName = object.getString("userName");
                String createDate = object.getString("createDate");
                String chatName = object.getString("chatName");
                String fileName = object.getString("fileName");
                String exminingInfo = object.getString("exminingInfo");
                chatUtil.addGroup(chatName, fileName, exminingInfo, createDate, "" + UserID, ChatID, false);

                JSONArray jsonComment = object.getJSONArray("Comments");
                for (int b = 0; b < jsonComment.length(); b++) {
                    JSONObject jsonObject = jsonComment.getJSONObject(b);
                    String comment = jsonObject.getString("Comment");
                    String CommentDate = jsonObject.getString("CommentDate");
                    boolean isSender = jsonObject.getBoolean("isSender");
                    int userID = jsonObject.getInt("UserID");
                    int groupID = jsonObject.getInt("ChatID");
                    saveComment(comment, groupID, userID, isSender, CommentDate, false);
                }

                JSONArray jsonContacts = object.getJSONArray("Contacts");
                for (int b = 0; b < jsonContacts.length(); b++) {
                    JSONObject jsonObject = jsonContacts.getJSONObject(b);
                    int userID = jsonObject.getInt("userID");
                    int chatID = jsonObject.getInt("chatID");
                    String userName = jsonObject.getString("UserName");
                    String imgURL = jsonObject.getString("imrUrl");
                    addJoinContact(chatID, userName, userID, imgURL);
                }

                //String chatName, String fileName, String exminingInfo,String userCreatedName,String userID, int groupID
            }
        } catch (Exception e) {
        }

    }

    public void addLocalGroup(JSONArray joinGroupArray) {
        try {
            for (int a = 0; a < joinGroupArray.length(); a++) {
                JSONObject object = joinGroupArray.getJSONObject(a);
                int ChatID = object.getInt("chatID");
                int UserID = object.getInt("userID");
                String UserName = object.getString("userName");
                String createDate = object.getString("createDate");
                String chatName = object.getString("chatName");
                String fileName = object.getString("fileName");
                String exminingInfo = object.getString("exminingInfo");
                chatUtil.refreshChatGroup(chatName, fileName, exminingInfo, createDate, "" + UserID, ChatID, false);

                JSONArray jsonComment = object.getJSONArray("Comments");
                for (int b = 0; b < jsonComment.length(); b++) {
                    JSONObject jsonObject = jsonComment.getJSONObject(b);
                    String comment = jsonObject.getString("Comment");
                    String CommentDate = jsonObject.getString("CommentDate");
                    boolean isSender = jsonObject.getBoolean("isSender");
                    int userID = jsonObject.getInt("UserID");
                    int groupID = jsonObject.getInt("ChatID");
                    saveComment(comment, groupID, userID, isSender, CommentDate, false);
                }

                JSONArray jsonContacts = object.getJSONArray("Contacts");
                for (int b = 0; b < jsonContacts.length(); b++) {
                    JSONObject jsonObject = jsonContacts.getJSONObject(b);
                    int userID = jsonObject.getInt("userID");
                    int chatID = jsonObject.getInt("chatID");
                    String userName = jsonObject.getString("UserName");
                    String imgURL = jsonObject.getString("imrUrl");
                    addJoinContact(chatID, userName, userID, imgURL);
                }

                //String chatName, String fileName, String exminingInfo,String userCreatedName,String userID, int groupID
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }
}
