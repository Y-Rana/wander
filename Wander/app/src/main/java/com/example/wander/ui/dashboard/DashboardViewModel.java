package com.example.wander.ui.dashboard;

import static androidx.lifecycle.LiveDataKt.observe;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.example.wander.model.*;
import com.mapbox.geojson.Point;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DashboardViewModel extends ViewModel {
    private final MutableLiveData<List<Post>> mPosts;
    private final MutableLiveData<Post> guessPost;
    private final MutableLiveData<String> mText;
    private final MutableLiveData<List<String>> mUserGroups;

    private final MutableLiveData<Post> mPost;

    private FirebaseStorage storage;

    private FirebaseFirestore db;

    public DashboardViewModel() {
        storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();
        mText = new MutableLiveData<>();
        mText.setValue("This is dashboard fragment");
        mPosts = new MutableLiveData<>();
        mPost = new MutableLiveData<>();
        guessPost = new MutableLiveData<>();
        mPosts.setValue(new ArrayList<>());
        mUserGroups = new MutableLiveData<>();
        mUserGroups.setValue(getUserGroups());
    }

    public LiveData<String> getText() {
        return mText;
    }

    public LiveData<List<Post>> getPosts() {
        return mPosts;
    }

    public LiveData<Post> getGuessPost() {
        return guessPost;
    }

    private void fetchPosts() {
        Log.d("FetchPosts", "groupNames " + mUserGroups.getValue().size());

        List<Post> posts = mPosts.getValue();

        mPost.observeForever(post -> {
            if (post != null) {
                Log.d("FetchPosts", "Post from " + post.getGroupName());
                posts.add(post);
            }

            mPosts.setValue(posts);
        });

        for (String name : mUserGroups.getValue()) {

            if (name != null) {
                Post.getPostFromGroup(name, mPost);
            }
        }



    }

    private List<String> getUserGroups() {
        List<String> groups = new ArrayList<>();

        //Access document in GroupMembership
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        //Log.d("GetUserGroups", user.getDisplayName());
        if (user == null) {
            return groups;
        }

        DocumentReference userGroups = db.collection("groupMembership").document(user.getDisplayName());

        userGroups.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot groupInfo = task.getResult();
                    for (int i = 1; i <= groupInfo.getLong("groupNum"); i++) {
                        groups.add(groupInfo.getString("group" + i));
                    }
                }
                mUserGroups.setValue(groups);
                Log.d("GetUserGroups", "groups " + groups.size());

                fetchPosts();
            }
        }).addOnFailureListener(e -> Log.e("GetUserGroups", e.getMessage()));
        //Add to list
        return groups;
    }

    public void setGuessPost(Post post) {
        Log.d("SetGuessPost", post.getImageURL().getPath());
        guessPost.setValue(post);
    }
}