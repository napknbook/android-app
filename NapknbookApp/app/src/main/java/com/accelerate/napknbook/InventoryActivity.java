package com.accelerate.napknbook;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.accelerate.napknbook.models.User;
import com.accelerate.napknbook.models.InventoryItem;
import com.accelerate.napknbook.models.Skill;
import com.accelerate.napknbook.utils.SharedPreferencesHelper;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;


public class InventoryActivity extends AppCompatActivity {

    FlexboxLayout flexboxLayout ;
    View skillCardView ;
    FloatingActionButton addInventoryFAB;
    private static final int RC_ADD_INVENTORY_MANUAL = 9003;
    private static final int RC_ADD_INVENTORY_AUTO = 9004;
    ArrayList<InventoryItem> inventory;

    SharedPreferencesHelper sharedPreferencesHelper ;
    final String[] csrfToken = new String[1];
    final String[] authToken = new String[1];
    //Character mainCharacter ;
    int mainCharacterIndex ;
    User user;

    final int manualButtonId = 53 ;
    final int autoButtonId = 54 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setNavigationBarColor(ContextCompat.getColor(this, R.color.black));
        }

        Toolbar toolbar = findViewById(R.id.inventoryToolbar);
        setSupportActionBar(toolbar);

        // Enable the Up button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        toolbar.getNavigationIcon().setTint(Color.WHITE);



        sharedPreferencesHelper = SharedPreferencesHelper.getInstance(this);
        authToken[0] = sharedPreferencesHelper.getAuthToken();



        flexboxLayout = findViewById(R.id.InventoryFlexbox);

        //skills = getIntent().getParcelableArrayListExtra("skills");



        // Step 1: Define the launcher for receiving results
        ActivityResultLauncher<Intent> addInventoryManualLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        Skill skill = data.getParcelableExtra("skill");

                        if (skill != null) {

                            // Add a view or update UI with the skill data
                            View loadingSkillCardView = LayoutInflater.from(this)
                                    .inflate(R.layout.loading_skill_card_layout, flexboxLayout, false);
                            TextView skillTextView = loadingSkillCardView.findViewById(R.id.skillNameTextView) ;
                            TextView levelTextView = loadingSkillCardView.findViewById(R.id.skillLevelTextView);
                            skillTextView.setText(StringUtils.capitalize(skill.getName()));
                            levelTextView.setText("Lvl " + String.valueOf(skill.getLevel()));


                            loadingSkillCardView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    Intent intent = new Intent(InventoryActivity.this, SkillActivity.class);
                                    startActivity(intent);

                                }
                            });
                            flexboxLayout.addView(loadingSkillCardView);
                        }

                    }
                });

        // Step 1: Define the launcher for receiving results
        ActivityResultLauncher<Intent> addInventoryAutoLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        Skill skill = data.getParcelableExtra("skill");

                        if (skill != null) {

                            // Add a view or update UI with the skill data
                            View loadingSkillCardView = LayoutInflater.from(this)
                                    .inflate(R.layout.loading_skill_card_layout, flexboxLayout, false);
                            TextView skillTextView = loadingSkillCardView.findViewById(R.id.skillNameTextView) ;
                            TextView levelTextView = loadingSkillCardView.findViewById(R.id.skillLevelTextView);
                            skillTextView.setText(StringUtils.capitalize(skill.getName()));
                            levelTextView.setText("Lvl " + String.valueOf(skill.getLevel()));


                            loadingSkillCardView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    Intent intent = new Intent(InventoryActivity.this, SkillActivity.class);
                                    startActivity(intent);

                                }
                            });
                            flexboxLayout.addView(loadingSkillCardView);
                        }

                    }
                });


//       addInventoryFAB = findViewById(R.id.addInventoryFAB);
//       addInventoryFAB.setOnClickListener(new View.OnClickListener() {
//           @Override
//           public void onClick(View v) {
//               Intent intent = new Intent(InventoryActivity.this, AddSkillActivity.class);

