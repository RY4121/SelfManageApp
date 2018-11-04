package jp.next.coby.rariru.selfmanageapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.StringPrepParseException;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import jp.next.coby.rariru.selfmanageapp.Const;

public class LoginActivity extends AppCompatActivity {

    EditText mEmailEditText;
    EditText mPasswordEditText;
    EditText mNameEditText;
    EditText mFacultyEditText;
    ProgressDialog mProgress;

    FirebaseAuth mAuth;
    OnCompleteListener<AuthResult> mCreateAccountListener;
    OnCompleteListener<AuthResult> mLoginListener;
    DatabaseReference mDataBaseReference;

    //Spinner用の処理
    Spinner mSpinner;
    String SpiText;

    // アカウント作成時にフラグを立て、ログイン処理後に名前をFirebaseに保存する
    boolean mIsCreateAccount = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //BundleクラスにonSavedInstanceStateメソッドが定義されていて、
        //savedInstanceStateに破棄時のインスタンス状態が保存されている
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mDataBaseReference = FirebaseDatabase.getInstance().getReference();

        // FirebaseAuthのオブジェクトを取得する
        mAuth = FirebaseAuth.getInstance();

        // アカウント作成処理のリスナー
        mCreateAccountListener = new OnCompleteListener<AuthResult>() {
            @Override
            //ジェネリクス<>を用いている：
            public void onComplete(Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // 成功した場合
                    // ログインを行う
                    String email = mEmailEditText.getText().toString();
                    String password = mPasswordEditText.getText().toString();

                    //loginメソッドの中でmLoginListenerのaddOnCompleteListenerの
                    //処理が書かれているため、このメソッドより後に処理を書いても、
                    //ここではその処理は実行されない。
                    login(email, password);
                } else {
                    // 失敗した場合
                    // エラーを表示する
                    View view = findViewById(android.R.id.content);
                    Snackbar.make(view, "アカウント作成に失敗しました", Snackbar.LENGTH_LONG).show();

                    // プログレスダイアログを非表示にする
                    mProgress.dismiss();
                }
            }
        };

        // ログイン処理のリスナー
        mLoginListener = new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    // 成功した場合
                    FirebaseUser user = mAuth.getCurrentUser();
                    DatabaseReference userRef = mDataBaseReference.child(Const.UsersPATH).child(user.getUid());

                    if (mIsCreateAccount) {
                        // アカウント作成の時は表示名をFirebaseに保存する
                        String name = mNameEditText.getText().toString();
                        String faculty = SpiText;

                        String uid = user.getUid();

                        String email = user.getEmail();
                        String password = mPasswordEditText.getText().toString();

                        Map<String, String> data = new HashMap<String, String>();
                        data.put("name", name);
                        data.put("faculty",faculty);
                        data.put("uid",uid);

                        data.put("email", email);
                        data.put("password",password);


                        user.sendEmailVerification();
                        userRef.setValue(data);

                        // 表示名をPrefarenceに保存する
                        saveName(name);
                    } else {
                        //ログインボタンを押したときの処理
                        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                Map data = (Map) snapshot.getValue();
                                try{
                                    saveName((String)data.get("name"));
                                }catch (Exception e){
                                    //ログインした際、表示名が取得できなかったときの処理（仮定）
                                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                    startActivity(intent);
                                }

                            }
                            @Override
                            public void onCancelled(DatabaseError firebaseError) {
                            }
                        });
                    }

                    // プログレスダイアログを非表示にする
                    mProgress.dismiss();

                    // Activityを閉じる
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(intent);
                    //finish();

                } else {
                    // 失敗した場合
                    // エラーを表示する
                    View view = findViewById(android.R.id.content);
                    Snackbar.make(view, "ログインに失敗しました", Snackbar.LENGTH_LONG).show();

                    // プログレスダイアログを非表示にする
                    mProgress.dismiss();
                }
            }
        };

        // UIの準備
        setTitle("ログイン");

        mEmailEditText = (EditText) findViewById(R.id.emailText);
        mPasswordEditText = (EditText) findViewById(R.id.passwordText);
        mNameEditText = (EditText) findViewById(R.id.nameText);

        mSpinner = (Spinner)findViewById(R.id.spinner);
        mSpinner.setOnItemSelectedListener(mCategoryClickListener);

        mProgress = new ProgressDialog(this);
        mProgress.setMessage("処理中...");

        Button createButton = (Button) findViewById(R.id.createButton);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // キーボードが出てたら閉じる
                InputMethodManager im = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                im.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                String email = mEmailEditText.getText().toString();
                String password = mPasswordEditText.getText().toString();
                String name = mNameEditText.getText().toString();

                if (email.length() != 0 && password.length() >= 6 && name.length() != 0) {
                    // ログイン時に表示名を保存するようにフラグを立てる
                    mIsCreateAccount = true;

                    createAccount(email, password);
                } else {
                    // エラーを表示する
                    Snackbar.make(v, "正しく入力してください(パスワードは六文字以上です)", Snackbar.LENGTH_LONG).show();
                }
            }
        });

        Button loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // キーボードが出てたら閉じる
                InputMethodManager im = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                im.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                String email = mEmailEditText.getText().toString();
                String password = mPasswordEditText.getText().toString();

                if (email.length() != 0 && password.length() >= 6) {
                    // フラグを落としておく
                    mIsCreateAccount = false;

                    login(email, password);
                } else {
                    // エラーを表示する
                    Snackbar.make(v, "正しく入力してください", Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    private AdapterView.OnItemSelectedListener mCategoryClickListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Spinner spinner = (Spinner) parent;
            //選択されたアイテムを取得
            String item = (String) spinner.getSelectedItem();
            SpiText = item;
            Toast.makeText(getApplicationContext(), item, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    private void createAccount(String email, String password) {
        // プログレスダイアログを表示する
        mProgress.show();

        // アカウントを作成する
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(mCreateAccountListener);
    }

    private void login(String email, String password) {
        // プログレスダイアログを表示する
        mProgress.show();

        // ログインする
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(mLoginListener);
    }

    private void saveName(String name) {
        // Preferenceに保存する
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(Const.NameKEY, name);
        editor.commit();
    }
}