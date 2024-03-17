package com.example.wander.ui.guess;

import android.location.Location;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.wander.model.Post;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.mapbox.geojson.Point;

import java.util.List;

public class GuessFragmentViewModel extends ViewModel {
    private Point accLocation;
    private Point mGuessPoint;
    private Long mGuessScore;

    public GuessFragmentViewModel() {
        mGuessPoint = Point.fromLngLat(0.0,0.0);
    }

    public Point getGuessPoint() {
        return mGuessPoint;
    }

    public void setAccLocation(Point point) {
        accLocation = point;
    }

    public Long getGuessScore() {
        return mGuessScore;
    }

    public void handleSubmit() {
        //code to create and upload a guess to firebase
        calculateScore();
    }

    public void setGuessPoint(Point point) {
        mGuessPoint = point;
    }

    private void calculateScore() {
        //formula for calculation of score, set mGuessScore
        calculateDistance(mGuessPoint, accLocation);
    }

    private float calculateDistance(Point one, Point two) {
        double startLat = mGuessPoint.latitude();
        double startLong = mGuessPoint.longitude();
        double endLat = accLocation.latitude();
        double endLong = accLocation.longitude();
        float[] result = new float[1];

        Location.distanceBetween(startLat, startLong, endLat, endLong, result);
        Log.d("CalculateDistance", "Distance (km)" + result[0] / 1000.0);
        return result[0];
    }
}