//               addInventoryAutoLauncher.launch(intent);
//           }
//       });

        setupUI();
    }

    private void setupUI() {



        SpeedDialView speedDialView = findViewById(R.id.addInventoryFAB);


        speedDialView.addActionItem(
                new SpeedDialActionItem.Builder(manualButtonId, android.R.color.transparent)
                        //.setFabBackgroundColor(R.color.black)
                        .setLabel("Manual")
                        .setLabelBackgroundColor(getResources().getColor(R.color.black))
                        .setLabelColor(getResources().getColor(R.color.white))
                        .create()
        );

        speedDialView.addActionItem(
                new SpeedDialActionItem.Builder(autoButtonId, android.R.color.transparent)
                        .setLabel("Auto")
                        .setLabelBackgroundColor(getResources().getColor(R.color.black))
                        .setLabelColor(getResources().getColor(R.color.white))
                        .create()
        );

        speedDialView.setOnActionSelectedListener(actionItem -> {
            switch (actionItem.getId()) {
                case manualButtonId:
                    Toast.makeText(this, "Edit clicked", Toast.LENGTH_SHORT).show();

                    return false; // Keep menu open
                case autoButtonId:
                    Intent intent = new Intent(InventoryActivity.this, CameraActivity.class);
                    startActivity(intent);
                    return true;
            }
            return false;
        });
    }

    private void setupData() {

        //TODO Check if data is already cached


//        NapknbookService service = RetrofitClientInstance.getRetrofitInstance().create(NapknbookService.class);
//        String characterPk = userEntity.getCharacters().get(sharedPreferencesHelper.getMainCharacterIndex()).getPk() ;
//        Call<List<InventoryItem>> call = service.getInventory("Basic " + authToken[0], characterPk);
//        call.enqueue(new Callback<List<InventoryItem>>() {
//            @Override
//            public void onResponse(Call<List<InventoryItem>> call, Response<List<InventoryItem>> response) {
//
//                inventory = new ArrayList<>(response.body()) ;
//
//
//
//                for (InventoryItem inventoryItem : inventory) {
//
//                    View inventoryItemCard = LayoutInflater.from(getApplicationContext()).inflate(R.layout.skill_card_layout_v2, flexboxLayout, false);
//                    TextView inventoryItemTextView = inventoryItemCard.findViewById(R.id.skillNameTextView) ;
//                    TextView levelTextView = inventoryItemCard.findViewById(R.id.skillLevelTextView);
//                    ImageView inventoryItemImageView = inventoryItemCard.findViewById(R.id.skillImageView);
//
//
//                    Glide.with(getApplicationContext())
//                            .load(inventoryItem.getImgUrl())
//                            //.transform(new CircleCrop())
//                            .into(inventoryItemImageView);
//
//                    inventoryItemCard.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//
//                            Intent intent = new Intent(InventoryActivity.this, InventoryItemActivity.class);
//                            intent.putExtra("inventoryItem", inventoryItem);
//                            startActivity(intent);
//
//                        }
//                    });
//                    flexboxLayout.addView(inventoryItemCard);
//                }
//
//            }
//
//            @Override
//            public void onFailure(Call<List<InventoryItem>> call, Throwable t) {
//
//                String test = "sas" ;
//            }
//        });
//
//
//
//
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_ADD_INVENTORY_AUTO && resultCode == RESULT_OK) {

            InventoryItem inventoryItem = getIntent().getParcelableExtra("inventoryItem");
            inventory.add(inventoryItem);

            View inventoryItemCardView = LayoutInflater.from(this).inflate(R.layout.skill_card_layout, flexboxLayout, false);
            inventoryItemCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(InventoryActivity.this, InventoryItemActivity.class);
                    intent.putExtra("inventoryItemName", inventoryItem.getName());
                    startActivity(intent);
                }
            });
            flexboxLayout.addView(inventoryItemCardView);




        }

        if (requestCode == RC_ADD_INVENTORY_MANUAL && resultCode == RESULT_OK) {

            InventoryItem inventoryItem = getIntent().getParcelableExtra("inventoryItem");
            inventory.add(inventoryItem);

            View inventoryItemCardView = LayoutInflater.from(this).inflate(R.layout.skill_card_layout, flexboxLayout, false);
            inventoryItemCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(InventoryActivity.this, InventoryItemActivity.class);
                    intent.putExtra("inventoryItemName", inventoryItem.getName());
                    startActivity(intent);
                }
            });
            flexboxLayout.addView(inventoryItemCardView);




        }
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


}