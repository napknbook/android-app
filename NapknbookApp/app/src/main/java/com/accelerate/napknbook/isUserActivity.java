package com.accelerate.napknbook;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;


import com.accelerate.napknbook.models.User;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;

import com.accelerate.napknbook.utils.SharedPreferencesHelper;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.SignInClient;

public class isUserActivity extends AppCompatActivity {


    Button newUserButton;
    Button existingUserButton;
    ImageButton continueWithGoogleButton;
    GoogleSignInClient mGoogleSignInClient ;
    private static final int RC_SIGN_UP = 9002;
    SharedPreferencesHelper sharedPreferencesHelper ;
    final String[] csrfToken = new String[1];
    final String[] authToken = new String[1];
    User user;

    private static final int RC_SIGN_IN = 100; // Request code for sign-in
    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_is_user);

        // Get display height and width
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setNavigationBarColor(ContextCompat.getColor(this, R.color.black));
        }


        // Configure Google Sign-In
        //oneTapClient = Identity.getSignInClient(this);
        //signInRequest = BeginSignInRequest.builder()
        //        .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
        //                .setSupported(true)
        //                .setServerClientId(getString(R.string.server_client_id)) // Web Client ID
        //                .setFilterByAuthorizedAccounts(false)
        //                .build())
        //        .build();



        /*
        Glide.with(this)
                .load(R.drawable.logo)
                .transform(new CircleCrop())
                .into(backgroundImageView);


         */


        setUpButtons();


    }

    void setUpButtons() {

        newUserButton = findViewById(R.id.newUserButton);
        existingUserButton = findViewById(R.id.existingUserButton);
        continueWithGoogleButton = findViewById(R.id.continueWithGoogleButton);


        newUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(isUserActivity.this, RegisterActivity.class);
                startActivity(intent);
                //finish();
            }
        });

        existingUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(isUserActivity.this, LoginActivity.class);
                startActivity(intent);
                //finish();
            }
        });
        continueWithGoogleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //signIn();
            }
        });


    }


    /*
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_UP);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_UP) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                // Use account information to register the user
                String email = account.getEmail();
                String username = "";
                String password = "";
                String userIdToken = account.getIdToken();

                // Send this information to your backend to create a new account

                NapknbookService service = RetrofitClientInstance.getRetrofitInstance().create(NapknbookService.class);
                GoogleRegisterRequest registerRequest = new GoogleRegisterRequest(username, email, password, userIdToken);

                Call<RegisterResponse> call = service.googleRegisterUser(registerRequest);
                call.enqueue(new Callback<RegisterResponse>() {
                    @Override
                    public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                        if (response.isSuccessful()) {
                            // Registration successful
                            RegisterResponse registerResponse = response.body();
                            String token = registerResponse.getToken();
                            Entity entity = registerResponse.getEntity();
                            sharedPreferencesHelper.saveAuthToken(token);
                            sharedPreferencesHelper.saveEntity(entity);

                            // Use the token as needed
                            // Store token in keystore

                            finish();

                        } else {
                            // Handle request errors depending on status code (e.g., 400, 401, 403, etc.)

                        }
                    }

                    @Override
                    public void onFailure(Call<RegisterResponse> call, Throwable t) {
                        // Handle failure: network error, exception, etc.
                    }
                });

            } catch (ApiException e) {
                // Handle exception
                Log.w("API", "signInResult:failed code=" + e.getStatusCode());
            }
        }
    }



    private void signIn() {
        oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(this, result -> {
                    try {
                        startIntentSenderForResult(
                                result.getPendingIntent().getIntentSender(),
                                RC_SIGN_IN, null, 0, 0, 0);
                    } catch (Exception e) {
                        Log.e("MainActivity", "Google Sign-In failed", e);
                        Toast.makeText(this, "Sign-In Failed", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(this, e -> {
                    Log.e("MainActivity", "Google Sign-In error", e);
                    Toast.makeText(this, "Sign-In Failed", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            try {
                SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(data);
                String idToken = credential.getGoogleIdToken();
                String displayName = credential.getDisplayName();

                if (idToken != null) {
                    Log.d("MainActivity", "ID Token: " + idToken);
                    Toast.makeText(this, "Welcome, " + displayName, Toast.LENGTH_SHORT).show();
                    // Send the ID token to your backend for verification
                }
            } catch (Exception e) {
                Log.e("MainActivity", "Sign-In failed", e);
                Toast.makeText(this, "Sign-In Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

     */
}