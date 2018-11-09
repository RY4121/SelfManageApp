package jp.next.coby.rariru.selfmanageapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import static java.lang.String.valueOf;

public class AnswerDetailListAdapter extends BaseAdapter {

    private LayoutInflater mLayoutInflater = null;
    private Group mQuestion;

    private ArrayList<Answer> mAnswerDetailArrayList;

    public AnswerDetailListAdapter(Context context, Group question) {
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mQuestion = question;
    }

    @Override
    public int getCount() {
        return mAnswerDetailArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return mAnswerDetailArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = mLayoutInflater.inflate(R.layout.list_answer_detail,parent,false);
        }

        String name = mAnswerDetailArrayList.get(position).getName();
        String uid = mAnswerDetailArrayList.get(position).getUid();
        String title = mAnswerDetailArrayList.get(position).getTitle();
        String body = mAnswerDetailArrayList.get(position).getBody();

        TextView nameText = convertView.findViewById(R.id.nameTextView);
        nameText.setText("表示名："+name);
        TextView uidText = convertView.findViewById(R.id.uidTextView);
        uidText.setText("UID："+uid);
        TextView bodyText = convertView.findViewById(R.id.bodyTextView);
        bodyText.setText(body);
        TextView titleText = convertView.findViewById(R.id.titleTextView);
        titleText.setText(title);


        return convertView;
    }

    public void setAnswerDetailArrayList(ArrayList<Answer> answerArrayList) {
        mAnswerDetailArrayList = answerArrayList;
    }

}
