package com.example.wander;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.example.wander.databinding.FragmentGuessBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GuessFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GuessFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String IMAGE_PATH = null;

    private FragmentGuessBinding binding;

    //Possibly coordinates of the current location of the user
//    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mImagePath;
    //private String mParam2;

    public GuessFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param mImagePath The image path for the post picture.
     * @return A new instance of fragment GuessFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GuessFragment newInstance(String mImagePath) {
        GuessFragment fragment = new GuessFragment();
        Bundle args = new Bundle();
        args.putString(IMAGE_PATH, mImagePath);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mImagePath = getArguments().getString(IMAGE_PATH);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_guess, container, false);
        ImageView imageView = (ImageView) view.findViewById(R.id.post_picture);

        Glide.with(this).load("https://cms.globema.pl/glbmedia/9-2016-08-23-11112312124124124124.jpg").into(imageView);

        return view;
    }
}