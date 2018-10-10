package jp.next.coby.rariru.selfmanageapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class CategoryActivity extends AppCompatActivity {

    private ListView mCategoryListView;
    private ArrayList<String> mCategoryArrayList;
    private String[] cate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        cate = new String[]{"趣味", "生活", "健康", "仕事", "学校", "勉強", "娯楽"};

        //データを準備
        mCategoryArrayList = new ArrayList<>();
        for (int i = 0; i < cate.length; i++) {
            mCategoryArrayList.add(cate[i]);
        }

        //Adapterの設定
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                mCategoryArrayList
        );

        //ListViewにArrayAdapterを設定する
        mCategoryListView = (ListView) findViewById(R.id.categorylistView);
        //ListViewへのadapterセットは必須
        mCategoryListView.setAdapter(adapter);

        mCategoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("categoryName",mCategoryArrayList.get(position));
                startActivity(intent);
            }
        });
    }
}
