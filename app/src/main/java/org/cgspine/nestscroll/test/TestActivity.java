package org.cgspine.nestscroll.test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.cgspine.nestscroll.R;

import java.util.ArrayList;
import java.util.List;

public class TestActivity extends AppCompatActivity {

    ListView mListView;
    TestLayout mTestLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        mListView = (ListView) findViewById(R.id.listView);
        mTestLayout = (TestLayout) findViewById(R.id.testLayout);
        List<String> list = new ArrayList<>();
        for(int i = 0;i<100;i++){
            list.add(""+i);
        }
        ArrayAdapter arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,list);
        mTestLayout.setArrayAdapter(arrayAdapter);
        mListView.setAdapter(arrayAdapter);
    }
}
