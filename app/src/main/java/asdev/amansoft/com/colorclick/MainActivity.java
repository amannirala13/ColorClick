package asdev.amansoft.com.colorclick;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {
    private Button playButton;
    private DatabaseReference mDatabase;
    private TextView worldsHighestScoreText;
    private String worldsHighestScore;
    private RelativeLayout loadingContainer, mainContainer;
    private CircularImageView profilePic;
    private TextView playerName, worldsHighestScoreName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        playButton = findViewById(R.id.play_button);
        worldsHighestScoreText = findViewById(R.id.world_highest_score);
        loadingContainer = findViewById(R.id.loading_container);
        mainContainer = findViewById(R.id.main_container);
        profilePic = findViewById(R.id.player_image);
        playerName = findViewById(R.id.name_text);
        worldsHighestScoreName = findViewById(R.id.world_highest_score_name);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference scoresRef = FirebaseDatabase.getInstance().getReference("ColorClick13");
        scoresRef.keepSynced(true);

        new helper().startBackgroundAnimation((RelativeLayout) findViewById(R.id.main_activity_screen));
        Picasso.get().load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString()).into(profilePic);
        playerName.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());


        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent GameIntent = new Intent(MainActivity.this, Game.class);
                GameIntent.putExtra("WORLD_HIGHEST", worldsHighestScore);
                startActivity(GameIntent);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                worldsHighestScore = dataSnapshot.child("World Highest").getValue().toString();
                worldsHighestScoreText.setText(worldsHighestScore);
                worldsHighestScoreName.setText(dataSnapshot.child("World Leader").getValue().toString());
                loadingContainer.setVisibility(View.GONE);
                animateMainScreen();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Error connecting to Database", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void animateMainScreen() {
        //Enter Animation
        mainContainer.setVisibility(View.VISIBLE);
        profilePic.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                YoYo.with(Techniques.FlipInY).duration(1500).repeat(0).playOn(findViewById(R.id.player_image));
                YoYo.with(Techniques.BounceInUp).duration(1500).repeat(0).playOn(findViewById(R.id.main_container));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        YoYo.with(Techniques.BounceIn).duration(200).repeat(0).playOn(findViewById(R.id.welcome_text));
                        YoYo.with(Techniques.BounceIn).duration(350).repeat(0).playOn(findViewById(R.id.name_text));
                        YoYo.with(Techniques.BounceIn).duration(500).repeat(0).playOn(findViewById(R.id.your_highest_score_text));
                        YoYo.with(Techniques.BounceIn).duration(650).repeat(0).playOn(findViewById(R.id.your_highest_score));
                        YoYo.with(Techniques.BounceIn).duration(800).repeat(0).playOn(findViewById(R.id.world_highest_score_text));
                        YoYo.with(Techniques.BounceIn).duration(950).repeat(0).playOn(findViewById(R.id.world_highest_score_name));
                        YoYo.with(Techniques.BounceIn).duration(1050).repeat(0).playOn(findViewById(R.id.world_highest_score));
                        YoYo.with(Techniques.BounceIn).duration(1200).repeat(0).playOn(findViewById(R.id.play_button));
                        YoYo.with(Techniques.BounceIn).duration(1350).repeat(0).playOn(findViewById(R.id.play_text));
                    }
                }, 400);
            }

        }, 50);

    }
}