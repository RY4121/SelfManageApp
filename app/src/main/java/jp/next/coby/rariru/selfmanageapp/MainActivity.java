package jp.next.coby.rariru.selfmanageapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;

import static java.lang.String.valueOf;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener/*, GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener*/ {

    //Gestureイベントの処理
    GestureDetector mGestureDetector;

    private Toolbar mToolbar;
    private int mGenre = 0;

    private DatabaseReference mDatabaseReference;
    private DatabaseReference mGenreRef;
    private ListView mListView;
    private ArrayList<Group> mQuestionArrayList;
    private TasksListAdapter mAdapter;
    FirebaseUser user;
    NavigationView navigationView;
    private FloatingActionButton fab;

    //個人タグ用の変数
    private SmallTaskAdapter mTaskAdapter;
    //パッケージ名を含めた文字列をキーとすることでユニークなものにしている
    public final static String EXTRA_TASK = "jp.next.coby.rariru.selfmanageapp.TASK";

    //Realmの設定
    private Realm mRealm;
    private RealmChangeListener mRealmListener = new RealmChangeListener() {
        @Override
        public void onChange(Object o) {
            reloadListView();
        }
    };

    private ChildEventListener mEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            HashMap map = (HashMap) dataSnapshot.getValue();
            String title = (String) map.get("title");
            String body = (String) map.get("body");
            String name = (String) map.get("name");
            String uid = (String) map.get("uid");
            String imageString = (String) map.get("image");
            byte[] bytes;
            if (imageString != null) {
                bytes = Base64.decode(imageString, Base64.DEFAULT);
            } else {
                bytes = new byte[0];
            }
            String date = (String) map.get("date");
            String time = (String) map.get("time");

            ArrayList<Answer> answerArrayList = new ArrayList<Answer>();
            HashMap answerMap = (HashMap) map.get("answers");
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

            Group question = new Group(title, body, name, uid, dataSnapshot.getKey(), mGenre, bytes, answerArrayList, date, time);
            mQuestionArrayList.add(question);
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            HashMap map = (HashMap) dataSnapshot.getValue();

            // 変更があったQuestionを探す
            for (Group question : mQuestionArrayList) {
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


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        //GestureDetectorインスタンス作成
        //mGestureDetector = new GestureDetector(this,this);

        //Realmの設定
        mRealm = Realm.getDefaultInstance();
        mRealm.addChangeListener(mRealmListener);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // ジャンルを選択していない場合（mGenre == 0）はエラーを表示するだけ
                if (mGenre == 0) {
                    Snackbar.make(view, "ジャンルを選択して下さい", Snackbar.LENGTH_LONG).show();
                    return;
                } else if (mGenre == 1) {
                    //個人用タグでの挙動
                    Intent intent = new Intent(getApplicationContext(), InputActivity.class);
                    intent.putExtra("genre", mGenre);
                    startActivity(intent);
                } else if (mGenre == 2) {
                    //共通タグでの挙動
                    // ログイン済みのユーザーを取得する
                    user = FirebaseAuth.getInstance().getCurrentUser();

                    if (user == null) {
                        // ログインしていなければログイン画面に遷移させる
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                    } else {
                        // ジャンルを渡して質問作成画面を起動する
                        Intent intent = new Intent(getApplicationContext(), TaskSendActivity.class);
                        intent.putExtra("genre", mGenre);
                        startActivity(intent);
                    }
                }
            }

        });


        // ナビゲーションドロワーの設定
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, mToolbar, R.string.app_name, R.string.app_name);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Firebase
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        // ListViewの準備
        mListView = (ListView) findViewById(R.id.listView);
        mAdapter = new TasksListAdapter(this);
        mQuestionArrayList = new ArrayList<Group>();
        mAdapter.notifyDataSetChanged();

        //リストビューが押された時の処理
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mGenre == 1) {
                    Task task = (Task) parent.getAdapter().getItem(position);

                    Intent intent = new Intent(getApplicationContext(), InputActivity.class);
                    intent.putExtra("genre", mGenre);
                    intent.putExtra(EXTRA_TASK, task.getId());
                    startActivity(intent);
                } else if (mGenre == 2) {
                    user = FirebaseAuth.getInstance().getCurrentUser();

                    if (user != null) {
                        // Questionのインスタンスを渡して質問詳細画面を起動する
                        Intent intent = new Intent(getApplicationContext(), TaskDetailActivity.class);
                        intent.putExtra("question", mQuestionArrayList.get(position));
                        startActivity(intent);
                    } else if (user == null) {
                        Snackbar.make(view, "ログインしてから選択してください", Snackbar.LENGTH_LONG)
                                .setAction("Login", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                        startActivity(intent);
                                    }
                                }).show();

                    }
                }
            }
        });

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (mGenre == 1) {
                    // タスクを削除する

                    final Task task = (Task) parent.getAdapter().getItem(position);

                    // ダイアログを表示する
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                    builder.setTitle("削除");
                    builder.setMessage(task.getTitle() + "を削除しますか");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            //idがtask.getId()と一致するTaskを取得
                            RealmResults<Task> results = mRealm.where(Task.class).equalTo("id", task.getId()).findAll();

                            mRealm.beginTransaction();
                            results.deleteAllFromRealm();
                            mRealm.commitTransaction();

                            Intent resultIntent = new Intent(getApplicationContext(), TaskAlarmReceiver.class);
                            PendingIntent resultPendingIntent = PendingIntent.getBroadcast(
                                    MainActivity.this,
                                    task.getId(),
                                    resultIntent,
                                    PendingIntent.FLAG_UPDATE_CURRENT
                            );

                            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                            alarmManager.cancel(resultPendingIntent);

                            reloadListView();
                        }
                    });
                    builder.setNegativeButton("CANCEL", null);

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }

                return true;
            }
        });

        //reloadListView();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_profiles) {
            // if(user != null) {
            Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
            startActivity(intent);
            /*}else if(user == null) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }*/
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        //共通のタグではない時FloatingActionButtonを押せないようにする
        if (id == R.id.nav_hobby) {
            mToolbar.setTitle("個人");
            mGenre = 1;
            //fab.setVisibility(View.INVISIBLE);

            mTaskAdapter = new SmallTaskAdapter(MainActivity.this);
            //addTaskForTest();
            reloadListView();
        } else if (id == R.id.nav_life) {
            mToolbar.setTitle("共通");
            mGenre = 2;
            fab.setVisibility(View.VISIBLE);

            // --- ここから ---
            // 質問のリストをクリアしてから再度Adapterにセットし、AdapterをListViewにセットし直す
            mQuestionArrayList.clear();
            mAdapter.setQuestionArrayList(mQuestionArrayList);
            mListView.setAdapter(mAdapter);

            // 選択したジャンルにリスナーを登録する
            if (mGenreRef != null) {
                mGenreRef.removeEventListener(mEventListener);
            }
            mGenreRef = mDatabaseReference.child(Const.ContentsPATH).child(String.valueOf(mGenre));
            mGenreRef.addChildEventListener(mEventListener);
        } else if (id == R.id.nav_favorite) {
            Intent intent = new Intent(getApplicationContext(), FavoriteActivity.class);
            //intent.putExtra("question", mQuestion);
            startActivity(intent);
            return true;
        }

        //ドロワーをしまう処理
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    //個人用タグリスト編集メソッド
    private void reloadListView() {
        // Realmデータベースから、「全てのデータを取得して新しい日時順に並べた結果」を取得
        RealmResults<Task> taskRealmResults = mRealm.where(Task.class).findAll().sort("date", Sort.DESCENDING);

        mTaskAdapter.setSmallTaskList(mRealm.copyFromRealm(taskRealmResults));
        mListView.setAdapter(mTaskAdapter);
        mTaskAdapter.notifyDataSetChanged();
    }


    @Override
    protected void onResume() {
        super.onResume();

        //ログインしていない場合はナビゲーションドロワーに”お気に入り”のタグを見えないかつ押せないようにする
        user = FirebaseAuth.getInstance().getCurrentUser();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();
        MenuItem menuItem = menu.findItem(R.id.nav_favorite);

        if (user == null) {//ログインしていないとき
            menuItem.setVisible(false);
        } else {
            //menuItem.setVisible(true);
            menuItem.setVisible(false);
        }

        //2018年9月1日公開用処理
        MenuItem menuItem1,menuItem2;
        menuItem1 = menu.findItem(R.id.nav_hobby);
        //menuItem1.setVisible(false);
        menuItem2 = menu.findItem(R.id.nav_life);
        menuItem2.setVisible(false);

        // 1:趣味を既定の選択とする
        if (mGenre == 0) {
            onNavigationItemSelected(navigationView.getMenu().getItem(0));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mRealm.close();
    }


    /*@Override
    public boolean onTouchEvent(MotionEvent event){
        if(mGestureDetector.onTouchEvent(event)){
            return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {//ダウン時に呼ばれる
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {//プレス時に呼ばれる
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {//長押し時に呼ばれる

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {//アップ時に呼ばれる
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {//スクロール時に呼ばれる
        return false;
    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float x, float y) {//フリック時に呼ばれる
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        Log.v("Test", "onDoubleTapEvent()");
        return false;
    }
    @Override
    public boolean onDoubleTap(MotionEvent e) {
        Log.v("Test", "onDoubleTap()");
        return false;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        Log.v("Test", "onSingleTapConfirmed()");
        return false;
    }*/
}
