package com.example.wander;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.Manifest;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;
import com.example.wander.databinding.ActivityCameraBinding;
import com.example.wander.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Firebase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.UUID;

public class CameraActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_CODE = 1;
    private static final int MULT_KEY = 1;

    ActivityCameraBinding cameraBinding;
    ActivityResultLauncher<Uri> takePictureLauncher;
    Uri imageUri;
    private TextView dashboardRedirectText;

    private FirebaseStorage storage;
    private StorageReference storageReference;

    GPSTracker gps;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        String[] PERMISSIONS;

        super.onCreate(savedInstanceState);
        cameraBinding = ActivityCameraBinding.inflate(getLayoutInflater());
        setContentView(cameraBinding.getRoot());

        imageUri = createURI();
        setTakePictureLauncher();

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        dashboardRedirectText = findViewById(R.id.dashboardRedirect);



        PERMISSIONS = new String[] {
                Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_FINE_LOCATION
        };

        cameraBinding.cameraButton.setOnClickListener(view -> {
            if (!hasPermissions(CameraActivity.this, PERMISSIONS)) {
                ActivityCompat.requestPermissions(CameraActivity.this, PERMISSIONS, MULT_KEY);
            } else {
                takePictureLauncher.launch(imageUri);
            }
        });

        dashboardRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CameraActivity.this, MainActivity.class));
            }
        });

        cameraBinding.uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadPicture(imageUri);
            }
        });

    }


    private boolean hasPermissions(Context context, String... PERMISSIONS) {

        if (context != null && PERMISSIONS != null) {
            for (String permission : PERMISSIONS) {

                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private Uri createURI() {
        File imageFile = new File(getApplicationContext().getFilesDir(), "camera_photo.jpg");
        return FileProvider.getUriForFile(
                getApplicationContext(),
                "com.example.wander.fileProvider",
                imageFile
        );
    }

    private void setTakePictureLauncher() {
        takePictureLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                new ActivityResultCallback<Boolean>() {
                    @Override
                    public void onActivityResult(Boolean result) {
                        gps = new GPSTracker(CameraActivity.this);
                        try {
                            if (result) {
                                cameraBinding.CameraUser.setImageURI(null);
                                cameraBinding.CameraUser.setImageURI(imageUri);
                                if (gps.canGetLocation) {
                                    double latitude = gps.getLatitude();
                                    double longitude = gps.getLongitude();

                                    // \n is for new line
                                    Log.d("Latitude", "my Latitude" + latitude);
                                    Log.d("Longitude", "my Longitude" + longitude);
                                    Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
                                }
                            }
                        } catch (Exception exception) {
                            exception.getStackTrace();
                        }
                    }
                }
        );
    }

    private void uploadPicture(Uri image) {
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("image/jpg")
                .build();
        StorageReference reference = storageReference.child("Posts/" + UUID.randomUUID().toString());


        reference.putFile(image, metadata).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(CameraActivity.this, "Image posted", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CameraActivity.this, "There was an error when uploading", Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MULT_KEY) {
            if ((grantResults[0] == PackageManager.PERMISSION_GRANTED) && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                takePictureLauncher.launch(imageUri);
            } else {
                Toast.makeText(this, "Permissions denied, please allow permissions to take picture", Toast.LENGTH_SHORT).show();
            }
        }

    }


}

