package com.example.wander.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    private final MutableLiveData<Integer> xCoord;
    private final MutableLiveData<Integer> yCoord;
    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
        xCoord = new MutableLiveData<>();
        yCoord = new MutableLiveData<>();
    }

    public LiveData<String> getText() {
        return mText;
    }
}