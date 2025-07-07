package com.accelerate.napknbook;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.accelerate.napknbook.api.NapknbookService;
import com.accelerate.napknbook.api.RetrofitClientInstance;
import com.accelerate.napknbook.database.repositories.UserRepository;
import com.accelerate.napknbook.models.GoogleRegisterRequest;
import com.accelerate.napknbook.models.RegisterRequest;
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

import com.accelerate.napknbook.utils.SharedPreferencesHelper;

public class RegisterActivity extends AppCompatActivity {

    EditText usernameEditText ;
    EditText emailEditText ;
    EditText passwordEditText ;
    EditText confirmPasswordEditText ;
    TextView errorTextView ;
    Button registerButton ;
    ImageButton signUpGoogleButton;
    GoogleSignInClient mGoogleSignInClient ;
    private static final int RC_SIGN_UP = 9002;
    ConstraintLayout loadingConstraintLayout ;
    SharedPreferencesHelper sharedPreferencesHelper ;
    final String[] csrfToken = new String[1];
    final String[] authToken = new String[1];
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setNavigationBarColor(ContextCompat.getColor(this, R.color.black));
        }

        sharedPreferencesHelper = SharedPreferencesHelper.getInstance(this);

        usernameEditText = findViewById(R.id.registerUsernameEditText);
        emailEditText = findViewById(R.id.registerEmailEditText);
        passwordEditText = findViewById(R.id.registerPasswordEditText);
        confirmPasswordEditText = findViewById(R.id.registerConfirmPasswordEditText);
        errorTextView = findViewById(R.id.errorTextView);
        registerButton = findViewById(R.id.registerButton);
        loadingConstraintLayout = findViewById(R.id.loadingSpinnerConstraintLayout);


        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                registerButton.setEnabled(false);
                loadingConstraintLayout.setVisibility(View.VISIBLE);

                errorTextView.setVisibility(View.GONE);

                String username = String.valueOf(usernameEditText.getText());
                String email = String.valueOf(emailEditText.getText());
                String password = String.valueOf(passwordEditText.getText());
                String confirmPassword = String.valueOf(confirmPasswordEditText.getText());

                if (password.length() < 8) {
                    registerButton.setEnabled(true);
                    errorTextView.setText("Password must be at least 8 characters");
                    errorTextView.setVisibility(View.VISIBLE);
                    loadingConstraintLayout.setVisibility(View.GONE);
                    return;  // This will exit the onClick method early
                }

                if (!password.equals(confirmPassword)) {
                    registerButton.setEnabled(true);
                    errorTextView.setText("Passwords don't match");
                    errorTextView.setVisibility(View.VISIBLE);
                    loadingConstraintLayout.setVisibility(View.GONE);
                    return;  // This will exit the onClick method early
                }



                NapknbookService service = RetrofitClientInstance.getRetrofitInstance().create(NapknbookService.class);
                RegisterRequest registerRequest = new RegisterRequest(username, email, password, "");

                Call<AuthResponse> call = service.registerUser(registerRequest);
                call.enqueue(new Callback<AuthResponse>() {

                    @Override
                    public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                        if (response.isSuccessful()) {
                            // Registration successful
                            AuthResponse authResponse = response.body();
                            String token = authResponse.getToken();
                            User user = authResponse.getUser();

                            sharedPreferencesHelper.saveAuthToken(token);
                            sharedPreferencesHelper.saveUser(user);
                            sharedPreferencesHelper.saveUserPk(user.pk);
                            sharedPreferencesHelper.setMainCharacterName(user.name + "#0");

                            // Save UserEntity to local Room database
                            UserRepository repository = new UserRepository(getApplicationContext());
                            repository.insertUser(user);


                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();

                        } // Handle request errors depending on status code (e.g., 400, 401, 403, etc.)

                        else if (response.code() == 401){
                            registerButton.setEnabled(true);
                            errorTextView.setText("401");
                            errorTextView.setVisibility(View.VISIBLE);
                            loadingConstraintLayout.setVisibility(View.GONE);


                        }

                        else if (response.code() == 409) {
                            registerButton.setEnabled(true);

                            errorTextView.setText("Username already taken");
                            errorTextView.setVisibility(View.VISIBLE);
                            loadingConstraintLayout.setVisibility(View.GONE);

                        }

                            else {
                                registerButton.setEnabled(true);

                                errorTextView.setText(response.message());
                                errorTextView.setVisibility(View.VISIBLE);
                                loadingConstraintLayout.setVisibility(View.GONE);

                            }
                        }

                        @Override
                        public void onFailure(Call<AuthResponse> call, Throwable t) {
                            registerButton.setEnabled(true);
                            loadingConstraintLayout.setVisibility(View.GONE);

                            // Handle failure: network error, exception, etc.
                        }
                    });

                }
        });

        signUpGoogleButton = findViewById(R.id.signUpGoogleButton);
        signUpGoogleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                //.requestIdToken(getString(R.string.server_client_id))
                .requestIdToken("getString(R.string.google_web_client_id)") // working web client 1 zarar@napknbook.com
                //.requestIdToken("REMOVED") // working android client release sha 1
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        try {
            mGoogleSignInClient.signOut();
        }
        catch (Exception e) {

        }

    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_UP);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loadingConstraintLayout.setVisibility(View.VISIBLE);

        if (requestCode == RC_SIGN_UP) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                // Use account information to register the user
                String email = account.getEmail();
                String username = "" ;
                String password = "" ;
                String userIdToken = account.getIdToken();

                // Send this information to your backend to create a new account

                NapknbookService service = RetrofitClientInstance.getRetrofitInstance().create(NapknbookService.class);
                GoogleRegisterRequest registerRequest = new GoogleRegisterRequest(username, email, password, userIdToken);

                Call<AuthResponse> call = service.googleRegisterUser(registerRequest);
                call.enqueue(new Callback<AuthResponse>() {
                    @Override
                    public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                        if (response.isSuccessful()) {
                            // Registration successful
                            AuthResponse authResponse = response.body();
                            String token = authResponse.getToken();
                            User user = authResponse.getUser();
                            sharedPreferencesHelper.saveAuthToken(token);
                            sharedPreferencesHelper.saveUser(user);
                            sharedPreferencesHelper.saveUserPk(user.pk);
                            sharedPreferencesHelper.setMainCharacterName(user.name + "#0");

                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // Handle request errors depending on status code (e.g., 400, 401, 403, etc.)
                            errorTextView.setText("Google registration failed. Try again.");
                            errorTextView.setVisibility(View.VISIBLE);
                            loadingConstraintLayout.setVisibility(View.GONE);

                        }
                    }

                    @Override
                    public void onFailure(Call<AuthResponse> call, Throwable t) {
                        errorTextView.setText("Network error. Please check your connection.");
                        errorTextView.setVisibility(View.VISIBLE);
                        Log.e("RegisterActivity", "Google registration failed: " + t.getMessage());
                        loadingConstraintLayout.setVisibility(View.GONE);

                    }
                });
            } catch (ApiException e) {
                // Handle Google sign-in failure
                errorTextView.setText("Google Sign-In failed. Try again.");
                errorTextView.setVisibility(View.VISIBLE);
                Log.e("RegisterActivity", "Google Sign-In failed", e);
                loadingConstraintLayout.setVisibility(View.GONE);

            }
        }
    }

}