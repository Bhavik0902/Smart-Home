package com.example.smarthome;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private String emailTxt, passwordTxt;
    private EditText email, password;
    private TextView signUp, forgotPassword;
    private AppCompatButton logIn;
    private ProgressBar progressBar;
    String msg;

    @Override
    protected void onStart() {
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null && mAuth.getCurrentUser().isEmailVerified()) {
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        mAuth = FirebaseAuth.getInstance();

        email = findViewById(R.id.emailLogIn);
        password = findViewById(R.id.passwordLogIn);
        signUp = findViewById(R.id.signUpView);
        forgotPassword = findViewById(R.id.forgotPassword);
        logIn = findViewById(R.id.login);
        progressBar = findViewById(R.id.progress);

        signUp.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this,SignUpActivity.class);
            startActivity(intent);
        });

        logIn.setOnClickListener(v -> {
            emailTxt = email.getText().toString().trim();
            passwordTxt = password.getText().toString().trim();

            if (emailTxt.isEmpty()) {
                email.setError("Email Required");
                email.requestFocus();
                return;
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString().trim()).matches()) {
                email.setError("Valid Email Required");
                email.requestFocus();
                return;
            } else if (passwordTxt.isEmpty()) {
                password.setError("Password Required");
                password.requestFocus();
                return;
            } else if (passwordTxt.length() < 6) {
                password.setError("Min 6 char required");
                password.requestFocus();
                return;
            }

            progressBar.setVisibility(View.VISIBLE);
            loginUser(emailTxt,passwordTxt,v);
        });

        forgotPassword.setOnClickListener(v -> sendPasswordResetEmail(v));

    }

    public void loginUser(String email, String password, View v) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful() && mAuth.getCurrentUser().isEmailVerified()) {
                        Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
                        startActivity(intent);
                        Snackbar.make(v, "Logged in successfully !", Snackbar.LENGTH_SHORT)
                                .setAction("Action", null)
                                .setBackgroundTint(ContextCompat.getColor(this,R.color.snackbar))
                                .setTextColor(ContextCompat.getColor(this,R.color.black))
                                .show();
                        finish();
                    }
                    else if(task.isSuccessful()) {
                        //Toast.makeText(this, "Email not Verified...You have been sent a verification mail", Toast.LENGTH_SHORT).show();
                        Snackbar.make(v, "Email not Verified...You have been sent a verification mail", Snackbar.LENGTH_SHORT)
                                .setAction("Action", null)
                                .setBackgroundTint(ContextCompat.getColor(this,R.color.snackbar))
                                .setTextColor(ContextCompat.getColor(this,R.color.black))
                                .show();
                        sendEmailVerificationLink();
                    }
                    else {
                        //Toast.makeText(this, "Invalid Email / Password", Toast.LENGTH_SHORT).show();
                        Snackbar.make(v, "Invalid Email / Password", Snackbar.LENGTH_SHORT)
                                .setAction("Action", null)
                                .setBackgroundTint(ContextCompat.getColor(this,R.color.snackbar))
                                .setTextColor(ContextCompat.getColor(this,R.color.black))
                                .show();
                    }

                    progressBar.setVisibility(View.GONE);
                });
    }

    public void sendPasswordResetEmail(View v) {
        if(!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString().trim()).matches()){
            email.setError("Please Enter Valid Email!!");
            email.requestFocus();
            return;
        }

        mAuth.sendPasswordResetEmail(email.getText().toString().trim())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        msg = "Reset link sent to your email !!!";
                    }else
                        msg = "Unable to send reset link...Please try again later!!";

                    Snackbar.make(v, msg, Snackbar.LENGTH_SHORT)
                            .setAction("Action", null)
                            .setBackgroundTint(ContextCompat.getColor(this,R.color.snackbar))
                            .setTextColor(ContextCompat.getColor(this,R.color.black))
                            .show();
                });
    }

    public void sendEmailVerificationLink() {
        mAuth.getCurrentUser().sendEmailVerification()
                .addOnCompleteListener(task -> {
                });
    }
}