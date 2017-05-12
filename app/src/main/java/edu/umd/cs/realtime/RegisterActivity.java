package edu.umd.cs.realtime;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import edu.umd.cs.realtime.models.User;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText firstNameET;
    private EditText lastNameET;
    private EditText addressET;
    private EditText cellPhoneET;
    private EditText emailET;
    private EditText passwordET;
    private CheckBox publisherCb;
    private ProgressDialog progressDialog;
    private Button registerButton;
    private FirebaseAuth auth;
    private DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initializeVariables();

    }

    private void initializeVariables() {
        firstNameET = (EditText) findViewById(R.id.firstNameEt);
        lastNameET = (EditText) findViewById(R.id.lastNameEt);
        addressET = (EditText) findViewById(R.id.addressEt);
        cellPhoneET = (EditText) findViewById(R.id.cellPhoneEt);
        emailET = (EditText) findViewById(R.id.emailEt1);
        passwordET = (EditText) findViewById(R.id.passwordEt1);
        publisherCb = (CheckBox) findViewById(R.id.publihserCb);
        registerButton = (Button) findViewById(R.id.registerBtn);

        progressDialog = new ProgressDialog(this);

        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference();

        registerButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        if(v == registerButton) {
            register();
        }

    }

    private void register() {
        final String firstName = firstNameET.getText().toString().trim();
        final String lastName = lastNameET.getText().toString().trim();
        final String address = addressET.getText().toString().trim();
        final String cellPhone = cellPhoneET.getText().toString().trim();
        final String email = emailET.getText().toString().trim();
        final String type = publisherCb.isChecked()? "publisher" : "reader";
        String password = passwordET.getText().toString().trim();



        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    DatabaseReference usersRef = db.child("users");
                    String uid = auth.getCurrentUser().getUid();

                    Task result = usersRef.child(uid).setValue(new User(firstName,lastName,cellPhone,address,email, type));

                    result.addOnCompleteListener(RegisterActivity.this, new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {

                            if (!task.isSuccessful()) {
                                Toast.makeText(RegisterActivity.this, "Failed to add user to the database.", Toast.LENGTH_SHORT);
                            } else {
                                sendVerificationEmail();
                                startActivity(new Intent(RegisterActivity.this,LogInActivity.class));
                            }
                        }
                    });


                } else {
                    Toast.makeText(RegisterActivity.this, "Failed to create new user.", Toast.LENGTH_SHORT);
                }
            }
        });
    }

    private void sendVerificationEmail(){
        FirebaseUser user = auth.getCurrentUser();

        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d("EMAIL", "Email sent.");
                } else {
                    Log.d("EMAIL", "Email failed.");
                }
            }
        });
    }


}

