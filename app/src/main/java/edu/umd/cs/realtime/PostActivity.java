package edu.umd.cs.realtime;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import edu.umd.cs.realtime.models.Post;

public class PostActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener,View.OnClickListener {
    private DatabaseReference db;
    private FirebaseAuth auth;

    private TextView titleTV, locationTV, categoryTV, endTimeTV, contentTV;
    private Button deleteButton;
    private CheckBox favoriteButton;
    private Post post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        //Include all variables to set their valureaes.
        initializeVariables();
        //Add post data to variables and handle the visibility of the delete button.
        loadPostData();

    }

    private void initializeVariables() {
        titleTV = (TextView) findViewById(R.id.titleTv2);
        locationTV = (TextView) findViewById(R.id.locationTv);
        categoryTV = (TextView) findViewById(R.id.categoryTv);
        endTimeTV = (TextView) findViewById(R.id.endTimeTv);
        contentTV = (TextView) findViewById(R.id.contentTv);
        deleteButton = (Button) findViewById(R.id.deleteBtn);
        favoriteButton = (CheckBox) findViewById(R.id.favBtn);

        db = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        contentTV.setMovementMethod(new ScrollingMovementMethod());
        deleteButton.setOnClickListener(this);

    }

    private boolean isOwner(String pid) {
        return auth.getCurrentUser().getUid().equals(pid);
    }

    private void loadPostData() {
        Intent i = getIntent();
        post = i.getExtras().getParcelable("selectedPost");
        String[] categories = Post.getCategories();

        //Remove the delete button if current user is not the owner.
        if (!isOwner(post.getPid())) {
            deleteButton.setVisibility(View.GONE);
        }

        titleTV.setText(post.getTitle());
        locationTV.setText(post.getLocation());
        categoryTV.setText(categories[post.getCategory()]);
        endTimeTV.setText(post.getEnd());

        contentTV.setText(Html.fromHtml(post.getContent()));

        setFavoriteButton();
        favoriteButton.setOnCheckedChangeListener(this);
    }



    private void setFavoriteButton() {
        String uid = auth.getCurrentUser().getUid();
        DatabaseReference favRef = db.child("favorites").child(uid).child(post.getId());

        ValueEventListener favsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Boolean res = (Boolean) dataSnapshot.getValue();

                if (res != null) {
                    favoriteButton.setChecked(true);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        favRef.addListenerForSingleValueEvent(favsListener);
    }

    private void favorite() {
        String uid = auth.getCurrentUser().getUid();
        DatabaseReference favRef = db.child("favorites").child(uid).child(post.getId());
        favRef.setValue(true);
    }

    private void unfavorite() {
        String uid = auth.getCurrentUser().getUid();
        DatabaseReference favRef = db.child("favorites").child(uid).child(post.getId());
        favRef.removeValue();
    }



    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            unfavorite();
        } else {
            favorite();
        }
    }


    private void delete() {
        DatabaseReference postRef = db.child("posts").child(post.getId());
        postRef.removeValue();
    }


    @Override
    public void onClick(View v) {
        if (v == deleteButton) {
            Log.d("DEL", "Clicked");
            delete();
            startActivity(new Intent(PostActivity.this, MainActivity.class));
        }
    }
}
