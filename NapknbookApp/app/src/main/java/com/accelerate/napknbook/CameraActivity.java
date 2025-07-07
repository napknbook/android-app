package com.accelerate.napknbook;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.accelerate.napknbook.api.NapknbookService;
import com.accelerate.napknbook.api.RetrofitClientInstance;
import com.accelerate.napknbook.utils.SharedPreferencesHelper;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.common.util.concurrent.ListenableFuture;

import org.json.JSONArray;
import org.json.JSONObject;
import android.Manifest;


import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CameraActivity extends AppCompatActivity {
    private PreviewView cameraPreview;
    private ImageButton btnCapture;
    private Button btnConfirmSend;
    private ImageView closePreviewImageView;
    private FrameLayout previewOverlay;
    private ImageView imagePreview;
    private ProcessCameraProvider cameraProvider;
    private Camera camera;
    private ImageCapture imageCapture;
    private ExecutorService cameraExecutor;
    private Button cancelTasksGenerationButton ;
    private Button confirmTasksGenerationButton ;
    private ConstraintLayout confirmTasksGenerationConstraintLayout ;
    private ConstraintLayout loadingConstraintLayout;

    private String characterPk;
    private String authToken;

    private File latestPhotoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.black));
        }

        cameraPreview = findViewById(R.id.cameraPreview);
        btnCapture = findViewById(R.id.btnCapture);
        btnConfirmSend = findViewById(R.id.btnConfirmSend);
        imagePreview = findViewById(R.id.imagePreview);
        previewOverlay = findViewById(R.id.previewOverlay);

        closePreviewImageView = findViewById(R.id.closeButton);

        cameraExecutor = Executors.newSingleThreadExecutor();

        SharedPreferencesHelper prefs = SharedPreferencesHelper.getInstance(this);
        authToken = prefs.getAuthToken();
        characterPk = prefs.getMainCharacterPk();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 100);
        } else {
            startCamera();
        }

        btnCapture.setOnClickListener(v -> takePicture());

        closePreviewImageView.setOnClickListener(v -> {
            previewOverlay.setVisibility(View.GONE);
            startCamera();
        });

        confirmTasksGenerationConstraintLayout = findViewById(R.id.confirmTasksGenerationConstraintLayout);
        loadingConstraintLayout = findViewById(R.id.loadingSpinnerConstraintLayout);


        cancelTasksGenerationButton = findViewById(R.id.cancelTasksGenerationButton);
        cancelTasksGenerationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmTasksGenerationConstraintLayout.setVisibility(View.GONE);
            }
        });

        ImageView goldImageView = findViewById(R.id.goldImageView);

        Glide.with(getApplicationContext())
                .load(R.drawable.gold)
                .transform(new CircleCrop())
                .into(goldImageView);

        btnConfirmSend.setOnClickListener(v -> {

            confirmTasksGenerationConstraintLayout.setVisibility(View.VISIBLE);

        });

        confirmTasksGenerationButton = findViewById(R.id.confirmTasksGenerationButton);
        confirmTasksGenerationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (latestPhotoFile != null) {
                    uploadImage(latestPhotoFile);
                    confirmTasksGenerationConstraintLayout.setVisibility(View.GONE);
                    loadingConstraintLayout.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderFuture.get();
                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build();

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(cameraPreview.getSurfaceProvider());

                imageCapture = new ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                        .build();

                cameraProvider.unbindAll();
                camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);
            } catch (Exception e) {
                Log.e("CameraX", "Use case binding failed", e);
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void takePicture() {
        File photoFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "photo.jpg");
        ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(photoFile).build();

        imageCapture.takePicture(outputFileOptions, cameraExecutor, new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                latestPhotoFile = photoFile;

                runOnUiThread(() -> {
                    previewOverlay.setVisibility(View.VISIBLE);
                    try {
                        Bitmap bitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                        Bitmap rotatedBitmap = rotateBitmapIfRequired(bitmap, photoFile.getAbsolutePath());
                        imagePreview.setImageBitmap(rotatedBitmap);
                    } catch (IOException e) {
                        Toast.makeText(CameraActivity.this, "Failed to load preview", Toast.LENGTH_SHORT).show();
                    }

                    cameraProvider.unbindAll(); // Pause live feed while previewing
                });
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                Log.e("CameraX", "Photo capture failed: " + exception.getMessage(), exception);
                runOnUiThread(() -> Toast.makeText(CameraActivity.this, "Photo capture failed", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void uploadImage(File file) {
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), file);
        MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
        RequestBody characterPart = RequestBody.create(MediaType.parse("text/plain"), characterPk);

        NapknbookService service = RetrofitClientInstance.getRetrofitInstance().create(NapknbookService.class);
        Call<ResponseBody> call = service.uploadImage("Bearer " + authToken, characterPart, imagePart);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String responseString = response.body().string();
                        JSONObject json = new JSONObject(responseString);
                        JSONArray tasksArray = json.getJSONArray("tasks");

                        runOnUiThread(() -> {
                            Toast.makeText(CameraActivity.this, "Tasks created from image!", Toast.LENGTH_SHORT).show();
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("tasksCreated", true);
                            setResult(RESULT_OK, resultIntent);
                            finish();
                        });

                    } catch (Exception e) {
                        Log.e("CameraActivity", "Error parsing AI response", e);
                        Toast.makeText(CameraActivity.this, "Error reading AI response", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CameraActivity.this, "Upload failed: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(CameraActivity.this, "Upload error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Bitmap rotateBitmapIfRequired(Bitmap bitmap, String path) throws IOException {
        ExifInterface exif = new ExifInterface(path);
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.postRotate(90); break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.postRotate(180); break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.postRotate(270); break;
            default:
                return bitmap;
        }

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
    }
}

