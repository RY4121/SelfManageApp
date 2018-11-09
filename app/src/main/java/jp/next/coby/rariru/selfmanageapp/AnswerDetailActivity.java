package jp.next.coby.rariru.selfmanageapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.String.valueOf;

public class AnswerDetailActivity extends AppCompatActivity {

    Group mGroup;
    AnswerDetailListAdapter mAdapter;
    DatabaseReference mAnswerRef;
    ArrayList<Answer> mAnswerArrayList;

    private DatabaseReference dataBaseReference = FirebaseDatabase.getInstance().getReference();


    private ChildEventListener mEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            String answerUid = dataSnapshot.getKey();
            mAnswerRef = dataBaseReference.child(Const.ContentsPATH).child(valueOf(mGroup.getGenre())).child(mGroup.getQuestionUid()).child(Const.AnswersPATH).child(answerUid);
            mAnswerRef.addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            HashMap Amap = (HashMap)dataSnapshot.getValue();
                            String name = (String)Amap.get("name");
                            String uid = (String)Amap.get("uid");
                            String title = (String)Amap.get("title");

                            String bodyText = (String)Amap.get("body");

                            Answer answer = new Answer(title,name,uid,bodyText);
                            mAnswerArrayList.add(answer);
                            mAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

            /*for (Answer answer : mQuestion.getAnswers()) {
                // 同じAnswerUidのものが存在しているときは何もしない
                if (answerUid.equals(answer.getAnswerUid())) {
                    return;
                }
            }*/
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

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


    @Override
    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_answer_detail);

        Bundle extras = getIntent().getExtras();
        mGroup = (Group)extras.get("question");

        //listViewの準備
        ListView mListView = (ListView)findViewById(R.id.listView);
        mAdapter = new AnswerDetailListAdapter(this,mGroup);
        mAnswerArrayList = new ArrayList<Answer>();
        mAdapter.notifyDataSetChanged();


        //回答のデータをfirebaseに保存する
        mAnswerRef = dataBaseReference
                .child(Const.ContentsPATH)
                .child(valueOf(mGroup.getGenre()))
                .child(mGroup.getQuestionUid())
                .child(Const.AnswersPATH);
        mAnswerRef.addChildEventListener(mEventListener);


        //友達リストをクリアしてから再度Adapterにセットし、AdapterをListViewにセットし直す
        mAnswerArrayList.clear();
        mAdapter.setAnswerDetailArrayList(mAnswerArrayList);
        mListView.setAdapter(mAdapter);
    }

}
