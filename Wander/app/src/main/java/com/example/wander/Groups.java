package com.example.wander;

import static android.view.View.GONE;
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.LayoutTransition;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Groups extends AppCompatActivity {

    private TextView groups;
    private ImageView searchButton;
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

        ImageView dashboardRedirectButton = findViewById(R.id.arrow_to_dash);
        searchButton = findViewById(R.id.search);
        Button createButton = findViewById(R.id.create_group);

        groupsContainer = findViewById(R.id.groups_container);
        groupsScrollView = findViewById(R.id.groups_scrollView);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        DocumentReference userGroups = db.collection("groupMembership").document(user.getUid());

        DocumentReference allGroups = db.collection("groupData").document("groups");

        // Load groups that user is a member of
        db.collection("groupTest")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot group : task.getResult()) {
                                String members = group.get("members").toString();
                                String userName = user.getDisplayName();
                                if (members.contains(userName)) {
                                    addGroup(new Group(group.get("id").toString(), group.get("name").toString(), "eindhoven", Arrays.asList(group.get("members").toString()), Arrays.asList(group.get("members").toString())));
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
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
                addGroup(new Group("11", editName.getText().toString(), "eindhoven", Collections.singletonList(user.getDisplayName()), Collections.singletonList(user.getDisplayName())));
                addGroupToFirestore(new Group("thisismyid", editName.getText().toString(), "eindhoven", Collections.singletonList(user.getDisplayName()), Collections.singletonList(user.getDisplayName())));
                dialog.cancel();
            }
        });
        dialog.show();
    }

    private void addGroup(Group group) {

        final View view = getLayoutInflater().inflate(R.layout.group_card, null);

        TextView nameView = view.findViewById(R.id.group_name);
        Button join = view.findViewById(R.id.join);
        Button dropdown = view.findViewById(R.id.info);
        FrameLayout expand_content = view.findViewById(R.id.group_card_content);
        LinearLayout layout = view.findViewById(R.id.layout);

        layout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);

        nameView.setText(group.getGroupName());

        boolean admin = false;
        boolean member = false;

        // Determine if user is admin or member of group
        for (int i = 0; i < group.getGroupAdmins().size(); i++) {
            if (group.getGroupAdmins().get(i).contains(user.getDisplayName())) {
                admin = true;
            }
            if (group.getMembers().get(i).contains(user.getDisplayName())) {
                member = true;
            }
        }

        if (member) {
            join.setText("Joined");
        } else {
            join.setText("Join");
        }

        dropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TransitionManager.beginDelayedTransition(layout, new AutoTransition());

                if (expand_content.getVisibility() == GONE) {
                    expand_content.setVisibility(View.VISIBLE);
                } else {
                    expand_content.setVisibility(GONE);
                }

            }
        });

        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        groupsContainer.addView(view);

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
        groupHash.put("members", group.getMembers().toString());

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