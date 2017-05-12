package edu.umd.cs.realtime;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import edu.umd.cs.realtime.models.Post;

public class SearchActivity extends AppCompatActivity{
    private ListView searchLV;
    ArrayList<Post> searchResults;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Intent i = getIntent();
        searchResults  = i.getParcelableArrayListExtra("searchResults");

        initializeVariables();
    }


    private void initializeVariables() {
        searchLV = (ListView) findViewById(R.id.searchLv);
        searchLV.setAdapter(new SearchAdapter());
        searchLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Post post = searchResults.get(position);
                Intent i1 = new Intent(SearchActivity.this, PostActivity.class);
                i1.putExtra("selectedPost", post);
                startActivity(i1);
            }
        });
    }


    class SearchAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return searchResults.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Post p = searchResults.get(position);

            convertView = getLayoutInflater().inflate(R.layout.list_cell,null);
            TextView titleTV = (TextView) convertView.findViewById(R.id.titleTv1);
            TextView summaryTV = (TextView) convertView.findViewById(R.id.summaryTv);

            titleTV.setText(p.getTitle());
            summaryTV.setText(p.getSummary());
            return convertView;
        }
    }
}
