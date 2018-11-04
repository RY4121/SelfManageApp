package jp.next.coby.rariru.selfmanageapp;

import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class FriendCheckAdapter extends BaseAdapter{
    private LayoutInflater mLayoutInFlater = null;
    private ArrayList<Group> mUserArrayList;

    private CheckBox[] checkBox = new CheckBox[1];
    private String user;
    private DatabaseReference mDatabaseReference,mFriendRef;


    public FriendCheckAdapter(Context context){
        mLayoutInFlater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount(){return mUserArrayList.size();}

    @Override
    public Object getItem(int position){return mUserArrayList.get(position);}

    @Override
    public long getItemId(int position){return position;}

    @Override
    public View getView(int position,View convertView,ViewGroup parent){

        if(convertView == null){
            convertView = mLayoutInFlater.inflate(R.layout.list_check_friends,parent,false);
        }

        String name = mUserArrayList.get(position).getName();
        String uid = mUserArrayList.get(position).getUid();
        String faculty = mUserArrayList.get(position).getTitle();

        TextView UserNameText = (TextView) convertView.findViewById(R.id.UserNameTextView);
        UserNameText.setText(name);

        TextView facultyText = (TextView) convertView.findViewById(R.id.facultyTextView);
        facultyText.setText(faculty);
        if(faculty.equals("CS")) {
            facultyText.setTextColor(Color.BLUE);
        }else if(faculty.equals("BS")){
            facultyText.setTextColor(Color.GREEN);
        }else if(faculty.equals("MS")){
            facultyText.setTextColor(Color.RED);
        }else if(faculty.equals("ES")){
            facultyText.setTextColor(Color.YELLOW);
        }

        TextView UIDText = (TextView) convertView.findViewById(R.id.UIDTextView);
        UIDText.setText(uid);


        //チェックボックス
        checkBox[0] = convertView.findViewById(R.id.checkbox_1);
        checkBox[0].setChecked(false);
        checkBox[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean check = checkBox[0].isChecked();
                if(check){
                    /*user = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    mDatabaseReference = FirebaseDatabase.getInstance().getReference();
                    mFriendRef = mDatabaseReference.child(Const.UsersPATH).child(user).child(Const.FriendsPATH);

                    mFriendRef.push().setValue(mUserArrayList);*/
                    //Snackbar.make(v,"クリックされました",Snackbar.LENGTH_LONG).show();
                }else{
                    //do nothing
                }
            }
        });

        return convertView;
    }

    public void setUserArrayList(ArrayList<Group> userArrayList){mUserArrayList = userArrayList;}
}

