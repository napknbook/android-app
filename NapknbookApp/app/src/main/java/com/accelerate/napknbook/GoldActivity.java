package com.accelerate.napknbook;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.accelerate.napknbook.api.NapknbookService;
import com.accelerate.napknbook.api.RetrofitClientInstance;
import com.accelerate.napknbook.models.AuthResponse;
import com.accelerate.napknbook.models.User;
import com.android.billingclient.api.*;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.accelerate.napknbook.utils.SharedPreferencesHelper;
import com.accelerate.napknbook.models.PurchaseVerificationRequest;

import java.util.*;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GoldActivity extends AppCompatActivity {

    private BillingClient billingClient;
    private ArrayList<ProductDetails> products = new ArrayList<>();
    private ProductDetails activeProduct;
    private SharedPreferencesHelper sharedPreferencesHelper;
    private final String[] authToken = new String[1];

    ConstraintLayout napknbookInventoryConstraintLayout ;
    ConstraintLayout yourInventoryConstraintLayout ;
    ConstraintLayout activityConstraintLayout ;

    LinearLayout zBucks500LinearLayout ;

    LinearLayout zBucks5000LinearLayout ;
    LinearLayout zBucks250LinearLayout ;
    LinearLayout zBucks1000LinearLayout ;
    LinearLayout zBucks10000LinearLayout ;
    LinearLayout earlyAdopterBadgeLinearLayout ;
    LinearLayout verifiedBadgeLinearLayout ;
    LinearLayout subscriptionOfferLinearLayout ;

    Button acceptButton ;
    Button declineButton ;
    TextView closeButton ;
    TextView noAdsTextView ;
    TextView dollarsTextView ;

    ImageView napknbookGoldImageView ;
    ImageView goldImageView, goldImageView1 ;
    ImageView yourInventoryImageView ;
    ImageView napknbookInventoryImageView ;

    ImageView verifiedBadgeImageView ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gold);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setNavigationBarColor(ContextCompat.getColor(this, R.color.black));
        }

        Toolbar toolbar = findViewById(R.id.tradeToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.getNavigationIcon().setTint(Color.WHITE);

        sharedPreferencesHelper = SharedPreferencesHelper.getInstance(this);
        authToken[0] = sharedPreferencesHelper.getAuthToken();
        setupBillingClient();
        setupUI();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupBillingClient() {
        billingClient = BillingClient.newBuilder(this)
                .enablePendingPurchases()
                .setListener(this::handlePurchaseUpdate)
                .build();

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    Log.d("BillingClient", "Connected to Google Play Billing");
                    queryAvailableProducts();
                } else {
                    Log.e("BillingClient", "Connection failed: " + billingResult.getDebugMessage());
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                Log.w("BillingClient", "Disconnected from Google Play Billing");
            }
        });
    }

    private void handlePurchaseUpdate(BillingResult billingResult, @Nullable List<Purchase> purchases) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (Purchase purchase : purchases) {
                if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                    String productId = purchase.getProducts().get(0);
                    if (isConsumable(productId)) {
                        consumePurchase(purchase);
                    } else if (isSubscription(productId)) {
                        acknowledgePurchase(purchase);
                    }
                }
            }
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            Log.d("BillingClient", "Purchase canceled by user.");
        } else {
            Log.e("BillingClient", "Error during purchase: " + billingResult.getDebugMessage());
        }
    }

    private void queryAvailableProducts() {


        List<QueryProductDetailsParams.Product> productList = Arrays.asList(
                createProduct("zbucks_10000", BillingClient.ProductType.INAPP), createProduct("zbucks_250", BillingClient.ProductType.INAPP),
                createProduct("zbucks_500", BillingClient.ProductType.INAPP), createProduct("zbucks_1000", BillingClient.ProductType.INAPP),
                createProduct("early_adopter_badge", BillingClient.ProductType.INAPP)
        );

        QueryProductDetailsParams params = QueryProductDetailsParams.newBuilder().setProductList(productList).build();
        billingClient.queryProductDetailsAsync(params, (billingResult, productDetailsList) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && productDetailsList != null) {
                products.addAll(productDetailsList);
            }
        });

        // Query subscriptions
        List<QueryProductDetailsParams.Product> subProductList = Arrays.asList(
                createProduct("verified_badge", BillingClient.ProductType.SUBS)
        );

        QueryProductDetailsParams subParams = QueryProductDetailsParams.newBuilder()
                .setProductList(subProductList)
                .build();

        billingClient.queryProductDetailsAsync(subParams, (billingResult, productDetailsList) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && productDetailsList != null) {
                products.addAll(productDetailsList);
            }
        });

    }

    private QueryProductDetailsParams.Product createProduct(String productId, String productType) {
        return QueryProductDetailsParams.Product.newBuilder()
                .setProductId(productId)
                .setProductType(productType)
                .build();
    }



    private void launchPurchaseFlow(ProductDetails productDetails) {
        BillingFlowParams params = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(Collections.singletonList(
                        BillingFlowParams.ProductDetailsParams.newBuilder()
                                .setProductDetails(productDetails)
                                .build()
                ))
                .build();
        billingClient.launchBillingFlow(this, params);
    }

    private void launchSubPurchaseFlow(ProductDetails productDetails) {
        // Get the first available offer token for the subscription
        List<ProductDetails.SubscriptionOfferDetails> subscriptionOffers = productDetails.getSubscriptionOfferDetails();
        if (subscriptionOffers == null || subscriptionOffers.isEmpty()) {
            Log.e("Billing", "No subscription offers available");
            return;
        }

        String offerToken = subscriptionOffers.get(0).getOfferToken(); // Use the first offer token

        BillingFlowParams params = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(Collections.singletonList(
                        BillingFlowParams.ProductDetailsParams.newBuilder()
                                .setProductDetails(productDetails)
                                .setOfferToken(offerToken) // Add the offer token for subscriptions
                                .build()
                ))
                .build();

        // Launch the billing flow
        BillingResult billingResult = billingClient.launchBillingFlow(this, params);
        if (billingResult.getResponseCode() != BillingClient.BillingResponseCode.OK) {
            Log.e("Billing", "Error launching billing flow: " + billingResult.getResponseCode());
        }
    }

    private void acknowledgePurchase(Purchase purchase) {
        if (purchase.isAcknowledged()) return;
        AcknowledgePurchaseParams params = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.getPurchaseToken())
                .build();
        billingClient.acknowledgePurchase(params, billingResult -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                sendPurchaseToBackend(purchase, "SUBSCRIPTION");
            }
        });
    }

    private void consumePurchase(Purchase purchase) {
        ConsumeParams params = ConsumeParams.newBuilder().setPurchaseToken(purchase.getPurchaseToken()).build();
        billingClient.consumeAsync(params, (billingResult, purchaseToken) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                sendPurchaseToBackend(purchase, "INAPP");
            }
        });
    }

    private void sendPurchaseToBackend(Purchase purchase, String purchaseType) {
        String productId = purchase.getProducts().get(0);
        String purchaseToken = purchase.getPurchaseToken();
        PurchaseVerificationRequest request = new PurchaseVerificationRequest(productId, purchaseToken, purchaseType);

        NapknbookService service = RetrofitClientInstance.getRetrofitInstance().create(NapknbookService.class);
        service.verifyPurchase("Bearer " + authToken[0], request).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {

                if (response.isSuccessful()) {
                    Log.d("BillingClient", response.isSuccessful() ? "Purchase verified successfully!" : "Backend verification failed.");

                    AuthResponse authResponse = response.body();
                    User user = authResponse.getUser();
                    sharedPreferencesHelper.saveUser(user);
                    sharedPreferencesHelper.saveUserPk(user.pk);

                    Intent intent = new Intent(GoldActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();

                } else if (response.code() == 401) {

                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                Log.e("BillingClient", "Error contacting backend: " + t.getMessage());

            }
        });
    }

    private boolean isConsumable(String productId) {
        return Arrays.asList("zbucks_10000", "zbucks_5000", "zbucks_1000", "zbucks_500", "zbucks_250", "early_adopter_badge").contains(productId);
    }

    private boolean isSubscription(String productId) {
        return productId.equals("verified_badge");
    }

    private ProductDetails findProduct(String productId) {

        ProductDetails foundProduct = null ;

        for (ProductDetails productDetails : products) {
            if (productDetails.getProductId().equals(productId)) {
                foundProduct = productDetails ;
                break;
            }
        }
        return foundProduct ;
    }


    private void setupUI() {

        //napknbookGoldImageView = findViewById(R.id.napknbookGoldImageView);
        yourInventoryImageView = findViewById(R.id.yourInventoryImageView);
        napknbookInventoryImageView = findViewById(R.id.napknbookInventoryImageView);
        goldImageView = findViewById(R.id.goldImageView) ;
        goldImageView1 = findViewById(R.id.goldImageView1);


        napknbookInventoryConstraintLayout = findViewById(R.id.napknbookInventoryConstraintLayout) ;
        yourInventoryConstraintLayout = findViewById(R.id.yourInventoryConstraintLayout);
        activityConstraintLayout = findViewById(R.id.activityConstraintLayout);


        zBucks250LinearLayout = findViewById(R.id.zbucks_250_button) ;
        zBucks500LinearLayout = findViewById(R.id.zbucks_500_button) ;
        zBucks500LinearLayout.setVisibility(View.GONE);
        zBucks1000LinearLayout = findViewById(R.id.zbucks_1000_button) ;
        zBucks5000LinearLayout = findViewById(R.id.zbucks_5000_button) ;
        zBucks5000LinearLayout.setVisibility(View.GONE);

        zBucks10000LinearLayout = findViewById(R.id.zbucks_10000_button) ;
        zBucks10000LinearLayout.setVisibility(View.GONE);

        earlyAdopterBadgeLinearLayout = findViewById(R.id.early_adopter_badge_button);
        verifiedBadgeLinearLayout = findViewById(R.id.verified_badge_button);
        subscriptionOfferLinearLayout = findViewById(R.id.subscriptionOfferLinearLayout);

        verifiedBadgeImageView = findViewById(R.id.verifiedBadgeImageView);


        zBucks500LinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProductDetails product = findProduct("zbucks_500");
                launchPurchaseFlow(product);
            }
        });

        zBucks5000LinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProductDetails product = findProduct("zbucks_5000");
                launchPurchaseFlow(product);
            }
        });

        zBucks1000LinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProductDetails product = findProduct("zbucks_1000");
                launchPurchaseFlow(product);
            }
        });

        zBucks10000LinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProductDetails product = findProduct("zbucks_10000");
                launchPurchaseFlow(product);
            }
        });

        zBucks250LinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProductDetails product = findProduct("zbucks_250");
                launchPurchaseFlow(product);
            }
        });

        earlyAdopterBadgeLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProductDetails product = findProduct("early_adopter_badge");
                launchPurchaseFlow(product);
            }
        });

        verifiedBadgeLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                subscriptionOfferLinearLayout.setVisibility(View.VISIBLE);
                LinearLayout goldLinearLayout  = findViewById(R.id.linearLayout3);
                goldLinearLayout.setVisibility(View.GONE);
                noAdsTextView.setVisibility(View.GONE);
                napknbookInventoryConstraintLayout.setVisibility(View.GONE);
                dollarsTextView.setText("4.99 / month");


                acceptButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (authToken[0] == null) {
                            Intent intent = new Intent(GoldActivity.this, isUserActivity.class);
                            startActivity(intent);
                        }

                        else {

                            ProductDetails product = findProduct("verified_badge");
                            launchSubPurchaseFlow(product);
                        }

                    }
                });

                declineButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        finish();

                    }
                });
            }
        });





        /*
        Glide.with(getApplicationContext())
                .load(R.drawable.gold)
                .transform(new CircleCrop())
                .into(napknbookGoldImageView);
        */

        Glide.with(getApplicationContext())
                .load(R.raw.backpack_brown)
                .transform(new CircleCrop())
                .into(yourInventoryImageView);

        Glide.with(getApplicationContext())
                .load(R.raw.backpack_grey)
                .transform(new CircleCrop())
                .into(napknbookInventoryImageView);

        Glide.with(getApplicationContext())
                .load(R.drawable.gold)
                .transform(new CircleCrop())
                .into(goldImageView);

        Glide.with(getApplicationContext())
                .load(R.drawable.gold)
                .transform(new CircleCrop())
                .into(goldImageView1);

        Glide.with(getApplicationContext())
                .load(R.drawable.logo)
                .transform(new CircleCrop())
                .into(verifiedBadgeImageView);


        yourInventoryImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



            }
        });

        napknbookInventoryImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (napknbookInventoryConstraintLayout.getVisibility() == View.GONE) {
                    napknbookInventoryConstraintLayout.setVisibility(View.VISIBLE);
                } else {
                    napknbookInventoryConstraintLayout.setVisibility(View.VISIBLE);
                }
            }
        });


        dollarsTextView = findViewById(R.id.dollarsTextView);
        dollarsTextView.setText("2.50");

        noAdsTextView = findViewById(R.id.noAdsTextView);
        noAdsTextView.setText("No Ads for 7 Days");


        declineButton = findViewById(R.id.declineButton);
        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (noAdsTextView.getText().equals("No Ads for 14 Days")) {
                    finish();
                }
                else {
                    noAdsTextView.setText("No Ads for 14 Days");
                }
            }
        });

        acceptButton = findViewById(R.id.acceptButton);
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (authToken[0] == null) {
                    Intent intent = new Intent(GoldActivity.this, isUserActivity.class);
                    startActivity(intent);
                }
                else {
                    ProductDetails product = findProduct("zbucks_250");
                    launchPurchaseFlow(product);
                }
            }
        });

        closeButton = findViewById(R.id.closeButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                napknbookInventoryConstraintLayout.setVisibility(View.GONE);
            }
        });
    }
}
