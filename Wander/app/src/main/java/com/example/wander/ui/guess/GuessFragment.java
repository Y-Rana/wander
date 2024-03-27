package com.example.wander.ui.guess;

import static android.view.View.VISIBLE;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.example.wander.R;
import com.example.wander.databinding.FragmentGuessBinding;
import com.example.wander.model.Post;
import com.example.wander.ui.dashboard.DashboardFragment;
import com.example.wander.ui.dashboard.DashboardViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import com.mapbox.android.gestures.MoveGestureDetector;
import com.mapbox.geojson.Point;
import com.mapbox.maps.MapView;
import com.mapbox.maps.MapboxMap;
import com.mapbox.maps.plugin.gestures.GesturesPlugin;
import com.mapbox.maps.plugin.gestures.GesturesUtils;
import com.mapbox.maps.plugin.gestures.OnMapClickListener;
import com.mapbox.maps.plugin.gestures.OnMoveListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GuessFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GuessFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private ImageView dropPin;

    private DashboardViewModel mViewModel;
    private GuessFragmentViewModel mGuessModel;

    public GuessFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param post The Post object.
     * @return A new instance of fragment GuessFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GuessFragment newInstance(Post post) {
        GuessFragment fragment = new GuessFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(requireActivity()).get(DashboardViewModel.class);
        mGuessModel = new ViewModelProvider(this).get(GuessFragmentViewModel.class);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_guess, container, false);
        ImageView imageView = (ImageView) view.findViewById(R.id.post_picture);
        RelativeLayout mapLayout = (RelativeLayout) view.findViewById(R.id.map_layout);
        MapView mapView = (MapView) view.findViewById(R.id.mapView);
        RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.guess_layout);
        dropPin = (ImageView) view.findViewById(R.id.drop_pin);
        Button guessButton = (Button) view.findViewById(R.id.guess_button);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewGroup.LayoutParams swap = imageView.getLayoutParams();
                imageView.setLayoutParams(mapLayout.getLayoutParams());

                mapLayout.setLayoutParams(swap);

                mapView.setLayoutParams(swap);

                layout.removeView(mapLayout);
                layout.removeView(imageView);

                layout.addView(mapLayout);
                layout.addView(imageView);

            }
        });

        guessButton.setOnClickListener(click -> mGuessModel.handleSubmit(mViewModel.getGuessPost().getValue().getGroupName()));

        GesturesPlugin gesturesPlugin = GesturesUtils.getGestures(mapView);
        MapboxMap mapboxMap = mapView.getMapboxMap();

        float factor = view.getContext().getResources().getDisplayMetrics().density;

        gesturesPlugin.addOnMapClickListener(new MapClickListener(mapLayout, dropPin, mapboxMap, factor, mGuessModel));

        gesturesPlugin.addOnMoveListener(new OnMoveListener() {
            @Override
            public void onMoveBegin(@NonNull MoveGestureDetector moveGestureDetector) {
                dropPin.setVisibility(View.INVISIBLE);
            }

            @Override
            public boolean onMove(@NonNull MoveGestureDetector moveGestureDetector) {
                return false;
            }

            @Override
            public void onMoveEnd(@NonNull MoveGestureDetector moveGestureDetector) {

            }
        });

        mViewModel.getGuessPost().observe(getViewLifecycleOwner(), gpost -> {
            Log.d("GuessFragment", "change");
            if (gpost != null) {
                Log.d("GuessFragment", gpost.getImageURL().getPath());
                mGuessModel.setAccLocation(gpost.getLocation());
                Glide.with(imageView).load(gpost.getImageURL()).into(imageView);
            }
        });

        return view;
    }
}




class MapClickListener implements OnMapClickListener {
    private final RelativeLayout mapLayout;
    private final ImageView dropPin;

    private final MapboxMap mapboxMap;

    private final int PIN_SIZE = 30;
    private final float factor;

    private final GuessFragmentViewModel guessModel;

    public MapClickListener(RelativeLayout mapLayout, ImageView dropPin, MapboxMap mapboxMap, float factor,
                            GuessFragmentViewModel guessModel) {
        this.mapLayout = mapLayout;
        this.dropPin = dropPin;
        this.factor = factor;
        this.mapboxMap = mapboxMap;
        this.guessModel = guessModel;
    }

    @Override
    public boolean onMapClick(@NonNull Point point) {
        int x = (int) mapboxMap.pixelForCoordinate(point).getX();
        int y = (int) mapboxMap.pixelForCoordinate(point).getY();
        Log.d("MapLayout", point.toString());

        guessModel.setGuessPoint(point);

        mapLayout.removeView(dropPin);
        RelativeLayout.LayoutParams layout = new RelativeLayout.LayoutParams((int) (PIN_SIZE * factor), (int) (PIN_SIZE * factor));
        layout.setMargins(x - (int) (0.5 * PIN_SIZE), y - (int) (PIN_SIZE), -1, -1);

        dropPin.setLayoutParams(layout);
        mapLayout.addView(dropPin);
        dropPin.setVisibility(VISIBLE);
        return true;
    }
}


