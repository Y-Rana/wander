package com.example.wander.ui.guess;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.wander.model.Post;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;

public class GuessFragmentViewModel extends ViewModel {
    private final MutableLiveData<Post> mPost;

    public GuessFragmentViewModel(Post post) {
        mPost = new MutableLiveData<>();
        mPost.setValue(post);
    }

    // Default constructor
    public GuessFragmentViewModel() {
        mPost = new MutableLiveData<>();
        // Initialize mPost with a default value, if needed
    }


    public LiveData<Post> getPost() {
        return mPost;
    }
}