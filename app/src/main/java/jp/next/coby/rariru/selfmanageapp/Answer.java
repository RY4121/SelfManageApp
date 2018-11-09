package jp.next.coby.rariru.selfmanageapp;

import java.io.Serializable;

public class Answer implements Serializable {
    private String mTitle;//Smallタスクの”タイトル”
    private String mName;
    private String mUid;
    private String mAnswerUid;

    private String mBody;//Smallタスクの”内容”

    public Answer(String title, String name, String uid, String answerUid) {
        mTitle = title;
        mName = name;
        mUid = uid;
        mAnswerUid = answerUid;

        //Smallタスクの”内容”
        mBody = answerUid;
    }


    public String getTitle() {
        return mTitle;
    }

    public String getName() {
        return mName;
    }

    public String getUid() {
        return mUid;
    }

    public String getAnswerUid() {
        return mAnswerUid;
    }

    public String getBody(){return mBody;}
}
