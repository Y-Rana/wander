package com.example.wander.ui.guess;

import android.location.Location;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.wander.model.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.mapbox.geojson.Point;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GuessFragmentViewModel extends ViewModel {
    private Point accLocation;
    private Point mGuessPoint;
    private String id;
    private Long mGuessScore;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

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
        long score = calculateScore();
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        //using email, as changing username would break the database model
        DocumentReference guess = db.collection("guesses").document(email + " " + id);

        Map<String, Object> guessHash = new HashMap<>();

        guessHash.put("points", score);

        guessHash.put("location", new GeoPoint(mGuessPoint.latitude(), mGuessPoint.longitude()));
        //guessHash.put("");

        guess.set(guessHash);

    }

    public void setGuessPoint(Point point) {
        mGuessPoint = point;
    }

    private long calculateScore() {
        //formula for calculation of score, set mGuessScore
        // 100/(x + 1)^2
        float distance = calculateDistance(mGuessPoint, accLocation) / 1000.0f;
        double score = 100.0 / ((0.3 * distance + 1) * (0.3 * distance + 1));

        //include code to display score on screen
        Log.d("CalculateScore", "Score is: " + mGuessScore);
        return mGuessScore = Math.round(score);
    }

    private float calculateDistance(Point one, Point two) {
        double startLat = one.latitude();
        double startLong = one.longitude();
        double endLat = two.latitude();
        double endLong = two.longitude();
        float[] result = new float[1];

        Location.distanceBetween(startLat, startLong, endLat, endLong, result);
        Log.d("CalculateDistance", "Distance (km)" + result[0] / 1000.0);

        return result[0];
    }

    public void setId(String id) {
        this.id = id;
    }
}

