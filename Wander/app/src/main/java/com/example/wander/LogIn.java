package com.example.wander;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.content.Intent;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class LogIn extends AppCompatActivity {
    private EditText loginEmail, loginPassword;
    private TextView signupRedirectText;
    private Button loginButton;
    private FirebaseAuth auth;
    private MainActivity dashboard;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        loginEmail = findViewById(R.id.login_email);
        loginPassword = findViewById(R.id.login_password);
        loginPassword.setTransformationMethod(new PasswordTransformationMethod());
        loginButton = findViewById(R.id.login_button);
        signupRedirectText = findViewById(R.id.signupRedirect);
        auth = FirebaseAuth.getInstance();
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = loginEmail.getText().toString();
                String pass = loginPassword.getText().toString();
                if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    if (!pass.isEmpty()) {
                        auth.signInWithEmailAndPassword(email, pass)
                                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {
                                        Toast.makeText(LogIn.this, "Logged in!", Toast.LENGTH_SHORT).show();

                                        startActivity(new Intent(LogIn.this, MainActivity.class));
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(LogIn.this, "Login failed!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        loginPassword.setError("Please enter a password");
                    }
                } else if (email.isEmpty()) {
                    loginEmail.setError("Please enter an email");
                } else {
                    loginEmail.setError("Please enter a correct email");
                }
            }
        });
        signupRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LogIn.this, SignUp.class));
            }
        });

    }
}