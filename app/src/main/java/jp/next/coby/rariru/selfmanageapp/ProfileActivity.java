package jp.next.coby.rariru.selfmanageapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static android.widget.Toast.makeText;

public class ProfileActivity extends AppCompatActivity {

    DatabaseReference mDataBaseReference, ProfileRef;
    private String user;
    private TextView mUIDTextView, mEmailTextView;
    private ImageButton AtButton;
    private WebView webView;
    private ChildEventListener mProfileListener;

    private Spinner mSpinner;
    private String SpiText;
    private EditText mEmailEditText;


    private ChildEventListener mProfileLisener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

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
        setContentView(R.layout.activity_profile);

        //UI部品の取得とユーザーIDを取得してTextViewに反映させる
        setTitle("プロフィール画面");
        mUIDTextView = (TextView) findViewById(R.id.UIDTextView);
        mEmailTextView = (TextView) findViewById(R.id.EmailTextView);

        webView = (WebView) findViewById(R.id.webView);
        AtButton = (ImageButton) findViewById(R.id.attendButton);
        //mailAdressが表示されているときのみ、遷移できる
        AtButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Uri uri = Uri.parse("https://service.cloud.teu.ac.jp/portal/index");
                Intent i = new Intent(Intent.ACTION_VIEW,uri);
                startActivity(i);
                */
                if (mEmailEditText.getText().length() != 18) {
                    //mailAdressの選択値がデフォルトではない場合
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    //データタイプを指定
                    intent.setType("message/rfc822");
                    //宛先を指定
                    intent.putExtra(Intent.EXTRA_EMAIL, new String[]{mEmailEditText.getText().toString()});
                    //Intentを発行
                    startActivity(intent);
                }else{
                    Snackbar.make(v,"メールアドレスを選択してください。",Snackbar.LENGTH_LONG).show();
                }
            }
        });

        mSpinner = (Spinner) findViewById(R.id.spinner);
        mSpinner.setOnItemSelectedListener(mMailClickListener);
        mEmailEditText = (EditText) findViewById(R.id.mailEditText);

        mDataBaseReference = FirebaseDatabase.getInstance().getReference();
        try {
            user = FirebaseAuth.getInstance().getCurrentUser().getUid();
            //ProfileRef = mDataBaseReference.child(Const.UsersPATH).child(user);
            //ProfileRef.addChildEventListener(mProfileListener);
        } catch (NullPointerException e) {
            //Snackbar用にViewを取得
            View view = findViewById(android.R.id.content);
            //Toast.makeText(this, "ログインするとUIDが表示されます", Toast.LENGTH_LONG).show();
            Snackbar
                    .make(view, "ログインするとUIDが表示されます", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Login", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(intent);
                        }
                    })
                    .show();

            mUIDTextView.setText("ログインしているとここにUIDが表示されます");
            mEmailTextView.setText("ログインしているとここにユーザーのメールアドレスが表示されます");
        }

        //ログインしている場合の処理
        if (user != null) {
            FirebaseUser Euser = FirebaseAuth.getInstance().getCurrentUser();
            mUIDTextView.setText(user);
            mUIDTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    //データタイプを指定
                    intent.setType("text/plain");
                    //送る値
                    intent.putExtra(Intent.EXTRA_TEXT, mUIDTextView.getText());
                    //Intentの発行
                    startActivity(intent);
                    Snackbar.make(v, "ユーザーIDを送ります。", Snackbar.LENGTH_LONG).show();
                }
            });

            mEmailTextView.setText(Euser.getEmail());
        } else {
            //do nothing
        }

    }

    private AdapterView.OnItemSelectedListener mMailClickListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Spinner spinner = (Spinner) parent;
            //選択されたアイテムを取得
            String item = (String) spinner.getSelectedItem();
            SpiText = item;
            if (item.equals("松下宗一郎")) {
                mEmailEditText.setText("matsushita@stf.teu.ac.jp");
            } else if (item.equals("宇田隆哉")) {
                mEmailEditText.setText("uda@stf.teu.ac.jp");
            } else if (item.equals("生野壮一郎")) {
                mEmailEditText.setText("ikuno@stf.teu.ac.jp");
            } else if (item.equals("布田裕一")) {
                mEmailEditText.setText("futayi@stf.teu.ac.jp");
            } else if (item.equals("山本進")) {
                mEmailEditText.setText("syamamoto@stf.teu.ac.jp");
            } else if (item.equals("柴田千尋")) {
                mEmailEditText.setText("shibatachh@stf.teu.ac.jp");
            } else if (item.equals("喜田義弘")) {
                mEmailEditText.setText("kitayshr@stf.teu.ac.jp");
            } else if (item.equals("松岡丈平")) {
                mEmailEditText.setText("matsuokajh@stf.teu.ac.jp");
            } else if (item.equals("相田沙織")) {
                mEmailEditText.setText("aidasor@stf.teu.ac.jp");
            } else {
                mEmailEditText.setText("ここにメールアドレスが表示されます。");
            }

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

}
