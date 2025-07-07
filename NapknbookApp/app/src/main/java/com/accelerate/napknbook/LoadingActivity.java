package com.accelerate.napknbook;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.accelerate.napknbook.database.repositories.TaskCategoryRepository;
import com.accelerate.napknbook.database.repositories.TaskRepository;
import com.accelerate.napknbook.database.repositories.UserRepository;
import com.accelerate.napknbook.utils.SharedPreferencesHelper;
import com.accelerate.napknbook.viewmodels.TaskCategoryViewModel;
import com.accelerate.napknbook.viewmodels.TaskCategoryViewModelFactory;
import com.accelerate.napknbook.viewmodels.TaskViewModel;
import com.accelerate.napknbook.viewmodels.TaskViewModelFactory;

import java.util.Timer;
import java.util.TimerTask;


public class LoadingActivity extends AppCompatActivity {

    private ProgressBar pb;
    private TextView loadingTextView;
    private int counter = 0;

    private SharedPreferencesHelper sharedPreferencesHelper;
    private String authToken;
    private String userPk;
    private String firstStartup ;

    private TaskCategoryViewModel categoryViewModel;
    private TaskViewModel taskViewModel ;


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
        firstStartup = sharedPreferencesHelper.getFirstStartup();



        startProgressBar();
    }

    private void startProgressBar() {

        long period = 100 ;

        if (firstStartup.equals("1")) {

            period = 25 ;
        }
        else if (firstStartup.equals("0")){
            period = 2 ;
        }

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
                    sharedPreferencesHelper.saveFirstStartup("0");
                }
            }
        };
        timer.schedule(tt, 0, period);
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
