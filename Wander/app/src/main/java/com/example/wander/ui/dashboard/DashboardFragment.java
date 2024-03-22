package com.example.wander.ui.dashboard;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.wander.R;
import com.example.wander.databinding.FragmentDashboardBinding;
import com.example.wander.model.Post;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(getActivity()).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textDashboard;
        final LinearLayout feed = binding.scrollLayout;

        dashboardViewModel.getPosts().observe(getViewLifecycleOwner(), posts -> {
                Log.d("Dashboard", "Change in posts, " + posts.size());
                feed.removeAllViews();
                for (Post post : posts) {
                    Log.d("Dashboard", "Post from " + post.getGroupName());
                    if (post.getImageURL() != null) {
                        PostLayout feedPost = new PostLayout(getContext(), post);
                        feedPost.setOnClickListener(click -> {
                            Navigation.findNavController(getActivity(), R.id.nav_host_fragment_activity_main).navigate(R.id.navigation_guess);
                            dashboardViewModel.setGuessPost(post);
                        });
                        feed.addView(feedPost);
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