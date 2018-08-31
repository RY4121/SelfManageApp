package jp.next.coby.rariru.selfmanageapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import static android.widget.Toast.makeText;

public class ProfileActivity extends AppCompatActivity {

    DatabaseReference mDataBaseReference;
    private String user;
    private TextView mUIDTextView,mEmailTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //UI部品の取得とユーザーIDを取得してTextViewに反映させる
        setTitle("プロフィール画面");
        mUIDTextView = (TextView) findViewById(R.id.UIDTextView);
        mEmailTextView = (TextView)findViewById(R.id.EmailTextView);

        try {
            user = FirebaseAuth.getInstance().getCurrentUser().getUid();
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
            mEmailTextView.setText(Euser.getEmail());
        } else {
            //do nothing
        }

    }
}
