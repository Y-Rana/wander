package com.example.wander.ui.dashboard;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.wander.databinding.FragmentDashboardBinding;
import com.example.wander.model.Post;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textDashboard;
        final RelativeLayout feed = binding.scrollLayout;

        dashboardViewModel.getPosts().observe(getViewLifecycleOwner(), posts -> {
                Log.d("Dashboard", "Change in posts, " + posts.size());
                for (Post post : posts) {
                    Log.d("Dashboard", "Post from " + post.getGroupName());
                    if (post.getImageURL() != null) {
                        feed.addView(new PostLayout(getContext(), post));
                    }
                }

        });
        return root;


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}