package com.accelerate.napknbook;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.accelerate.napknbook.database.AppDatabase;
import com.android.billingclient.api.ProductDetails;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.accelerate.napknbook.utils.SharedPreferencesHelper;

import java.util.concurrent.Executors;

public class SettingsActivity extends AppCompatActivity {


    private String authToken ;

    TextView logoutTextView, loginTextView, cancelSubTextView;
    TextView profileTextView, privacyTextView, securityTextView;
    TextView helpTextView, contactTextView, reportTextView;
    TextView aboutTextView, termsTextView, privacyPolicyTextView;
    SharedPreferencesHelper sharedPreferencesHelper;
    ImageView earlyAdopterBadgeImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setNavigationBarColor(ContextCompat.getColor(this, R.color.black));
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        toolbar.getNavigationIcon().setTint(Color.WHITE);

        sharedPreferencesHelper = SharedPreferencesHelper.getInstance(this);
        authToken = sharedPreferencesHelper.getAuthToken();

        // ✅ Initialize views first
        initViews();

        // ✅ Then set up buttons
        setupButtons();

        // Badge (optional)
        Glide.with(getApplicationContext())
                .load(R.raw.early_adopter_badge)
                .transform(new CircleCrop())
                .into(earlyAdopterBadgeImageView);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Handle the back arrow click here
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initViews() {
        logoutTextView = findViewById(R.id.logoutTextView);
        loginTextView = findViewById(R.id.loginTextView);
        cancelSubTextView = findViewById(R.id.cancelSubscriptionTextView);
        earlyAdopterBadgeImageView = findViewById(R.id.earlyAdopterBadgeImageView);

        profileTextView = findViewById(R.id.profileTextView);
        privacyTextView = findViewById(R.id.privacyTextView);
        securityTextView = findViewById(R.id.securityTextView);
        helpTextView = findViewById(R.id.helpTextView);
        contactTextView = findViewById(R.id.contactTextView);
        reportTextView = findViewById(R.id.reportTextView);
        aboutTextView = findViewById(R.id.aboutTextView);
        termsTextView = findViewById(R.id.termsTextView);
        privacyPolicyTextView = findViewById(R.id.privacyPolicyTextView);
    }

    void setupButtons() {
        logoutTextView = findViewById(R.id.logoutTextView);
        loginTextView = findViewById(R.id.loginTextView);
        TextView cancelSubTextView = findViewById(R.id.cancelSubscriptionTextView);

        // Show/hide Login & Logout based on authToken
        boolean isLoggedIn = authToken != null && !authToken.isEmpty();

        if (isLoggedIn) {
            loginTextView.setVisibility(View.GONE);
            logoutTextView.setVisibility(View.VISIBLE);

            // Show Cancel Subscription button only if user is subscribed
            if (sharedPreferencesHelper.isSubscribed()) {
                cancelSubTextView.setVisibility(View.VISIBLE);
            } else {
                cancelSubTextView.setVisibility(View.GONE);
            }

        } else {
            loginTextView.setVisibility(View.VISIBLE);
            logoutTextView.setVisibility(View.GONE);
            cancelSubTextView.setVisibility(View.GONE);
        }

        // Login button
        loginTextView.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, isUserActivity.class);
            startActivity(intent);
        });

        // Logout button
        logoutTextView.setOnClickListener(v -> {
            sharedPreferencesHelper.saveAuthToken(null);

            Executors.newSingleThreadExecutor().execute(() -> {
                AppDatabase.getInstance(getApplicationContext()).clearAllTables();
            });

            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .requestIdToken("getString(R.string.google_settings_client_id)")
                    .build();

            GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(SettingsActivity.this, gso);

            googleSignInClient.signOut().addOnCompleteListener(task -> {
                sharedPreferencesHelper.saveAuthToken(null);
                Intent intent = new Intent(SettingsActivity.this, LoadingActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finishAffinity();
            });
        });

        // Cancel Subscription button
        cancelSubTextView.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://play.google.com/store/account/subscriptions"));
            intent.setPackage("com.android.vending");
            startActivity(intent);
        });

        profileTextView.setOnClickListener(v -> Toast.makeText(this, "Coming soon", Toast.LENGTH_SHORT).show());
        privacyTextView.setOnClickListener(v -> Toast.makeText(this, "Coming soon", Toast.LENGTH_SHORT).show());
        securityTextView.setOnClickListener(v -> Toast.makeText(this, "Coming soon", Toast.LENGTH_SHORT).show());
        helpTextView.setOnClickListener(v -> Toast.makeText(this, "Coming soon", Toast.LENGTH_SHORT).show());
        contactTextView.setOnClickListener(v -> Toast.makeText(this, "Coming soon", Toast.LENGTH_SHORT).show());
        reportTextView.setOnClickListener(v -> Toast.makeText(this, "Coming soon", Toast.LENGTH_SHORT).show());
        aboutTextView.setOnClickListener(v -> Toast.makeText(this, "Coming soon", Toast.LENGTH_SHORT).show());

        termsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://napknbook.com/policies/terms-of-service"));
                startActivity(browserIntent);
                //Intent intent = new Intent(MainActivity.this, ConnectActivity.class);
                //startActivity(intent);
            }
        });
        privacyPolicyTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://napknbook.com/policies/privacy-policy"));
                startActivity(browserIntent);
                //Intent intent = new Intent(MainActivity.this, ConnectActivity.class);
                //startActivity(intent);
            }
        });

    }

}