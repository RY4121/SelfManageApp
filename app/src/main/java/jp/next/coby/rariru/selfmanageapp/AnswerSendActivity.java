package jp.next.coby.rariru.selfmanageapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import static java.lang.String.valueOf;

public class AnswerSendActivity extends AppCompatActivity implements View.OnClickListener, DatabaseReference.CompletionListener {

    private EditText mAnswerEditText;
    private Group mQuestion;
    private ProgressDialog mProgress;

    private boolean flag;
    private String user;
    private DatabaseReference dataBaseReference,answerRef;
    private Button sendButton;

    private ChildEventListener fEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            //参照してUidがある場合のみ呼ばれるメソッド
            flag = true;
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
        setContentView(R.layout.activity_answer_send);

        setTitle("Smallタスク追加画面");

        // 渡ってきたQuestionのオブジェクトを保持する
        Bundle extras = getIntent().getExtras();
        mQuestion = (Group) extras.get("question");

        // UIの準備
        mAnswerEditText = (EditText) findViewById(R.id.answerEditText);
        mProgress = new ProgressDialog(this);
        mProgress.setMessage("投稿中...");

        sendButton = (Button) findViewById(R.id.sendButton);
        sendButton.setOnClickListener(this);
    }

    @Override
    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
        mProgress.dismiss();

        if (databaseError == null) {
            finish();
        } else {
            Snackbar.make(findViewById(android.R.id.content), "投稿に失敗しました", Snackbar.LENGTH_LONG).show();
        }

    }

    @Override
    public void onClick(View v) {
        // キーボードが出てたら閉じる
        InputMethodManager im = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

        dataBaseReference = FirebaseDatabase.getInstance().getReference();
        answerRef = dataBaseReference.child(Const.ContentsPATH).child(valueOf(mQuestion.getGenre())).child(mQuestion.getQuestionUid()).child(Const.AnswersPATH);

        Map<String, String> data = new HashMap<String, String>();


        // UID
        data.put("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
/*
        // 表示名
        // Preferenceから名前を取る
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String name = sp.getString(Const.NameKEY, "");
        data.put("name", name);*/

        // 回答を取得する
        String answer = mAnswerEditText.getText().toString();

        if (answer.length() == 0) {
            // 回答が入力されていない時はエラーを表示するだけ
            Snackbar.make(v, "タスクを入力して下さい", Snackbar.LENGTH_LONG).show();
            return;
        }
        data.put("body", answer);

        mProgress.show();
        answerRef.push().setValue(data, this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        dataBaseReference = FirebaseDatabase.getInstance().getReference();
        answerRef = dataBaseReference.child(Const.ContentsPATH).child(valueOf(mQuestion.getGenre())).child(mQuestion.getQuestionUid()).child(Const.AnswersPATH);
        answerRef.addChildEventListener(fEventListener);

        if(flag == true){
            sendButton.setVisibility(View.GONE);
        }
    }

}
