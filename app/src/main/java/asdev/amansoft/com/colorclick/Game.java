package asdev.amansoft.com.colorclick;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;

public class Game extends AppCompatActivity {

    //Variable Initiation
    private CountDownTimer timer;
    private int RANDOM_NAME;
    private int RANDOM_COLOR;
    private int RANDOM_CASE;
    private ProgressBar timeBar;
    private Button redButton, blueButton, greenButton, yellowButton;
    private TextView scoreText , highestScoretext, challengeText, timeText;
    private int COLOURS [] = {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW};
    private String NAMES [] = {"Red", "Blue", "Green", "Yellow"};
    private int GAME_STATE = 0;
    private int answer;
    private int score =0;
    private Vibrator newChallengePing;
    private long worldHighestScore;
    private MediaPlayer gameTimerFX , timesUPFX, pointFx, looseFX;
    private final static int MAX_VOLUME = 100;

    private DatabaseReference mDatabase;

    //OnCreate
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);





        //Initialising variables
        timeBar = findViewById(R.id.time_bar);
        redButton = findViewById(R.id.red_button);
        blueButton = findViewById(R.id.blue_button);
        greenButton = findViewById(R.id.green_button);
        yellowButton = findViewById(R.id.yellow_button);
        challengeText = findViewById(R.id.challenge_text);
        timeText = findViewById(R.id.time_text);
        scoreText = findViewById(R.id.score);
        highestScoretext = findViewById(R.id.high_score);
        newChallengePing = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        worldHighestScore = Long.parseLong(getIntent().getStringExtra("WORLD_HIGHEST"));
        highestScoretext.setText(getIntent().getStringExtra("WORLD_HIGHEST"));



        mDatabase = FirebaseDatabase.getInstance().getReference();


                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        YoYo.with(Techniques.BounceIn).duration(300).repeat(0).playOn(findViewById(R.id.score_container));
                        YoYo.with(Techniques.BounceIn).duration(450).repeat(0).playOn(findViewById(R.id.timer_container));
                        YoYo.with(Techniques.BounceIn).duration(600).repeat(0).playOn(findViewById(R.id.challenge_text));
                        YoYo.with(Techniques.BounceIn).duration(750).repeat(0).playOn(findViewById(R.id.red_button));
                        YoYo.with(Techniques.BounceIn).duration(900).repeat(0).playOn(findViewById(R.id.blue_button));
                        YoYo.with(Techniques.BounceIn).duration(1050).repeat(0).playOn(findViewById(R.id.yellow_button));
                        YoYo.with(Techniques.BounceIn).duration(1150).repeat(0).playOn(findViewById(R.id.green_button));
                    }
                }, 100);



        //Setting Welcome Text
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                challengeText.setText("Set!");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        challengeText.setText("Go!");
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if(GAME_STATE==2)
                                    endGame();
                                else if(GAME_STATE==0)
                                startGame();
                            }
                        },1000);
                    }
                },2000);
            }
        },2500);


        createTimer(2000);


        redButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YoYo.with(Techniques.RubberBand).duration(200).repeat(0).playOn(findViewById(R.id.red_button));
                if(GAME_STATE == 1)
                checkAnswer(0);
            }
        });

        blueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YoYo.with(Techniques.RubberBand).duration(200).repeat(0).playOn(findViewById(R.id.blue_button));
                if(GAME_STATE == 1)
                    checkAnswer(1);
            }
        });

        greenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YoYo.with(Techniques.RubberBand).duration(200).repeat(0).playOn(findViewById(R.id.green_button));
                if(GAME_STATE == 1)
                    checkAnswer(2);
            }
        });

        yellowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YoYo.with(Techniques.RubberBand).duration(200).repeat(0).playOn(findViewById(R.id.yellow_button));
                if(GAME_STATE == 1)
                    checkAnswer(3);
            }
        });


    }

    //Check the answer if it is correct or not
    private void checkAnswer(int input) {

        StopGameTimerFX();
        if(answer==input)
        {
            PlayPointFX();
            ++score;
            scoreText.setText(Integer.toString(score));
            startGame();
        }
        else
        {
            timer.onFinish();
        }
    }

    //When the Game ends
    private void endGame() {

        StopPointFX();
        StopGameTimerFX();
        timer.cancel();
        PlayLooseFX();
        if(score>worldHighestScore) {
            mDatabase.child("World Leader").setValue(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
            mDatabase.child("World Highest").setValue(score);
            Intent endGameIntent = new Intent(Game.this, MainActivity.class);
            endGameIntent.putExtra("SCORE", score);
            startActivity(endGameIntent);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            finish();
        }
       // Toast.makeText(this, "You Lost", Toast.LENGTH_SHORT).show();
       // GAME_STATE = 0;

        else {
            Intent endGameIntent = new Intent(Game.this, ShowScore.class);
            endGameIntent.putExtra("SCORE", score);
            startActivity(endGameIntent);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            finish();
        }
    }

    //OnStop
    @Override
    protected void onStop() {
        super.onStop();
        GAME_STATE=2;
       // timer.onFinish();
       //finish();
    }


    //Starts the game
    private void startGame() {
        timeText.setTextColor(Color.WHITE);
        timeBar.getProgressDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        GAME_STATE=1;
       // vibrate();
      //  Toast.makeText(this, "Game Started", Toast.LENGTH_SHORT).show();

        final Random R_COLOR = new Random();
        final Random R_NAME = new Random();
        final Random R_CASE = new Random();
            RANDOM_COLOR = R_COLOR.nextInt(4);
            RANDOM_NAME = R_NAME.nextInt(4);
            RANDOM_CASE = R_CASE.nextInt(3);
        setAnswer(RANDOM_NAME);
        if(score>=100) {
            switch (RANDOM_CASE) {
                case 0:
                    challengeText.setText(NAMES[RANDOM_NAME]);
                    break;
                case 1:
                    challengeText.setText(NAMES[RANDOM_NAME].toUpperCase());
                    break;
                case 2:
                    challengeText.setText(NAMES[RANDOM_NAME].toLowerCase());
                    break;
                default:
                    challengeText.setText(NAMES[RANDOM_NAME]);
                    break;
            }
        } else{
            challengeText.setText(NAMES[RANDOM_NAME]);
        }
        challengeText.setTextColor(COLOURS[RANDOM_COLOR]);
        starttimer();


    }

    //Sets the answer according to the Random Colours and text generated
    private void setAnswer(int random_name) {
        answer = random_name;
    }

    //Starts the Game timer
    private void starttimer()
    {
        PlayGameTimerFX();
        timer.start();
    }

    //Creates the Game Timer
    private void createTimer(long milliseconds)
    {
        timer = new CountDownTimer(milliseconds, 1000) {

            public void onTick(long millisUntilFinished) {
                int seconds = (int) millisUntilFinished % 60000 / 1000;
                double percent = (seconds+1 / 2.0) * 100.0;
                if ((seconds+1)<=1) {
                    timeBar.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
                    timeText.setTextColor(Color.RED);
                }
                timeText.setText(Integer.toString(seconds+1));
                timeBar.setProgress((int) percent);
            }

            public void onFinish() {

          //      Toast.makeText(Game.this, "Oops! Timer Finished", Toast.LENGTH_SHORT).show();
                StopGameTimerFX();
                timeText.setText("0");
                timeBar.setProgress(0);
                GAME_STATE=0;



                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        endGame();
                    }
                },350);

            }

        };
    }


    //Vibrates the phone
    private void vibrate(){
        if (Build.VERSION.SDK_INT >= 26) {
            newChallengePing.vibrate(VibrationEffect.createOneShot(100,VibrationEffect.DEFAULT_AMPLITUDE));
        }else{
            newChallengePing.vibrate(100);
        }
    }


    private void PlayGameTimerFX()
    {
        gameTimerFX = MediaPlayer.create(this, R.raw.game_timer);
        gameTimerFX.start();
    }

    private void StopGameTimerFX()
    {
        if(gameTimerFX != null)
        gameTimerFX.reset();
    }

    private void PlayPointFX()
    {
        pointFx = MediaPlayer.create(this, R.raw.point);
        pointFx.start();
        final float volume = (float) (1 - (Math.log(MAX_VOLUME - 20) / Math.log(MAX_VOLUME)));
        pointFx.setVolume(volume, volume);
        pointFx.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                StopPointFX();
            }
        });
    }

    private void StopPointFX()
    {
        if(pointFx !=null)
        pointFx.reset();
    }


    private void PlayLooseFX()
    {
        looseFX = MediaPlayer.create(this, R.raw.loose);
        looseFX.start();
        looseFX.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                looseFX.reset();
            }
        });
    }


    }