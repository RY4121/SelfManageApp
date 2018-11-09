package jp.next.coby.rariru.selfmanageapp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Group implements Serializable {
    private String mTitle;//タスクのタイトル
    private String mBody;//タスクの内容
    private String mName;//タスクを追加したユーザー名
    private String mUid;//タスクを追加したユーザーのID
    private String mQuestionUid;//タスクのID
    private int mGenre;//タスクのジャンル
    private byte[] mBitmapArray;
    private ArrayList<Answer> mAnswerArrayList;//小タスクリスト  Answerクラスに定義されているのは

    private String mDate;//日時
    private String mTime;

    public String getTitle() {
        return mTitle;
    }

    public String getBody() {
        return mBody;
    }

    public String getName() {
        return mName;
    }

    public String getUid() {
        return mUid;
    }

    public String getQuestionUid() {
        return mQuestionUid;
    }

    public int getGenre() {
        return mGenre;
    }

    public byte[] getImageBytes() {
        return mBitmapArray;
    }

    public ArrayList<Answer> getAnswers() {
        return mAnswerArrayList;
    }

    public String getDate(){return mDate;}

    public String getTime(){return mTime;}

    public Group(String title, String body, String name, String uid, String questionUid, int genre, byte[] bytes, ArrayList<Answer> answers,String date,String time) {
        mTitle = title;
        mBody = body;
        mName = name;
        mUid = uid;
        mQuestionUid = questionUid;
        mGenre = genre;
        mBitmapArray = bytes.clone();
        mAnswerArrayList = answers;
        mDate = date;
        mTime = time;
    }


    public Group(String name,String uid){
        this.mName = name;
        this.mUid = uid;
    }

    public Group(String name,String uid,String faculty){
        this(name,uid);
        this.mTitle = faculty;
    }



}
