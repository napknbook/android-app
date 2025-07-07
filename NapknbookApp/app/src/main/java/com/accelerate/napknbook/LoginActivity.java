package com.accelerate.napknbook;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.util.Log;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.accelerate.napknbook.api.NapknbookService;
import com.accelerate.napknbook.api.RetrofitClientInstance;
import com.accelerate.napknbook.models.GoogleLoginRequest;
import com.accelerate.napknbook.models.LoginRequest;
import com.accelerate.napknbook.models.AuthResponse;
import com.accelerate.napknbook.models.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.util.Patterns;

import com.accelerate.napknbook.utils.SharedPreferencesHelper;

public class LoginActivity extends AppCompatActivity {

    EditText emailEditText;

    EditText usernameEditText ;

    EditText passwordEditText ;
    EditText confirmPasswordEditText ;

    TextView errorTextView ;

    Button loginButton ;
    ImageButton signInGoogleButton ;
    GoogleSignInClient mGoogleSignInClient ;
    SharedPreferencesHelper sharedPreferencesHelper ;
    ConstraintLayout loadingConstraintLayout ;

    private static final int RC_SIGN_IN = 9001;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Get display height and width
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setNavigationBarColor(ContextCompat.getColor(this, R.color.black));
        }

        usernameEditText = findViewById(R.id.loginUsernameEditText);
        emailEditText = findViewById(R.id.loginEmailEditText);
        passwordEditText = findViewById(R.id.loginPasswordEditText);
        errorTextView = findViewById(R.id.errorTextView);
        loadingConstraintLayout = findViewById(R.id.loadingSpinnerConstraintLayout);

        sharedPreferencesHelper = SharedPreferencesHelper.getInstance(this);


        /*
        Glide.with(this)
                .load(R.drawable.logo)
                .transform(new CircleCrop())
                .into(backgroundImageView);


         */


        loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginButton.setEnabled(false);

                loadingConstraintLayout.setVisibility(View.VISIBLE);
                String username = String.valueOf(usernameEditText.getText());
                String email = String.valueOf(emailEditText.getText());
                String password = String.valueOf(passwordEditText.getText());

                NapknbookService service = RetrofitClientInstance.getRetrofitInstance().create(NapknbookService.class);
                LoginRequest loginRequest = new LoginRequest(username, email, password);

                Call<AuthResponse> call = service.loginUser(loginRequest);
                call.enqueue(new Callback<AuthResponse>() {
                    @Override
                    public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                        if (response.isSuccessful()) {
                            AuthResponse authResponse = response.body();
                            String token = authResponse.getToken();
                            User user = authResponse.getUser();
                            sharedPreferencesHelper.saveAuthToken(token);
                            sharedPreferencesHelper.saveUser(user);
                            sharedPreferencesHelper.saveUserPk(user.pk);
                            sharedPreferencesHelper.setMainCharacterName(user.name + "#0");

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();

                        } else if (response.code() == 401){
                            loginButton.setEnabled(true);

                            errorTextView.setText("401");
                            errorTextView.setVisibility(View.VISIBLE);

                        }

                        else if (response.code() == 409) {
                            loginButton.setEnabled(true);

                            errorTextView.setText("Incorrect username or password");
                            errorTextView.setVisibility(View.VISIBLE);
                        }

                        else {
                            loginButton.setEnabled(true);

                            errorTextView.setText(response.message());
                            errorTextView.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(Call<AuthResponse> call, Throwable t) {
                        loginButton.setEnabled(true);

                        Log.e("Login", "Login failed: " + t.getMessage());
                        errorTextView.setText("Failed to login. Please try again.");
                        errorTextView.setVisibility(View.VISIBLE);
                        loadingConstraintLayout.setVisibility(View.GONE);

                    }
                });
            }
        });

        signInGoogleButton = findViewById(R.id.continueWithGoogleButton);
        signInGoogleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("getString(R.string.google_web_client_id)") // working web client 1 zarar@napknbook.com
                //.requestIdToken("REMOVED") // working android client release sha 1

                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loadingConstraintLayout.setVisibility(View.VISIBLE);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                String email = account.getEmail();
                String token = account.getIdToken();


                NapknbookService service = RetrofitClientInstance.getRetrofitInstance().create(NapknbookService.class);
                GoogleLoginRequest googleLoginRequest = new GoogleLoginRequest(email, token);

                Call<AuthResponse> call= service.googleLoginUser(googleLoginRequest);
                call.enqueue(new Callback<AuthResponse>() {
                    @Override
                    public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                        if (response.isSuccessful()) {

                            AuthResponse authResponse = response.body();
                            String token = authResponse.getToken();
                            User user = authResponse.getUser();
                            sharedPreferencesHelper.saveAuthToken(token);
                            sharedPreferencesHelper.saveUser(user);
                            sharedPreferencesHelper.saveUserPk(user.pk);
                            sharedPreferencesHelper.setMainCharacterName(user.name + "#0");

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else if (response.code() == 401){
                            errorTextView.setText("401");
                            errorTextView.setVisibility(View.VISIBLE);
                            loadingConstraintLayout.setVisibility(View.GONE);

                        }

                        else if (response.code() == 409) {
                            errorTextView.setText("Incorrect username or password");
                            errorTextView.setVisibility(View.VISIBLE);
                            loadingConstraintLayout.setVisibility(View.GONE);
                        }

                        else {
                            errorTextView.setText("Google registration failed. Try again.");
                            errorTextView.setVisibility(View.VISIBLE);
                            loadingConstraintLayout.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onFailure(Call<AuthResponse> call, Throwable t) {
                        Log.e("Login", "Login failed: " + t.getMessage());
                        errorTextView.setText("Failed to login. Please try again.");
                        errorTextView.setVisibility(View.VISIBLE);
                        loadingConstraintLayout.setVisibility(View.GONE);
                    }
                });

            } catch (ApiException e) {
                // Google Sign-In failed, update UI appropriately
                Log.w("API", "Google sign in failed", e);
                errorTextView.setText("Google registration failed. Try again.");
                errorTextView.setVisibility(View.VISIBLE);
                loadingConstraintLayout.setVisibility(View.GONE);
                // Handle the error (e.g., by displaying a message to the user)
            }
        }
    }


    public boolean isValidEmail(String email) {
        return email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}