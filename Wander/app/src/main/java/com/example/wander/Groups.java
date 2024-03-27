package com.example.wander;

import static android.view.View.GONE;
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wander.model.Group;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.AggregateQuery;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class Groups extends AppCompatActivity {

    private TextView groups;
    private ImageView searchButton;
    private Button createSaveButton;

    private LinearLayout groupsContainer;
    private ScrollView groupsScrollView;
    private SearchView searchView;

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);

        db = FirebaseFirestore.getInstance();

        ImageView dashboardRedirectButton = findViewById(R.id.arrow_to_dash);

        searchView = findViewById(R.id.searchView);
        searchView.setQueryHint("Search all groups");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                loadMatchingGroups(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                groupsContainer.removeAllViews();
                loadGroupCards();
                return false;
            }
        });

        Button createButton = findViewById(R.id.create_group);

        groupsContainer = findViewById(R.id.groups_container);
        groupsScrollView = findViewById(R.id.groups_scrollView);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        // Load groups that user is a member of
        db.collection("groupData")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        loadGroupCards();
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
                showCreatePopUp();
            }
        });
    }

    private void loadGroupCards() {
        groupsContainer.removeAllViews();
        // Load groups that user is a member of
        db.collection("groupData")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot group : task.getResult()) {
                                List<String> members = (List<String>) group.get("members");
                                for (int i = 0; i < members.size(); i++) {
                                    System.out.println("member: " + members.get(i));
                                    String formattedString = members.get(i).toString()
                                            .replace("[", "")
                                            .replace("]", "");
                                    if (formattedString.equals(user.getEmail())) {
                                        addGroup(new Group(group.getId(), group.get("name").toString(), group.get("location").toString(), (List<String>) group.get("admins"), (List<String>) group.get("members"), (boolean) group.get("requestToJoin")));
                                    }
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    private void showCreatePopUp() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.create_group);

        EditText editName = dialog.findViewById(R.id.editName);
        EditText editLocation = dialog.findViewById(R.id.editLocation);
        SwitchCompat requestToJoin = dialog.findViewById(R.id.rtj_switch);
        dialog.findViewById(R.id.create_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = createID();
                addGroup(new Group(id, editName.getText().toString(), editLocation.getText().toString(), Collections.singletonList(user.getEmail()), Collections.singletonList(user.getEmail()), requestToJoin.isChecked()));
                addGroupToFirestore(new Group(id, editName.getText().toString(), editLocation.getText().toString(), Collections.singletonList(user.getEmail()), Collections.singletonList(user.getEmail()), requestToJoin.isChecked()));
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
        View groupSettings = view.findViewById(R.id.group_card_settings);
        FrameLayout expand_content = view.findViewById(R.id.group_card_content);
        LinearLayout layout = view.findViewById(R.id.layout);
        TextView infoText = view.findViewById(R.id.info_text);

        layout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);

        nameView.setText(group.getGroupName());

        boolean admin = false;
        boolean member = false;

        // Determine if user is a member of group
        for (int i = 0; i < group.getMembers().size(); i++) {
            String formattedString = group.getMembers().get(i).toString()
                    .replace("[", "")
                    .replace("]", "");
            if (formattedString.equals(user.getEmail())) {
                member = true;
            }
        }

        // Determine if user is an admin of group
        for (int i = 0; i < group.getGroupAdmins().size(); i++) {
            String formattedString = group.getGroupAdmins().get(i).toString()
                    .replace("[", "")
                    .replace("]", "");
            if (formattedString.equals(user.getEmail())) {
                admin = true;
            }
        }

        if (member) {
            join.setText("Joined");
            join.setEnabled(false);
        } else {
            join.setText("Join");
        }

        if (admin) {
            groupSettings.setVisibility(View.VISIBLE);
        } else {
            groupSettings.setVisibility(GONE);
        }

        boolean finalMember = member;
        dropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TransitionManager.beginDelayedTransition(layout, new AutoTransition());

                if (expand_content.getVisibility() == GONE) {
                    expand_content.setVisibility(View.VISIBLE);
//                    if (finalMember) {
//                        join.setText("Leave");
//                        join.setBackgroundColor(0xFFFF3336);
//                        join.setEnabled(false);
//                    } else {
//                        join.setText("Join");
//                    }
                } else {
                    expand_content.setVisibility(GONE);
//                    if (finalMember) {
//                        join.setText("Joined");
//                        join.setEnabled(false);
//                    } else {
//                        join.setText("Join");
//                    }
                }

            }
        });

        // Join button on-click
        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("groupData").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot groupDB : task.getResult()) {
                                if(group.getId().equals(groupDB.getId())) {
                                    db.collection("groupData").document(groupDB.getId())
                                            .update("members", FieldValue.arrayUnion(user.getEmail().toString())).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    loadGroupCards();
                                                }
                                            });
                                }

                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
            }
        });

        // Settings button on-click
        groupSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSettingsPopUp(group);
            }
        });

        if (group.getRequestToJoin()) {
            infoText.setText("request to join = true");
        } else {
            infoText.setText("request to join = false");
        }

        infoText.setText(infoText.getText().toString() + "\n location = " + group.getGroupLocation());

        groupsContainer.addView(view);

        // Scroll to the bottom to show the latest group
        groupsScrollView.post(new Runnable() {
            @Override
            public void run() {
                groupsScrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
    }

    private void showSettingsPopUp(Group group) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.group_settings);

        EditText editName = dialog.findViewById(R.id.editNewName);
        EditText editRefresh = dialog.findViewById(R.id.editRefreshTime);
        EditText editPosters = dialog.findViewById(R.id.editPosters);
        Button deleteGroup = dialog.findViewById(R.id.deleteGroup);
        dialog.findViewById(R.id.settings_apply).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                db.collection("groupData")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot groupDB : task.getResult()) {
                                        if (groupDB.get("name").toString().equals(group.getGroupName())) {

                                            // Update name in database
                                            db.collection("groupData").document(groupDB.getId())
                                                    .update("name", editName.getText().toString())
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            loadGroupCards();
                                                        }
                                                    });

                                            // Check if new name is empty
                                            if (!editName.getText().toString().isEmpty()) {
                                                // Update local group object name
                                                group.setGroupName(editName.getText().toString());
                                            }
                                            break;
                                        }
                                    }
                                } else {
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                }
                            }
                        });

                dialog.cancel();
            }
        });

        dialog.findViewById(R.id.deleteGroup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("groupData")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot groupDB : task.getResult()) {
                                        if (groupDB.get("name").toString().equals(group.getGroupName())) {

                                            String id = groupDB.getId().replace("group ", "");

                                            // Delete group in database
                                            db.collection("groupData").document(groupDB.getId()).delete()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            // Delete associated post to the group in database
                                                            db.collection("posts").document("post " + id).delete()
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            // Load groups again
                                                                            loadGroupCards();
                                                                        }
                                                                    });
                                                        }
                                                    });
                                            break;
                                        }
                                    }
                                } else {
                                    Log.d(TAG, "Error deleting documents: ", task.getException());
                                }
                            }
                        });

                dialog.cancel();
            }
        });

        dialog.findViewById(R.id.closeSettings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.show();
    }

    private void addGroupToFirestore(Group group) {

        Query query = db.collection("groupData");
        AggregateQuery countQuery = query.count();

        countQuery.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                if (task.isSuccessful()) {

                    // Generate matching ID for post and group

                    Map<String, Object> postHash = new HashMap<>();
                    postHash.put("groupName", group.getGroupName());
                    postHash.put("imagePath", "");
                    postHash.put("location", group.getGroupLocation());
                    postHash.put("posterName", "postergoeshere");

                    // Add the post to Firestore
                    db.collection("posts").document(group.getId()).set(postHash);

                    Map<String, Object> groupHash = new HashMap<>();
                    groupHash.put("name", group.getGroupName());
                    groupHash.put("members", group.getMembers());
                    groupHash.put("admins", group.getGroupAdmins());
                    groupHash.put("location", group.getGroupLocation());
                    groupHash.put("requestToJoin", group.getRequestToJoin());
                    groupHash.put("postRef", db.collection("posts").document(group.getId()));

                    // Add the group to Firestore
                    db.collection("groupData").document(group.getId()).set(groupHash);
                } else {
                    Log.d(TAG, "Count failed: ", task.getException());
                }
            }
        });

    }

    // Load all groups that match search
    private void loadMatchingGroups(String newText) {

        groupsContainer.removeAllViews();

        db.collection("groupData")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot group : task.getResult()) {
                                if (matchesSearch(group.get("name").toString(), newText)) {
                                    addGroup(new Group(group.getId(), group.get("name").toString(), "eindhoven", Arrays.asList(group.get("admins").toString()), Arrays.asList(group.get("members").toString()), true));
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private boolean matchesSearch(String groupName, String s) {
        return groupName.toLowerCase().contains(s.toLowerCase());
    }

    public String createID() {
        return UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
    }


}