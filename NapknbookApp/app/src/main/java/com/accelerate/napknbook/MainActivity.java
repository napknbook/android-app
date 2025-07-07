package com.accelerate.napknbook;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Filter;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.accelerate.napknbook.api.NapknbookService;
import com.accelerate.napknbook.api.RetrofitClientInstance;
import com.accelerate.napknbook.database.repositories.CharacterRepository;
import com.accelerate.napknbook.database.repositories.UserRepository;
import com.accelerate.napknbook.demo.DemoExploreActivity;
import com.accelerate.napknbook.models.Character;
import com.accelerate.napknbook.models.Skill;
import com.accelerate.napknbook.models.User;
import com.accelerate.napknbook.utils.ResourceMapSingleton;
import com.accelerate.napknbook.utils.SharedPreferencesHelper;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {

    ArrayList<Skill> skillModels = new ArrayList<>();
    ImageView goldImageView ;
    View goldButton ;
    Button exploreButton ;
    Button gearButton ;
    TextView generateCharacterTextView ;
    TextView earlyAdopterBadgeLevelTextView ;
    TextView verifiedBadgeLevelTextView ;
    Button skillsButton ;
    Button settingsButton ;
    Button connectButton ;
    Button inventoryButton ;
    Button tasksButton ;
    TextInputLayout textInputLayout ;
    TextView closeDayBreakerMenuButton ;
    Button heatButton ;
    ImageView heatingElementImageView ;
    ImageView cameraImageView ;
    Button confirmCharacterGenerationButton ;
    Button cancelCharacterGenerationButton ;
    ConstraintLayout confirmCharacterGenerationConstraintLayout;
    ConstraintLayout loadingSpinnerConstraintLayout ;
    ConstraintLayout verifiedBadgeConstraintLayout ;
    ConstraintLayout earlyAdopterBadgeConstraintLayout ;
    User user;
    VideoView videoView ;
    TextView usernameTextView ;
    Character mainCharacter ;
    String mainCharacterName ;
    int mainCharacterIndex ;
    ArrayList<Character> characters ;
    Map<String, Integer> dictionary = new HashMap<>();
    AutoCompleteTextView autoCompleteTextView ;
    String[] characterNames ;
    SharedPreferencesHelper sharedPreferencesHelper ;
    final String[] csrfToken = new String[1];
    final String[] authToken = new String[1];
    ArrayAdapter<String> adapter ;
    String aspectRatioString ;
    TextView walletBalanceTextView ;
    TextView helpTextView ;

    UserRepository userRepository;
    CharacterRepository characterRepository ;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setNavigationBarColor(ContextCompat.getColor(this, R.color.black));
        }

        sharedPreferencesHelper = SharedPreferencesHelper.getInstance(this);


        authToken[0] = sharedPreferencesHelper.getAuthToken();

        autoCompleteTextView = findViewById(R.id.autoCompleteTextView);

        // Get display height and width
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        // Get closest aspect ratio
        int aspectRatio = (int)(((float) height / width)*100) ;
        int closest = findClosestAspectRatio(aspectRatio);

        aspectRatioString = Integer.toString(findClosestAspectRatio(aspectRatio));

        startVideoView();

        characterRepository = new CharacterRepository(getApplicationContext());

        user = sharedPreferencesHelper.getUser();

        characterNames = getCharacterNames(user);


        autoCompleteTextView.setText(sharedPreferencesHelper.getMainCharacterName()); // or use main character


        setUpButtons();

        ImageView goldImageView = findViewById(R.id.goldImageView);

        Glide.with(getApplicationContext())
                .load(R.drawable.gold)
                .transform(new CircleCrop())
                .into(goldImageView);

        ImageView badgeImageView = findViewById(R.id.badgeImageView);

        Glide.with(getApplicationContext())
                .load(R.drawable.gold)
                .transform(new CircleCrop())
                .into(badgeImageView);

        ImageView verifiedBadgeImageView = findViewById(R.id.verifiedBadgeImageView);

        Glide.with(getApplicationContext())
                .load(R.drawable.logo)
                .transform(new CircleCrop())
                .into(verifiedBadgeImageView);


        ImageView earlyAdopterBadgeImageView = findViewById(R.id.earlyAdopterBadgeImageView);

        Glide.with(getApplicationContext())
                .load(R.raw.early_adopter_badge)
                .transform(new CircleCrop())
                .into(earlyAdopterBadgeImageView);

        ImageView confirmGoldImageView = findViewById(R.id.confirmMenuGoldImageView);

        Glide.with(getApplicationContext())
                .load(R.drawable.gold)
                .transform(new CircleCrop())
                .into(confirmGoldImageView);

        confirmCharacterGenerationConstraintLayout = findViewById(R.id.confirmCharacterGenerationConstraintLayout);
        confirmCharacterGenerationConstraintLayout.setVisibility(View.GONE);

        loadingSpinnerConstraintLayout = findViewById(R.id.loadingSpinnerConstraintLayout);
        loadingSpinnerConstraintLayout.setVisibility(View.GONE);

        walletBalanceTextView = findViewById(R.id.walletBalanceTextView);
        walletBalanceTextView.setText(user.getBalance().toString());
        //updateBalance();

        earlyAdopterBadgeLevelTextView = findViewById(R.id.earlyAdopterBadgeLevelTextView);
        earlyAdopterBadgeLevelTextView.setText(user.getEarly_adopter_badge_level());

        verifiedBadgeLevelTextView = findViewById(R.id.verifiedBadgeLevelTextView);
        verifiedBadgeLevelTextView.setText(user.getVerified_badge_level());

        heatingElementImageView = findViewById(R.id.heatingElementImageView);
        heatingElementImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getApplicationContext(), "DayBreaker Not Found", Toast.LENGTH_SHORT).show();

            }
        });

        cameraImageView = findViewById(R.id.cameraImageView);
        cameraImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "DayBreaker Not Found", Toast.LENGTH_SHORT).show();
            }
        });




        earlyAdopterBadgeConstraintLayout = findViewById(R.id.earlyAdopterBadgeConstraintLayout);
        verifiedBadgeConstraintLayout = findViewById(R.id.verifiedBadgeConstraintLayout);
        textInputLayout = findViewById(R.id.textInputLayout);

        generateCharacterTextView = findViewById(R.id.generateCharacterTextView);
        generateCharacterTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (authToken[0] == null) {

                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);

                } else {
                    confirmCharacterGenerationConstraintLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        if (characterNames.length > 1) {
            adapter = new ArrayAdapter<>(this,
                    R.layout.character_dropdown,
                    Arrays.copyOfRange(characterNames, 1, characterNames.length));
        } else {
            adapter = new ArrayAdapter<>(this,
                    R.layout.character_dropdown,
                    characterNames); // fallback to whole array or empty array
        }

        autoCompleteTextView.setText(sharedPreferencesHelper.getMainCharacterName());
        autoCompleteTextView.setAdapter(adapter);



    }



    void updateBalance() {

        NapknbookService service = RetrofitClientInstance.getRetrofitInstance().create(NapknbookService.class);
        Call<ResponseBody> call = service.getBalance("Bearer " + authToken[0]);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String json = response.body().string();
                        JSONObject jsonObject = new JSONObject(json);
                        String balance = jsonObject.optString("balance");
                        walletBalanceTextView.setText(balance);

                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Handle failure
            }
        });
    }
    void startVideoView() {

        String characterClipFilename = "guy0_workshop" + "_" + aspectRatioString + ".mp4" ;

        ResourceMapSingleton resourceMapSingleton = ResourceMapSingleton.getInstance();
        HashMap<String, Integer> resourceMap = resourceMapSingleton.getResourceMap();
        int resourceId = resourceMap.get(characterClipFilename);

        videoView = findViewById(R.id.videoView);
        String path = "android.resource://" + getPackageName() + "/" + resourceId;
        //String path = "android.resource://" + getPackageName() + "/" + R.raw.zarrari;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            videoView.setAudioFocusRequest(AudioManager.AUDIOFOCUS_NONE);
        }
        videoView.setVideoURI(Uri.parse(path));

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                videoView.start(); // Restart the video when it ends
            }
        });


        videoView.start();

    }

    void playConnectAnimation() {

        String characterClipFilename = "guy0_workshop" + "_connect_" + aspectRatioString + ".mp4" ;

        ResourceMapSingleton resourceMapSingleton = ResourceMapSingleton.getInstance();
        HashMap<String, Integer> resourceMap = resourceMapSingleton.getResourceMap();
        int resourceId = resourceMap.get(characterClipFilename);

        videoView = findViewById(R.id.videoView);
        String path = "android.resource://" + getPackageName() + "/" + resourceId;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            videoView.setAudioFocusRequest(AudioManager.AUDIOFOCUS_NONE);
        }
        videoView.setVideoURI(Uri.parse(path));

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                showDayBreakerUI();
            }
        });

        videoView.start();

    }

    void playConnectReversedAnimation() {


        String characterClipFilename = "guy0_workshop" + "_connect_reversed_" + aspectRatioString + ".mp4" ;

        ResourceMapSingleton resourceMapSingleton = ResourceMapSingleton.getInstance();
        HashMap<String, Integer> resourceMap = resourceMapSingleton.getResourceMap();
        int resourceId = resourceMap.get(characterClipFilename);

        videoView = findViewById(R.id.videoView);
        String path = "android.resource://" + getPackageName() + "/" + resourceId;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            videoView.setAudioFocusRequest(AudioManager.AUDIOFOCUS_NONE);
        }
        videoView.setVideoURI(Uri.parse(path));



        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                showMainUI();
                startVideoView();
            }
        });


        videoView.start();

    }

    public int findClosestAspectRatio(int aspectRatio) {

        int[] aspectRatios = {177, 200, 216, 222, 112, 150} ;

        int closest = aspectRatios[0] ;
        int minDiff = Math.abs(aspectRatios[0] - aspectRatio);

        for (int i = 1; i < aspectRatios.length; i++) {
            int diff = Math.abs(aspectRatios[i] - aspectRatio);
            if (diff < minDiff) {
                minDiff = diff;
                closest = aspectRatios[i];
            }
        }


        return closest ;
    }

    public void saveEntity(User user) {

        Gson gson = new Gson();
        String json = gson.toJson(user);

    }

    @Override
    protected void onResume() {
        super.onResume();


        if (videoView != null) {
            videoView.start();
        }

        // Prevent crash if characterNames is not yet ready
        if (characterNames == null || autoCompleteTextView == null) return;

        adapter = new ArrayAdapter<String>(this,
                R.layout.character_dropdown,
                //Arrays.copyOfRange(characterNames, 1, characterNames.length),
                characterNames) {
            @Override
            public Filter getFilter() {
                return new Filter() {
                    @Override
                    protected FilterResults performFiltering(CharSequence constraint) {
                        FilterResults results = new FilterResults();
                        results.values = characterNames;  // Always show the full list
                        results.count = characterNames.length;
                        return results;
                    }

                    @Override
                    protected void publishResults(CharSequence constraint, FilterResults results) {
                        notifyDataSetChanged();  // Refresh the dropdown with the full list
                    }
                };
            }
        };
        autoCompleteTextView.setAdapter(adapter);

        autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            if (position >= 0 && position < characterNames.length) {
                String mainCharacterName = characterNames[position];

                // Set selected text and dismiss dropdown
                autoCompleteTextView.setText(mainCharacterName, false); // 👈 prevent autocomplete from filtering again
                autoCompleteTextView.dismissDropDown();

                // Save selected character name
                sharedPreferencesHelper.setMainCharacterName(mainCharacterName);

                // TODO: Save the characterPk if needed here

                User user = sharedPreferencesHelper.getUser();
                String mainCharacterPk = getPkForCharacterName(mainCharacterName); // implement this helper
                sharedPreferencesHelper.setMainCharacterPk(mainCharacterPk);

                // Optional: trigger next logic, e.g. startVideoView();
            } else {
                Log.e("AutoComplete", "Invalid character selection at position " + position);
            }
        });




    }

    private String getPkForCharacterName(String mainCharacterName) {
        User user = sharedPreferencesHelper.getUser();
        if (user == null || mainCharacterName == null) return null;

        ArrayList<Character> characters = user.getCharacters();
        if (characters == null) return null;

        for (int i = 0; i < characters.size(); i++) {
            Character character = characters.get(i);
            String expectedName = user.getName() + "#" + i;
            if (expectedName.equals(mainCharacterName)) {
                return character.getPk();  // ✅ Found match
            }
        }

        return null;  // ❌ Not found
    }


    void setUpButtons() {

        goldButton = findViewById(R.id.goldButton);
        exploreButton = findViewById(R.id.exploreButton);
        //gearButton = findViewById(R.id.mainGearButton);
        settingsButton = findViewById(R.id.settingsButton);
        skillsButton = findViewById(R.id.skillsButton);
        connectButton = findViewById(R.id.connectButton);
        closeDayBreakerMenuButton = findViewById(R.id.closeDayBreakerMenuTextView);
        tasksButton = findViewById(R.id.tasksButton);
        inventoryButton = findViewById(R.id.inventoryButton);
        confirmCharacterGenerationButton = findViewById(R.id.confirmCharacterGenerationButton);
        cancelCharacterGenerationButton = findViewById(R.id.cancelCharacterGenerationButton);
        helpTextView = findViewById(R.id.helpImageView);

        inventoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, InventoryActivity.class);
                startActivity(intent);
            }
        });

        tasksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TasksActivity.class);
                startActivity(intent);
            }
        });

        helpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/@napknbook"));
                startActivity(browserIntent);
            }
        });

        goldButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GoldActivity.class);
                startActivity(intent);
            }
        });
        exploreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DemoExploreActivity.class);
                startActivity(intent);
            }
        });

        /*gearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GearActivity.class);
                startActivity(intent);
            }
        });*/
        skillsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, SkillsActivity.class);
                //intent.putParcelableArrayListExtra("skills", skills) ;
                startActivity(intent);

            }
        });
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Change visibility of main ui

                hideMainUI();

                playConnectAnimation();

                // Change visibility of Day Breaker ui


            }
        });

        closeDayBreakerMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hideDayBreakerUI();
                playConnectReversedAnimation();


            }
        });
        cancelCharacterGenerationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmCharacterGenerationConstraintLayout.setVisibility(View.GONE);
            }
        });
        confirmCharacterGenerationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loadingSpinnerConstraintLayout.setVisibility(View.VISIBLE);
                confirmCharacterGenerationConstraintLayout.setVisibility(View.GONE);

                NapknbookService service = RetrofitClientInstance.getRetrofitInstance().create(NapknbookService.class);
                Call<Character> call = service.generateCharacter("Bearer " + authToken[0], csrfToken[0]);

                call.enqueue(new Callback<Character>() {
                    @Override
                    public void onResponse(Call<Character> call, Response<Character> response) {
                        loadingSpinnerConstraintLayout.setVisibility(View.GONE);

                        if (response.isSuccessful() && response.body() != null) {
                            Character character = response.body();

                            // Save character
                            User user = sharedPreferencesHelper.getUser();
                            if (user.getCharacters() == null) {
                                user.setCharacters(new ArrayList<>());
                            }
                            user.getCharacters().add(character);

                            user.setBalance(user.getBalance()-15);
                            sharedPreferencesHelper.saveUser(user);

                            // Navigate away
                            Intent intent = new Intent(MainActivity.this, LoadingActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();

                        } else if (response.code() == 401) {
                            Toast.makeText(MainActivity.this, "Authentication error. Please log in again.", Toast.LENGTH_LONG).show();
                            sharedPreferencesHelper.saveAuthToken(null);
                            Intent intent = new Intent(MainActivity.this, isUserActivity.class);
                            startActivity(intent);
                            finish();

                        } else if (response.code() == 402) {
                            Toast.makeText(MainActivity.this, "Insufficient funds", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(MainActivity.this, GoldActivity.class);
                            startActivity(intent);
                            //confirmCharacterGenerationConstraintLayout.setVisibility(View.VISIBLE);

                        } else {
                            Toast.makeText(MainActivity.this, "Unexpected error. Please try again later.", Toast.LENGTH_LONG).show();
                            //confirmCharacterGenerationConstraintLayout.setVisibility(View.VISIBLE);
                        }
                    }
                    @Override
                    public void onFailure(Call<Character> call, Throwable t) {
                        loadingSpinnerConstraintLayout.setVisibility(View.GONE);
                        //confirmCharacterGenerationConstraintLayout.setVisibility(View.VISIBLE);
                        Toast.makeText(MainActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

            }
        });
    }

    private void hideMainUI() {


        settingsButton.setVisibility(View.INVISIBLE);
        exploreButton.setVisibility(View.INVISIBLE);
        tasksButton.setVisibility(View.INVISIBLE);
        settingsButton.setVisibility(View.INVISIBLE);
        connectButton.setVisibility(View.INVISIBLE);
        goldButton.setVisibility(View.INVISIBLE);
        generateCharacterTextView.setVisibility(View.INVISIBLE);
        helpTextView.setVisibility(View.INVISIBLE);
        textInputLayout.setVisibility(View.INVISIBLE);
        earlyAdopterBadgeConstraintLayout.setVisibility(View.INVISIBLE);
        verifiedBadgeConstraintLayout.setVisibility(View.INVISIBLE);

    }

    private void showMainUI() {

        settingsButton.setVisibility(View.VISIBLE);
        exploreButton.setVisibility(View.VISIBLE);
        tasksButton.setVisibility(View.VISIBLE);
        settingsButton.setVisibility(View.VISIBLE);
        connectButton.setVisibility(View.VISIBLE);
        goldButton.setVisibility(View.VISIBLE);
        generateCharacterTextView.setVisibility(View.VISIBLE);
        helpTextView.setVisibility(View.VISIBLE);

        earlyAdopterBadgeConstraintLayout.setVisibility(View.VISIBLE);
        verifiedBadgeConstraintLayout.setVisibility(View.VISIBLE);

        textInputLayout.setVisibility(View.VISIBLE);

    }

    private void hideDayBreakerUI() {

        closeDayBreakerMenuButton.setVisibility(View.INVISIBLE);
        cameraImageView.setVisibility(View.INVISIBLE);
        heatingElementImageView.setVisibility(View.INVISIBLE);

    }

    private void showDayBreakerUI() {

        closeDayBreakerMenuButton.setVisibility(View.VISIBLE);
        cameraImageView.setVisibility(View.VISIBLE);
        heatingElementImageView.setVisibility(View.VISIBLE);
    }


    String[] getCharacterNames(User user) {

        String[] characterNames  = new String[user.getCharacters().size()];
        String entityName = user.getName();

            for (int i = 0; i < user.getCharacters().size(); i++) {
                String characterName = entityName + "#" + Integer.toString(i) ;
                characterNames[i] = characterName ;
            }

        return characterNames ;
    }




    private void getCsrfToken() {


        NapknbookService service = RetrofitClientInstance.getRetrofitInstance().create(NapknbookService.class);
        Call<ResponseBody> call = service.getCsrfToken();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String json = response.body().string();
                        JSONObject jsonObject = new JSONObject(json);
                        csrfToken[0] = jsonObject.optString("csrfToken");

                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Handle failure
            }
        });
    }

}