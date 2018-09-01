package jp.next.coby.rariru.selfmanageapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.Snackbar;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static java.lang.String.valueOf;

public class TaskDetailListAdapter extends BaseAdapter {
    private final static int TYPE_QUESTION = 0;
    private final static int TYPE_ANSWER = 1;

    private LayoutInflater mLayoutInflater = null;
    private Group mQustion;

    private DatabaseReference mDatabaseReference, mDeleteRef;
    private String user;

    private Context mContext;

    public TaskDetailListAdapter(Context context, Group question) {
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mQustion = question;
        mContext = context;
    }

    @Override
    public int getCount() {
        return 1 + mQustion.getAnswers().size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_QUESTION;
        } else {
            return TYPE_ANSWER;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public Object getItem(int position) {
        return mQustion;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (getItemViewType(position) == TYPE_QUESTION) {
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.list_question_detail, parent, false);
            }
            String body = mQustion.getBody();
            String name = mQustion.getName();

            TextView bodyTextView = (TextView) convertView.findViewById(R.id.bodyTextView);
            bodyTextView.setText(body);

            TextView nameTextView = (TextView) convertView.findViewById(R.id.nameTextView);
            nameTextView.setText(name);

            byte[] bytes = mQustion.getImageBytes();
            if (bytes.length != 0) {
                Bitmap image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length).copy(Bitmap.Config.ARGB_8888, true);
                ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView);
                imageView.setImageBitmap(image);
            }

            //追加した処理
            mDatabaseReference = FirebaseDatabase.getInstance().getReference();

            String date = mQustion.getDate();
            String time = mQustion.getTime();

            String uid = mQustion.getUid();

            TextView DateTextView = (TextView) convertView.findViewById(R.id.DateTextView);
            DateTextView.setText(date);
            TextView TimeTextView = (TextView) convertView.findViewById(R.id.TimeTextView);
            TimeTextView.setText(time);

            TextView UIDTextView = (TextView) convertView.findViewById(R.id.UIDTextView);
            UIDTextView.setText(uid);
            TextView mUIDTextView = (TextView) convertView.findViewById(R.id.mUIDTextView);
            //mUIDTextView.setText("タスク作成者のUID");


            Button button = (Button) convertView.findViewById(R.id.DeleteButton);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Snackbar.make(v, "タスクは削除されました", Snackbar.LENGTH_LONG).show();
                    //タスク一覧からの削除
                    mDeleteRef = mDatabaseReference.child(Const.ContentsPATH).child(valueOf(mQustion.getGenre())).child(mQustion.getQuestionUid());
                    mDeleteRef.removeValue();
                    //お気に入りからの削除
                    user = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    mDeleteRef = mDatabaseReference.child(Const.FavoritePATH).child(user).child(mQustion.getQuestionUid());
                    mDeleteRef.removeValue();
                }
            });
        } else {
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.list_answer, parent, false);
            }

            Answer answer = mQustion.getAnswers().get(position - 1);
            String body = answer.getBody();
            String name = answer.getName();

            try {
                user = FirebaseAuth.getInstance().getCurrentUser().getUid();
            } catch (NullPointerException e) {
                /*View view = ((MainActivity)mContext).findViewById(R.id.drawer_layout);
                Snackbar
                        .make(view, "ログインしないと表示できません", Snackbar.LENGTH_INDEFINITE)
                        .show();
                Intent intent = new Intent(mContext,SettingActivity.class);
                startActivity(intent);

                Toast.makeText(mContext, "ログインしないと表示できません", Toast.LENGTH_LONG).show();*/
            }

            //ログインしていてかつタスク詳細画面に入った場合の処理
            //if(user != null){
                TextView bodyTextView = (TextView) convertView.findViewById(R.id.bodyTextView);
                bodyTextView.setText(body);

                TextView nameTextView = (TextView) convertView.findViewById(R.id.nameTextView);
                nameTextView.setText(user);
            //}
        }

        return convertView;
    }

}
