package com.accelerate.napknbook;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.accelerate.napknbook.database.repositories.UserRepository;
import com.accelerate.napknbook.utils.SharedPreferencesHelper;

import java.util.Timer;
import java.util.TimerTask;


public class LoadingActivity extends AppCompatActivity {

    private ProgressBar pb;
    private TextView loadingTextView;
    private int counter = 0;

    private SharedPreferencesHelper sharedPreferencesHelper;
    private String authToken;
    private String userPk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        // Setup appearance
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.black));
        }

        // Initialize views
        pb = findViewById(R.id.progressBar);
        loadingTextView = findViewById(R.id.loadingTextView);
        loadingTextView.setText("Loading 0%...");

        // Load tokens
        sharedPreferencesHelper = SharedPreferencesHelper.getInstance(this);
        authToken = sharedPreferencesHelper.getAuthToken();
        userPk = sharedPreferencesHelper.getUserPk();

        startProgressBar();
    }

    private void startProgressBar() {
        Timer timer = new Timer();
        TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                counter++;
                pb.setProgress(counter);

                runOnUiThread(() -> loadingTextView.setText("Loading " + counter + "%..."));

                if (counter >= 100) {
                    timer.cancel();
                    proceedToNextScreen();
                }
            }
        };
        timer.schedule(tt, 0, 25);
    }

    private void proceedToNextScreen() {
        runOnUiThread(() -> {
            if (authToken == null || userPk == null) {
                // Go to login flow
                Intent intent = new Intent(LoadingActivity.this, isUserActivity.class);
                startActivity(intent);
            } else {
                // Go to main app
                Intent intent = new Intent(LoadingActivity.this, MainActivity.class);
                startActivity(intent);
            }
            finish();
        });
    }
}
