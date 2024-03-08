package com.example.wander;

import android.gesture.Gesture;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.example.wander.databinding.FragmentGuessBinding;
import com.google.type.LatLng;
import com.mapbox.geojson.Point;
import com.mapbox.maps.MapView;
import com.mapbox.maps.MapboxMap;
import com.mapbox.maps.plugin.gestures.GesturesPlugin;
import com.mapbox.maps.plugin.gestures.GesturesUtils;
import com.mapbox.maps.plugin.gestures.OnMapClickListener;
import com.mapbox.maps.viewannotation.ViewAnnotationManager;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GuessFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GuessFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String IMAGE_PATH = null;

    private ViewAnnotationManager vam;
    private ImageView dropPin;

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
        RelativeLayout mapLayout = (RelativeLayout) view.findViewById(R.id.map_layout);
        MapView mapView = (MapView) view.findViewById(R.id.mapView);
        RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.guess_layout);
        dropPin = (ImageView) view.findViewById(R.id.drop_pin);

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

        GesturesPlugin gesturesPlugin = GesturesUtils.getGestures(mapView);
        MapboxMap mapboxMap = mapView.getMapboxMap();

        float factor = view.getContext().getResources().getDisplayMetrics().density;

        gesturesPlugin.addOnMapClickListener(new MapClickListener(mapLayout, dropPin, mapboxMap, factor));

        Glide.with(this).load("https://cms.globema.pl/glbmedia/9-2016-08-23-11112312124124124124.jpg").into(imageView);

        return view;
    }


}

class MapClickListener implements OnMapClickListener {
    private final RelativeLayout mapLayout;
    private final ImageView dropPin;

    private final MapboxMap mapboxMap;

    private final int PIN_SIZE = 20;
    private final float factor;

    public MapClickListener(RelativeLayout mapLayout, ImageView dropPin, MapboxMap mapboxMap, float factor) {
        this.mapLayout = mapLayout;
        this.dropPin = dropPin;
        this.factor = factor;
        this.mapboxMap = mapboxMap;
    }

    @Override
    public boolean onMapClick(@NonNull Point point) {
        int x = (int) mapboxMap.pixelForCoordinate(point).getX();
        int y = (int) mapboxMap.pixelForCoordinate(point).getY();
        Log.d("MapLayout", point.toString());
        mapLayout.removeView(dropPin);
        RelativeLayout.LayoutParams layout = new RelativeLayout.LayoutParams((int) (PIN_SIZE * factor), (int) (PIN_SIZE * factor));
        layout.setMargins(x - (int) (0.5 * PIN_SIZE), y - (int) (PIN_SIZE), -1, -1);

        dropPin.setLayoutParams(layout);
        mapLayout.addView(dropPin);
        return true;
    }
}