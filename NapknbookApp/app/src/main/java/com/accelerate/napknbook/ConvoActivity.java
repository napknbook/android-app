package com.accelerate.napknbook;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.accelerate.napknbook.adapters.CommentRecyclerViewAdapter;
import com.accelerate.napknbook.api.NapknbookService;
import com.accelerate.napknbook.api.RetrofitClientInstance;
import com.accelerate.napknbook.models.Comment;
import com.accelerate.napknbook.models.Convo;
import com.accelerate.napknbook.utils.ResourceMapSingleton;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.accelerate.napknbook.utils.SharedPreferencesHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConvoActivity extends AppCompatActivity {

    ImageView originalPosterImageView;
    RecyclerView commentsRecyclerView;

    ArrayList<Comment> comments = new ArrayList<>();
    Convo convo ;

    TextView conversationTitleTextView ;
    TextView conversationDescTextView ;

    SharedPreferencesHelper sharedPreferencesHelper ;
    final String[] csrfToken = new String[1];
    final String[] authToken = new String[1];



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_convo);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setNavigationBarColor(ContextCompat.getColor(this, R.color.black));
        }

        sharedPreferencesHelper = SharedPreferencesHelper.getInstance(this);
        authToken[0] = sharedPreferencesHelper.getAuthToken();

        Intent intent = getIntent()  ;
        convo = intent.getParcelableExtra("convo");

        originalPosterImageView = findViewById(R.id.originalPosterImageView);


        String pp_filename = convo.getAgent().getCharacterType() + "_pp.webp" ;

        ResourceMapSingleton resourceMapSingleton = ResourceMapSingleton.getInstance();
        HashMap<String, Integer> resourceMap = resourceMapSingleton.getResourceMap();
        int resourceId = resourceMap.get(pp_filename);

        Glide.with(this)
                .load(resourceId)
                .transform(new CircleCrop())
                .into(originalPosterImageView);

//        Glide.with(getApplicationContext())
//                .load(R.drawable.gold)
//                .transform(new CircleCrop())
//                .into(originalPoster);

        commentsRecyclerView = findViewById(R.id.commentsRecyclerView);

        conversationTitleTextView = findViewById(R.id.conversationTitleTextView);
        conversationDescTextView = findViewById(R.id.conversationDescTextView);


        conversationTitleTextView.setText(convo.getTitle());
        conversationDescTextView.setText(convo.getContent());


        //Check for cached

        NapknbookService service = RetrofitClientInstance.getRetrofitInstance().create(NapknbookService.class);

        Call<List<Comment>> call = service.getComments(authToken[0], convo.getPk());

        call.enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                comments.addAll(new ArrayList<>(response.body()));

                CommentRecyclerViewAdapter adapter = new CommentRecyclerViewAdapter(getApplicationContext(), comments);
                commentsRecyclerView.setAdapter(adapter);
                commentsRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));


            }

            @Override
            public void onFailure(Call<List<Comment>> call, Throwable t) {


            }
        });


    }
}