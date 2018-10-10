package jp.next.coby.rariru.selfmanageapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PrivateTaskAdapter extends BaseAdapter{

    private LayoutInflater mLayoutInflater;
    private List<Task> mTaskList;

    public PrivateTaskAdapter(Context context){
        mLayoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setSmallTaskList(List<Task> taskList){
        mTaskList = taskList;
    }

    @Override
    public int getCount(){
        return mTaskList.size();
    }

    @Override
    public Object getItem(int position){
        return mTaskList.get(position);
    }

    @Override
    public long getItemId(int position){
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        if(convertView == null){
            convertView = mLayoutInflater.inflate(R.layout.list_smallquestions,null);
        }

        //UI部品の取得
        TextView mTitleTextView = (TextView) convertView.findViewById(R.id.titleTextView);
        TextView mCategoryTextView = (TextView) convertView.findViewById(R.id.categoryTextView);
        TextView mDateTimeTextView = (TextView) convertView.findViewById(R.id.DateTimeTextView);

        // 後でTaskクラスから情報を取得するように変更する
        mTitleTextView.setText(mTaskList.get(position).getTitle());
        mCategoryTextView.setText(mTaskList.get(position).getCategory());

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.JAPANESE);
        Date date = mTaskList.get(position).getDate();
        mDateTimeTextView.setText(simpleDateFormat.format(date));

        return convertView;
    }

}
