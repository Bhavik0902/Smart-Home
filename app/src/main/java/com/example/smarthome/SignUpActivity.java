package com.example.smarthome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText email, password, confirmPassword;
    private String emailTxt, passwordTxt, confirmPasswordTxt;
    private Button create;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        mAuth = FirebaseAuth.getInstance();

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.conPass);
        create = findViewById(R.id.signUpAccButton);

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailTxt = email.getText().toString().trim();
                passwordTxt = password.getText().toString().trim();
                confirmPasswordTxt = confirmPassword.getText().toString().trim();

                if (emailTxt.isEmpty()) {
                    email.setError("Email Required");
                    email.requestFocus();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(emailTxt).matches()) {
                    email.setError("Valid Email Required");
                    email.requestFocus();
                    return;
                }
                if (passwordTxt.isEmpty()) {
                    password.setError("Password Required");
                    password.requestFocus();
                    return;
                }
                if (passwordTxt.length() < 6) {
                    password.setError("Min 6 char required");
                    password.requestFocus();
                    return;
                }
                if (confirmPasswordTxt.isEmpty() || !confirmPasswordTxt.equals(passwordTxt)) {
                    confirmPassword.setError("Password does not matches !!");
                    confirmPassword.requestFocus();
                    return;
                }

                createAccount(emailTxt, passwordTxt);
            }
        });
    }

    public void createAccount(String email, String password) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(SignUpActivity.this, "User with this email already exist.", Toast.LENGTH_SHORT).show();
                            }
                            else
                                Toast.makeText(SignUpActivity.this, "Error creating the account...Try again Later!!!", Toast.LENGTH_SHORT).show();
                        } else {
                            sendEmailVerificationLink();
                            Toast.makeText(SignUpActivity.this, "Account created successfully !!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                            finish();
                        }
                    }
                });
    }

    public void sendEmailVerificationLink() {
        mAuth.getCurrentUser().sendEmailVerification()
                .addOnCompleteListener(task -> {
                });
    }
}