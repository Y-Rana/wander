package com.example.wander;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wander.model.Group;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Groups extends AppCompatActivity {

    private TextView groups;
    private ImageView dashboardRedirectButton, searchButton;
    private Button createButton;
    private Button createSaveButton;

    private LinearLayout groupsContainer;
    private ScrollView groupsScrollView;

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);

        db = FirebaseFirestore.getInstance();

        dashboardRedirectButton = findViewById(R.id.arrow_to_dash);
        searchButton = findViewById(R.id.search);
        createButton = findViewById(R.id.create_group);

        groupsContainer = findViewById(R.id.groups_container);
        groupsScrollView = findViewById(R.id.groups_scrollView);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        DocumentReference userGroups = db.collection("groupMembership").document(user.getDisplayName());

        DocumentReference allGroups = db.collection("groupData").document("groups");

        userGroups.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot groupInfo = task.getResult();
                    for (int i = 1; i <= groupInfo.getLong("groupNum"); i++ ) {
                        addGroup(new Group(Integer.toString(i), groupInfo.getString("group" + Integer.toString(i)),groupInfo.getString("location"), Collections.singletonList("emptyAdmin")));
                        //addGroupToFirestore(new Group(Integer.toString(i), groupInfo.getString("group" + Integer.toString(i)), null));
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

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopUp();
            }
        });
    }

    private void showPopUp() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.create_group);

        EditText editName = dialog.findViewById(R.id.editName);
        EditText editLocation = dialog.findViewById(R.id.editLocation);
        dialog.findViewById(R.id.create_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addGroup(new Group("11", editName.getText().toString(), editLocation.getText().toString(), Collections.singletonList(user.getDisplayName())));
                addGroupToFirestore(new Group("thisismyid", editName.getText().toString(), editLocation.getText().toString(), Arrays.asList(user.getDisplayName())));
                dialog.cancel();
            }
        });
        dialog.show();
    }

    private void addGroup(Group group) {
        // Create a new TextView for the group
        TextView groupTextView = new TextView(this);
        groupTextView.setText(group.getGroupName());
        groupTextView.setTextSize(20);

        // Requires getGroupAdmins() to not be null
        for (int i = 0; i < group.getGroupAdmins().size(); i++) {
            if (group.getGroupAdmins().get(i).equals(user.getDisplayName())){
                groupTextView.setTextColor(Color.RED);
            }
        }
        groupsContainer.addView(groupTextView);

        // Scroll to the bottom to show the latest group
        groupsScrollView.post(new Runnable() {
            @Override
            public void run() {
                groupsScrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
    }

    private void addGroupToFirestore(Group group) {
        // Create a new group object
        Map<String, Object> groupHash = new HashMap<>();
        groupHash.put("id", group.getGroupId());
        groupHash.put("name", group.getGroupName());
        groupHash.put("location", group.getGroupLocation());

        // Add the group to Firestore
        db.collection("groupData")
                .add(groupHash)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        // DocumentSnapshot addedGroup = documentReference.get();
                        Toast.makeText(Groups.this, "Group created successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Groups.this, "Error creating group", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}