package edu.umd.cs.realtime;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import edu.umd.cs.realtime.models.Post;
import edu.umd.cs.realtime.models.User;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private DatabaseReference db;
    private FirebaseAuth auth;
    private ListView postsLV;
    private TextView name;
    private TextView accountType;

    final ArrayList<Post> posts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Firebase DB Setup
        db = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        initializeVariables();
        loadPosts();
    }


    public void initializeVariables() {
        postsLV = (ListView) findViewById(R.id.postsLv);

        String uid = auth.getCurrentUser().getUid();
        DatabaseReference userRef = db.child("users").child(uid);

        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                name = (TextView) findViewById(R.id.nameTv);
                accountType = (TextView) findViewById(R.id.accountTv);
                name.setText(user.getFirstName() + " " + user.getLastName());
                accountType.setText(user.getType().toUpperCase());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        userRef.addListenerForSingleValueEvent(userListener);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent i = new Intent(MainActivity.this, SearchActivity.class);
                i.putParcelableArrayListExtra("searchResults", findPosts(query));
                searchItem.collapseActionView();
                startActivity(i);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id ==  R.id.action_logout) {
            auth.signOut();
            startActivity(new Intent(MainActivity.this, LogInActivity.class));

        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadPosts(){
        DatabaseReference postsRef = db.child("posts");

        FirebaseListAdapter<Post> postsAdapter = new FirebaseListAdapter<Post>(this, Post.class, R.layout.list_cell, postsRef) {
            @Override
            public Post getItem(int position) {
                return super.getItem(getCount() - 1 - position);
            }

            @Override
            protected void populateView(View v, Post model, int position) {
                posts.add(model);
                Log.d("DATE_",model.toString());
                TextView titleTV = (TextView) v.findViewById(R.id.titleTv1);
                TextView summaryTV = (TextView) v.findViewById(R.id.summaryTv);

                titleTV.setText(model.getTitle());
                summaryTV.setText(model.getSummary());
            }

        };
        postsLV.setAdapter(postsAdapter);

        postsLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Post post = (Post) parent.getItemAtPosition(position);
                Intent i = new Intent(MainActivity.this, PostActivity.class);
                i.putExtra("selectedPost", post);
                startActivity(i);
            }
        });
    }

    /* The following is very bad practice for querying data from Firebase. I suggest using indexing
    on location, title and end time and location. Another option is using Firebase's cloud functions.
    The method basically searches the database if any of the strings match the following query.
    We will return every post that the string query matches in: location, title, category
    * */
    private ArrayList<Post> findPosts(String query){
        String formatted = query.toLowerCase();
        ArrayList<Post> searchResults = new ArrayList<>();

        for(Post p : posts) {
            boolean location = p.getLocation().toLowerCase().contains(formatted);

            if (location) {
                searchResults.add(p);
                continue;
            }

            boolean title = p.getTitle().toLowerCase().contains(formatted);

            if (title) {
                searchResults.add(p);
                continue;
            }

            boolean category = Post.getCategories()[p.getCategory()].contains(formatted);

            if (category) {
                searchResults.add(p);
                continue;
            }

            boolean start = p.getStart().toLowerCase().contains(formatted);

            if (start) {
                searchResults.add(p);
                continue;
            }

            boolean end = p.getEnd().toLowerCase().contains(formatted);

            if (end) {
                searchResults.add(p);
                continue;
            }



        }

        return searchResults;


    }
}
