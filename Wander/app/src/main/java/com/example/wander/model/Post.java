package com.example.wander.model;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mapbox.geojson.Point;

import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Post {
    private Point location;
    private String groupName;
    private StorageReference imageURL;

    private static FirebaseStorage storage;
    private static FirebaseFirestore db;


    public Post() {
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    public Post(Point location, String groupName, StorageReference imageURL) {
        this.location = location;
        this.groupName = groupName;
        this.imageURL = imageURL;
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    public Point getLocation() {
        return location;
    }

    public void setLocation(Point location) {
        this.location = location;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public StorageReference getImageURL() {
        return imageURL;
    }

    public void setImageURL(StorageReference imageURL) {
        this.imageURL = imageURL;
    }

    //Make modification to the representation of groups, where the post name is also stored in a reference
    //in the groupData
    public static void getPostFromGroup(String groupName, MutableLiveData<Post> post) {
        db = FirebaseFirestore.getInstance();
        DocumentReference desiredGroupMembership = db.collection("groupData").document(groupName);
        desiredGroupMembership.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot result = task.getResult();
                DocumentReference postRef = (DocumentReference) result.get("postRef");

                if (postRef != null) {
                    Log.d("GetPostFromGroup", postRef.getPath());
                } else {
                    Log.d("GetPostFromGroup", "is null");
                }
                getPost(postRef, post);
            }
        }).addOnFailureListener(e -> Log.d("GetPostReference", e.getMessage()));
    }

    //Able to get the post as well as the image url, however this completes after Glide loads the
    //image, so need to make it so glide waits for the method to complete.
    private static void getPost(DocumentReference postRef, MutableLiveData<Post> post) {
        if (postRef == null) {
            return;
        }

        storage = FirebaseStorage.getInstance();

        postRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot result = task.getResult();

                GeoPoint location = (GeoPoint) result.get("location");

                if (location == null) {
                    post.setValue(null);
                    return;
                }

                Log.d("GetPost", location.toString());
                StorageReference ref = storage.getReferenceFromUrl((String) result.get("imagePath"));
                Log.d("GetPost", ref.toString());
                String groupName = (String) result.get("groupName");

                Post temp = new Post();
                temp.setLocation(Point.fromLngLat(location.getLatitude(), location.getLongitude()));
                temp.setGroupName(groupName);
                temp.setImageURL(ref);
                post.setValue(temp);
            }
        }).addOnFailureListener(e -> Log.d("GetPost", e.getMessage()));
    }


}
