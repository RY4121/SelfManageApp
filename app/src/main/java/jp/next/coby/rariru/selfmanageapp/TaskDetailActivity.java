package jp.next.coby.rariru.selfmanageapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class TaskDetailActivity extends AppCompatActivity {

    private ListView mListView;
    private Group mQuestion;
    private TaskDetailListAdapter mAdapter;

    private DatabaseReference mAnswerRef, mfavoriteRef, mGenreRef;
    private int FavoCODE = 100;

    private FloatingActionButton mFavorite;
    private boolean flag = false;
    private String user;
    //private ChildEventListener fEventListener;
    private TasksListAdapter module;


    //お気に入りボタンが押された時にお気に入り情報を参照した結果、
    //firebaseに存在した時の処理
    private ChildEventListener fEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            //参照してUidがある場合のみ呼ばれるメソッド
            flag = true;
            mFavorite.setImageResource(R.drawable.star_2);
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


    private ChildEventListener mEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            HashMap map = (HashMap) dataSnapshot.getValue();

            String answerUid = dataSnapshot.getKey();

            for (Answer answer : mQuestion.getAnswers()) {
                // 同じAnswerUidのものが存在しているときは何もしない
                if (answerUid.equals(answer.getAnswerUid())) {
                    return;
                }
            }

            String body = (String) map.get("body");
            String name = (String) map.get("name");
            String uid = (String) map.get("uid");

            Answer answer = new Answer(body, name, uid, answerUid);
            mQuestion.getAnswers().add(answer);
            mAdapter.notifyDataSetChanged();
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_detail);

        // 渡ってきたQuestionのオブジェクトを保持する
        Bundle extras = getIntent().getExtras();
        mQuestion = (Group) extras.get("question");

        setTitle(mQuestion.getTitle());

        // ListViewの準備
        mListView = (ListView) findViewById(R.id.listView);
        mAdapter = new TaskDetailListAdapter(this, mQuestion);
        mListView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        //気になるところ
        module = new TasksListAdapter(this);
        //module.TaskListAdapter(mQuestion);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // ログイン済みのユーザーを取得する
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user == null) {
                    // ログインしていなければログイン画面に遷移させる
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                } else {//ログインしている時の処理
                    // Questionを渡して回答作成画面を起動する
                    // --- ここから ---
                    Intent intent = new Intent(getApplicationContext(), AnswerSendActivity.class);
                    intent.putExtra("question", mQuestion);
                    startActivity(intent);
                    // --- ここまで ---
                }
            }
        });


        //回答のデータをfirebaseに保存する
        DatabaseReference dataBaseReference = FirebaseDatabase.getInstance().getReference();
        mAnswerRef = dataBaseReference
                .child(Const.ContentsPATH)
                .child(String.valueOf(mQuestion.getGenre()))
                .child(mQuestion.getQuestionUid())
                .child(Const.AnswersPATH);
        mAnswerRef.addChildEventListener(mEventListener);

    }


    @Override
    protected void onResume() {
        super.onResume();
        //課題の処理
        DatabaseReference favoriteBaseReference = FirebaseDatabase.getInstance().getReference();
        // mGenreRef = FirebaseDatabase.getInstance().getReference().child(Const.ContentsPATH).child(String.valueOf(mGenre));
        mFavorite = (FloatingActionButton) findViewById(R.id.favorite);
        FirebaseUser userC = FirebaseAuth.getInstance().getCurrentUser();

        if (userC == null) {
            //ログインしていない場合の処理
            //非表示かつ押せない
            mFavorite.setVisibility(View.GONE);
        } else {
            user = FirebaseAuth.getInstance().getCurrentUser().getUid();
            mfavoriteRef = favoriteBaseReference.child(Const.FavoritePATH).child(user).child(mQuestion.getQuestionUid());
            mfavoriteRef.addChildEventListener(fEventListener);

            //ボタンを表示
            if (flag == true) {
                mFavorite.setImageResource(R.drawable.star_2);
            } else {
                mFavorite.setImageResource(R.drawable.star);
            }
            mFavorite.setVisibility(View.VISIBLE);
            mFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    user = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    if (flag == false) {//お気に入りボタンが一度押された時の処理
                        //質問のデータをお気に入り一覧のクラスに渡しておく
                        /*Intent intent = new Intent(getApplicationContext(), FavoriteActivity.class);
                        intent.putExtra("question", mQuestion);*/

                        //favoriteのタグのところにジャンルの情報を与える処理
                        Map<String, String> Fdata = new HashMap<String, String>();
                        Fdata.put("Genre", String.valueOf(mQuestion.getGenre()));
                        mfavoriteRef.setValue(Fdata);
                        mFavorite.setImageResource(R.drawable.star_2);
                        flag = true;
                    } else { //お気に入りボタンがもう一度押された時の処理
                        //firebaseからの削除
                        mfavoriteRef.removeValue();
                        mFavorite.setImageResource(R.drawable.star);
                        flag = false;
                    }
                }
            });
        }
    }
}

