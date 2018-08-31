package jp.next.coby.rariru.selfmanageapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static java.lang.String.valueOf;

public class FavoriteActivity extends AppCompatActivity {
    private ArrayList<Group> mFavoriteArrayList;
    private String user;
    private TasksListAdapter mAdapter;
    private ListView mListView;
    private DatabaseReference mfavoriteRef, mDataBaseReference;
    private int fGenre = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        setTitle("お気に入り");

        // ListViewの準備
        mListView = (ListView) findViewById(R.id.favoritelistView);
        mAdapter = new TasksListAdapter(this);
        mFavoriteArrayList = new ArrayList<Group>();
        mAdapter.notifyDataSetChanged();

        //ログインユーザーの取得
        user = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mDataBaseReference = FirebaseDatabase.getInstance().getReference();
        mfavoriteRef = mDataBaseReference.child(Const.FavoritePATH).child(user);
        mfavoriteRef.addChildEventListener(mFavoriteListener);

        // 質問のリストをクリアしてから再度Adapterにセットし、AdapterをListViewにセットし直す
        mFavoriteArrayList.clear();
        mAdapter.setQuestionArrayList(mFavoriteArrayList);
        mListView.setAdapter(mAdapter);


        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(),TaskDetailActivity.class);
                intent.putExtra("question",mFavoriteArrayList.get(position));
                startActivity(intent);
            }
        });
    }


    private ChildEventListener mFavoriteListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            HashMap Fmap = (HashMap) dataSnapshot.getValue();
            final String fID = dataSnapshot.getKey();
            fGenre = Integer.parseInt((String) Fmap.get("Genre"));
            //String fGenre = (String)Fmap.get("Genre");

            mfavoriteRef = mDataBaseReference.child(Const.ContentsPATH).child(valueOf(fGenre)).child(fID);
            //mfavoriteRef = mDataBaseReference.child(Const.ContentsPATH).child(fGenre).child(fID);
            mfavoriteRef.addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            HashMap Fmap = (HashMap)dataSnapshot.getValue();
                            String title = (String) Fmap.get("title");
                            String body = (String) Fmap.get("body");
                            String name = (String) Fmap.get("name");
                            String uid = (String) Fmap.get("uid");
                            String imageString = (String) Fmap.get("image");
                            byte[] bytes;
                            if (imageString != null) {
                                bytes = Base64.decode(imageString, Base64.DEFAULT);
                            } else {
                                bytes = new byte[0];
                            }
                            String date = (String)Fmap.get("date");
                            String time = (String)Fmap.get("time");

                            ArrayList<Answer> answerArrayList = new ArrayList<Answer>();
                            HashMap answerMap = (HashMap) Fmap.get("answers");
                            if (answerMap != null) {
                                for (Object key : answerMap.keySet()) {
                                    HashMap temp = (HashMap) answerMap.get((String) key);
                                    String answerBody = (String) temp.get("body");
                                    String answerName = (String) temp.get("name");
                                    String answerUid = (String) temp.get("uid");
                                    Answer answer = new Answer(answerBody, answerName, answerUid, (String) key);
                                    answerArrayList.add(answer);
                                }
                            }
                            Group question = new Group(title, body, name, uid, fID, fGenre, bytes, answerArrayList,date,time);
                            mFavoriteArrayList.add(question);
                            mAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
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

}
