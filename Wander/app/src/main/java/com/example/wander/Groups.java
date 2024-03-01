package com.example.wander;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class Groups extends AppCompatActivity {

    private TextView groups;
    private ImageView dashboardRedirectButton, searchButton;
    private FirebaseAuth auth;
    private FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        groups = findViewById(R.id.groups);
        dashboardRedirectButton = findViewById(R.id.arrow_to_dash);
        searchButton = findViewById(R.id.search);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        DocumentReference userGroups = db.collection("groupMembership").document(user.getDisplayName());

        userGroups.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot groupInfo = task.getResult();
                    for (int i = 1; i <= groupInfo.getDouble("groupNum"); i++ ) {
                        groups.append("\n"+groupInfo.getString("group" + Integer.toString(i)));
                    }
                }
            }
        });

        dashboardRedirectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Groups.this, MainActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }
}