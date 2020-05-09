package com.androdocs.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;

import android.app.SearchManager;
import android.content.AsyncQueryHandler;
import android.content.Intent;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.ref.WeakReference;

public class SearchableActivity extends ListActivity {

    private MyHandler mHandler;

    private TextView txt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);
        txt = (TextView)findViewById(R.id.textView);

        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //txt.setText("Searching By by: "+ query);
            mHandler = new MyHandler(this);
            mHandler.startQuery(0, null, intent.getData(), null, null, null, null);
        }
        else if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            mHandler = new MyHandler(this);
            mHandler.startQuery(0, null, intent.getData(), null, null, null, null);
        }
    }

    public void updateText(String text){
        txt.setText(text);
    }

    static class MyHandler extends AsyncQueryHandler {
        // avoid memory leak
        WeakReference<SearchableActivity> activity;

        public MyHandler(SearchableActivity searchableActivity) {
            super(searchableActivity.getContentResolver());
            activity = new WeakReference<>(searchableActivity);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            super.onQueryComplete(token, cookie, cursor);
            if (cursor == null || cursor.getCount() == 0) return;

            cursor.moveToFirst();

            long id = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
            String text = cursor.getString(cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1));
            long dataId =  cursor.getLong(cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID));

            cursor.close();

            if (activity.get() != null) {
                activity.get().updateText("onQueryComplete: " + id + " / " + text + " / " + dataId);
            }
        }
    };
}
