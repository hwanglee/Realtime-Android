package edu.umd.cs.realtime;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LogInActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText emailET;
    private EditText passwordET;
    private Button loginButton;
    private TextView registerTV;
    private ProgressDialog progressDialog;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        initializeVariables();
    }

    private void initializeVariables() {
        emailET = (EditText) findViewById(R.id.emailEt);
        passwordET = (EditText) findViewById(R.id.passwordEt);
        loginButton = (Button) findViewById(R.id.logInBtn);
        registerTV =  (TextView) findViewById(R.id.registerTv);

        progressDialog = new ProgressDialog(this);

        auth = FirebaseAuth.getInstance();

        loginButton.setOnClickListener(this);
        registerTV.setOnClickListener(this);
    }

    /***
     * @return
     * The method attempts to login the user based on the provided input.
     */
    private void login() {
        String email = emailET.getText().toString().trim();
        String password = passwordET.getText().toString().trim();
        progressDialog.setMessage("Logging In...");
        progressDialog.show();

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()) {
                    startActivity(new Intent(LogInActivity.this, MainActivity.class));
                } else {
                    Toast.makeText(LogInActivity.this, "Incorrect Email and/or Password.", Toast.LENGTH_SHORT).show();
                }

                progressDialog.hide();
            }
        });


    }

    @Override
    public void onClick(View v) {

        if(v == loginButton) {
            login();
        }

        if (v == registerTV) {
            startActivity(new Intent(this, RegisterActivity.class));
        }

    }
}
