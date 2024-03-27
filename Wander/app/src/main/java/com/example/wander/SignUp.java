package com.example.wander;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {
    private FirebaseAuth auth;
    private EditText signupEmail, signupPassword, signupUsername;
    private Button signupButton;
    private TextView loginRedirectText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        auth = FirebaseAuth.getInstance();
        signupEmail = findViewById(R.id.signup_email);
        signupUsername = findViewById(R.id.signup_username);
        signupPassword = findViewById(R.id.signup_password);
        signupPassword.setTransformationMethod(new PasswordTransformationMethod());
        signupButton = findViewById(R.id.signup_button);
        loginRedirectText = findViewById(R.id.loginRedirect);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("CreateUser", "Creating user");
                String email = signupEmail.getText().toString().trim();
                String pass = signupPassword.getText().toString().trim();
                String username = signupUsername.getText().toString().trim();
                if (email.isEmpty()){
                    signupEmail.setError("Email cannot be empty");
                }
                if (username.isEmpty() || username.length() < 3 || username.length() > 15) {
                    signupUsername.setError("Username must be between 3 and 15 characters");
                }
                if (pass.isEmpty()){
                    signupPassword.setError("Password cannot be empty");
                } else {
                    checkUsernameUniqueAndCreateAccount(email, username, pass);
                }
            }
        });
        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUp.this, LogIn.class));
            }
        });
    }

    private void checkUsernameUniqueAndCreateAccount(String email, String username, String pass) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Log.d("checkUsername", "checking username," + username);

        DocumentReference membershipRef = db.collection("usernames").document(username);

        membershipRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        Log.d("checkUsername", "username not unique, " + doc.getData().toString());
                        signupUsername.setError("Username must be unique.");
                    } else {
                        Log.d("checkUsername", "username unique");
                        CreateAccountAndSetUsername(email, username, pass);
                    }
                }
            }
        });
    }
    private void CreateAccountAndSetUsername(String email, String username, String pass) {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .build();

        auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = auth.getCurrentUser();

                    user.updateProfile(profileUpdates)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("setUsername", "Username set");
                                        createGroupMembershipAndUsername(username, email);
                                        Toast.makeText(SignUp.this, "Signup Successful", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(SignUp.this, LogIn.class));
                                        //redirect out of signup at this point
                                    }
                                }
                            });

                } else {
                    Toast.makeText(SignUp.this, "Signup Failed" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void createGroupMembershipAndUsername(String username, String email) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference groupMembershipDoc = db.collection("groupMembership").document(email);
        DocumentReference usernameDoc = db.collection("usernames").document(username);


        Map<String, Integer> groupHash = new HashMap<>();
        groupHash.put("groupNum", 0);
        groupMembershipDoc.set(groupHash);

        Map<String, String> usernameHash = new HashMap<>();
        usernameHash.put("email", email);

    }
}