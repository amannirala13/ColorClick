package asdev.amansoft.com.colorclick;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private Button playButton;
    private DatabaseReference mDatabase;
    private TextView worldsHighestScoreText;
    private String worldsHighestScore, personalHighestScore;
    private RelativeLayout loadingContainer, mainContainer;
    private CircularImageView profilePic;
    private ImageView winnerTag;
    private TextView playerName, worldsHighestScoreName, yourHighestScore;
    private MediaPlayer BackgroundFx;
    private int BackgroundMusicState = 0;
    private int BackPressedState = 0;
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
        yourHighestScore = findViewById(R.id.your_highest_score);
        winnerTag = findViewById(R.id.winner_tag);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference scoresRef = FirebaseDatabase.getInstance().getReference("ColorClick13");
        scoresRef.keepSynced(true);

        new helper().startBackgroundAnimation((RelativeLayout) findViewById(R.id.main_activity_screen));
        Picasso.get().load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString()).into(profilePic);
        playerName.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());


        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YoYo.with(Techniques.SlideOutDown).duration(700).repeat(0).playOn(findViewById(R.id.main_container));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent GameIntent = new Intent(MainActivity.this, Game.class);
                        GameIntent.putExtra("WORLD_HIGHEST", worldsHighestScore);
                        GameIntent.putExtra("PERSONAL_HIGHEST",personalHighestScore);
                        startActivity(GameIntent);
                        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                        finish();
                    }
                },700);

            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();

        if (BackgroundMusicState == 0) {

            BackgroundFx = MediaPlayer.create(this, R.raw.background_fx);
            new helper().PlayBackGroundMusic(BackgroundFx);
            BackgroundMusicState=1;

        }


        final DatabaseReference userDataBaseRef = mDatabase.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        userDataBaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("Personal Score")) {
                    personalHighestScore = dataSnapshot.child("Personal Score").child("HighestScore").getValue().toString();
                    yourHighestScore.setText(personalHighestScore);
                    if (worldsHighestScoreText.getText().toString().equalsIgnoreCase(yourHighestScore.getText().toString()))
                    {
                        winnerTag.setVisibility(View.VISIBLE);
                    }else
                    {
                        winnerTag.setVisibility(View.GONE);
                    }

                }
                else
                {
                    userDataBaseRef.child("Personal Score").child("HighestScore").setValue(0);
                    userDataBaseRef.child("Personal Score").child("HighestScore_Time").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Snackbar error = Snackbar.make(findViewById(R.id.main_container), "Failed to connect to Server !", Snackbar.LENGTH_SHORT);
                error.show();
            }
        });


        mDatabase.child("World").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    worldsHighestScore = dataSnapshot.child("World Highest").getValue().toString();
                    worldsHighestScoreText.setText(worldsHighestScore);
                    worldsHighestScoreName.setText(dataSnapshot.child("World Leader").getValue().toString());
                    loadingContainer.setVisibility(View.GONE);
                if (worldsHighestScoreText.getText().toString().equalsIgnoreCase(yourHighestScore.getText().toString()))
                {
                    winnerTag.setVisibility(View.VISIBLE);
                }else
                {
                    winnerTag.setVisibility(View.GONE);
                }
                animateMainScreen();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Snackbar error = Snackbar.make(findViewById(R.id.main_container), "Failed to connect to Server !", Snackbar.LENGTH_SHORT);
                error.show();
            }
        });

    }

    private void animateMainScreen() {
        //Enter Animation


        final MediaPlayer popSoundFX = MediaPlayer.create(this, R.raw.bubble);
        popSoundFX.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                popSoundFX.stop();
                popSoundFX.reset();
            }
        });

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
                        popSoundFX.start();
                    }
                }, 400);
            }

        }, 50);

    }


    @Override
    protected void onStop() {
        super.onStop();
        new helper().StopBackGroundMusic(BackgroundFx);
        BackgroundMusicState=0;
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if(savedInstanceState!=null)
        savedInstanceState.putInt("BackgroundMusicState", BackgroundMusicState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        BackgroundMusicState =savedInstanceState.getInt("BackgroundMusicState");
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        ++BackPressedState;

        if(BackPressedState==2) {
            finish();
            System.exit(0);
        }
        else {
            Snackbar exitMessage = Snackbar.make(findViewById(R.id.main_container), "Press again to exit !", Snackbar.LENGTH_SHORT);
            exitMessage.show();
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                    BackPressedState=0;

            }
        },2000);



    }


}

