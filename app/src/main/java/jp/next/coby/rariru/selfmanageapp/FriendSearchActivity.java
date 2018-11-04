package jp.next.coby.rariru.selfmanageapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class FriendSearchActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mSeachButton;
    private ListView mListView;

    private FriendAdapter mAdapter;
    private ArrayList<Group> mUserArrayList;
    private DatabaseReference mFriendRef, mDatabaseReference;

    private String SearchET, fID;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    //既に友達リストにある友達のユーザーIDを保存する。
    private boolean friendFlag = false;
    private boolean friendSearchFlag = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friendsearch);
        setTitle("友達検索画面");

        mSeachButton = (Button) findViewById(R.id.SearchButton);
        mSeachButton.setOnClickListener(this);

        //ListViewの準備
        mListView = (ListView) findViewById(R.id.SeachResultListView);
        mAdapter = new FriendAdapter(this);
        mUserArrayList = new ArrayList<Group>();
        mAdapter.notifyDataSetChanged();

        //友達リストをクリアしたから再度Adapterにセットし、AdapterをListViewにセットし直す
        mUserArrayList.clear();
        mAdapter.setUserArrayList(mUserArrayList);
        mListView.setAdapter(mAdapter);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();


        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                friendFlag = true;

                if(friendSearchFlag == true) {
                    mFriendRef = mDatabaseReference.child(Const.UsersPATH).child(user.getUid()).child(Const.FriendsPATH);
                    mFriendRef.push().setValue(mUserArrayList.get(position));
                }else{
                    Toast.makeText(FriendSearchActivity.this,
                            "選択されたユーザーは既に友達登録されています。",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

    }


    @Override
    public void onClick(View arg0) {

        //テキスト入力を受け付けるビューを作成します。
        final EditText editView = new EditText(FriendSearchActivity.this);
        new AlertDialog.Builder(FriendSearchActivity.this)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setTitle("検索したいユーザーの表示名を入力してください")
                //setViewにてビューを設定します。
                .setView(editView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        /*入力した文字をトースト出力する
                        Toast.makeText(FriendSearchActivity.this,
                                editView.getText().toString(),
                                Toast.LENGTH_LONG).show();*/

                        SearchET = editView.getText().toString();

                        mFriendRef = mDatabaseReference.child(Const.UsersPATH);
                        mFriendRef.addChildEventListener(mFriendListener);
                    }
                })
                .setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                })
                .show();

    }

    private ChildEventListener mFriendListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            //"Users"下のユニークなユーザーIDを取得する
            fID = dataSnapshot.getKey();

            if (SearchET.equals(fID)) {
                mFriendRef = mDatabaseReference.child(Const.UsersPATH).child(SearchET);
                mFriendRef.addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                //ユーザーIDの中身email,name等を一括取得
                                HashMap Fmap = (HashMap) dataSnapshot.getValue();
                                String name = (String) Fmap.get("name");
                                String uid = (String) Fmap.get("uid");
                                String faculty = (String) Fmap.get("faculty");

                                Group question = new Group(name, uid, faculty);
                                mUserArrayList.add(question);
                                mAdapter.notifyDataSetChanged();
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                if(friendFlag) {
                    mFriendRef = mDatabaseReference.child(Const.UsersPATH).child(user.getUid()).child(Const.FriendsPATH);
                    mFriendRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String friendID = dataSnapshot.getKey();
                            mFriendRef = mDatabaseReference.child(Const.UsersPATH).child(user.getUid()).child(Const.FriendsPATH).child(friendID);
                            mFriendRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    HashMap Fmap = (HashMap) dataSnapshot.getValue();
                                    String uid = (String) Fmap.get("uid");

                                    if (SearchET.equals(uid)) {
                                        friendSearchFlag = true;
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            } else {
                /*Toast.makeText(FriendSearchActivity.this,
                        "入力されたユーザーIDは存在しません",
                        Toast.LENGTH_LONG).show();*/

                mUserArrayList.clear();
                mAdapter.setUserArrayList(mUserArrayList);
                mAdapter.notifyDataSetChanged();
            }


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
