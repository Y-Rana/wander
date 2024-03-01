package com.example.wander;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class Profile extends AppCompatActivity {
    private EditText newUname;
    private TextView resetPass, uName, dashboardRedirectText;
    private Button applyButton, logoutButton;
    private FirebaseAuth auth;
    private FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        uName = findViewById(R.id.userName);
        newUname = findViewById(R.id.newUname);
        resetPass = findViewById(R.id.changePass);
        dashboardRedirectText = findViewById(R.id.dashboardRedirect);
        applyButton = findViewById(R.id.apply_button);
        logoutButton = findViewById(R.id.logout_button);

        auth = FirebaseAuth.getInstance();

        if (auth != null) {
            user = auth.getCurrentUser();
            uName.setText(user.getDisplayName());
        }

        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newUnameString = newUname.getText().toString();
                if (newUnameString != null && newUnameString != "") {
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(newUnameString)
                            .build();
                    user.updateProfile(profileUpdates)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(Profile.this, "Username change successful", Toast.LENGTH_SHORT).show();
                                        uName.setText(user.getDisplayName());
                                    }
                                }
                            });
                }
            }
        });

        resetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.sendPasswordResetEmail(user.getEmail())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(Profile.this, "Password reset email sent", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(Profile.this, MainActivity.class));
            }
        });

        dashboardRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { startActivity(new Intent(Profile.this, MainActivity.class)); }
        });
    }
}