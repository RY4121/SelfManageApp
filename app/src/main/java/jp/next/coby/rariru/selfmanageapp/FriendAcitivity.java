package jp.next.coby.rariru.selfmanageapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.util.Base64;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.String.valueOf;

public class FriendAcitivity extends AppCompatActivity {
    private TextView mUserName, mFaculty, mUID;
    private ListView mListView;
    private FriendAdapter mAdapter;
    private ArrayList<Group> mUserArrayList;
    private DatabaseReference mFriendRef,mDatabaseReference;
    private String user;

    private HashMap Fmap;
    private FriendCheckAdapter mCheckAdapter;


    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_friend);

        setTitle("友達リスト");

        //checkBox用のアダプターの準備
        mCheckAdapter = new FriendCheckAdapter(this);
        mCheckAdapter.notifyDataSetChanged();

        //ListViewの準備
        mListView = (ListView) findViewById(R.id.FriendListView);
        mAdapter = new FriendAdapter(this);
        mUserArrayList = new ArrayList<Group>();
        mAdapter.notifyDataSetChanged();

        user = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFriendRef = mDatabaseReference.child(Const.UsersPATH).child(user).child(Const.FriendsPATH);
        mFriendRef.addChildEventListener(mFriendListener);

        //友達リストをクリアしてから再度Adapterにセットし、AdapterをListViewにセットし直す
        mUserArrayList.clear();
        mAdapter.setUserArrayList(mUserArrayList);
        mListView.setAdapter(mAdapter);

        // 渡ってきたジャンルの番号を保持する
        Intent intent = getIntent();
        boolean checkBoxFlag = intent.getBooleanExtra("CheckBoxFlag",false);
        if(checkBoxFlag){
            //友達リストをクリアしてから再度Adapterにセットし、AdapterをListViewにセットし直す
            mUserArrayList.clear();
            mCheckAdapter.setUserArrayList(mUserArrayList);
            mListView.setAdapter(mCheckAdapter);
            mCheckAdapter.notifyDataSetChanged();
        }
    }


    private ChildEventListener mFriendListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            final String fID = dataSnapshot.getKey();

            mFriendRef = mDatabaseReference.child(Const.UsersPATH).child(user).child(Const.FriendsPATH).child(fID);
            mFriendRef.addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            HashMap Fmap = (HashMap)dataSnapshot.getValue();
                            String name = (String)Fmap.get("name");
                            String uid = (String)Fmap.get("uid");
                            String faculty = (String)Fmap.get("title");

                            Group question = new Group(name,uid,faculty);
                            mUserArrayList.add(question);
                            mAdapter.notifyDataSetChanged();

                            mCheckAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            HashMap map = (HashMap) dataSnapshot.getValue();

            // 変更があったQuestionを探す
            for (Group question : mUserArrayList) {
                if (dataSnapshot.getKey().equals(question.getQuestionUid())) {
                    // このアプリで変更がある可能性があるのは回答(Answer)のみ
                    question.getAnswers().clear();
                    HashMap answerMap = (HashMap) map.get("answers");
                    if (answerMap != null) {
                        for (Object key : answerMap.keySet()) {
                            HashMap temp = (HashMap) answerMap.get((String) key);
                            String answerBody = (String) temp.get("body");
                            String answerName = (String) temp.get("name");
                            String answerUid = (String) temp.get("uid");
                            Answer answer = new Answer(answerBody, answerName, answerUid, (String) key);
                            question.getAnswers().add(answer);
                        }
                    }
                    mAdapter.notifyDataSetChanged();

                    mCheckAdapter.notifyDataSetChanged();
                }
            }

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };


}
