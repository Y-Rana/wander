package com.example.wander;

import static androidx.navigation.fragment.FragmentKt.findNavController;

import android.os.Bundle;

import com.example.wander.ui.dashboard.DashboardFragment;
import com.example.wander.ui.guess.PosterActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.wander.databinding.ActivityMainBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private FirebaseAuth auth;
    private ImageView profileIcon, groupsIcon;
    private Boolean DEBUG_POSTER_VIEW = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();

        if (this.auth.getCurrentUser() == null) {
            Intent login_page = new Intent(this, LogIn.class);
            startActivity(login_page);
        } else {
            Log.d("Main OnCreate", "Current user is: " + auth.getCurrentUser().getEmail());
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //connecting to the database
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (DEBUG_POSTER_VIEW || checkIfPoster()){
            Intent poster_page = new Intent(this, PosterActivity.class);
            startActivity(poster_page);
        }

        profileIcon = findViewById(R.id.profile_icon);
        groupsIcon = findViewById(R.id.groups_icon);


//        BottomNavigationView navView = findViewById(R.id.nav_view);
//        // Passing each menu ID as a set of Ids because each
//        // menu should be considered as top level destinations.
//        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
//                .build();
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
//        NavigationUI.setupWithNavController(binding.navView, navController);


        profileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Profile.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        groupsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Groups.class));
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
    }

    private Boolean checkIfPoster(){
        return false;
    }

}

