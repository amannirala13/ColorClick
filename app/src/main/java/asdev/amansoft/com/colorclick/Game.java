package asdev.amansoft.com.colorclick;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;

public class Game extends AppCompatActivity {

    //Variable Initiation
    private CountDownTimer timer;
    private int RANDOM_NAME;
    private int RANDOM_COLOR;
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
                                startGame();
                            }
                        },1000);
                    }
                },2000);
            }
        },2500);


        createTimer(3000);



        redButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(GAME_STATE == 1)
                checkAnswer(0);
            }
        });

        blueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(GAME_STATE == 1)
                    checkAnswer(1);
            }
        });

        greenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(GAME_STATE == 1)
                    checkAnswer(2);
            }
        });

        yellowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(GAME_STATE == 1)
                    checkAnswer(3);
            }
        });


    }

    //Check the answer if it is correct or not
    private void checkAnswer(int input) {

        if(answer==input)
        {
            ++score;
            scoreText.setText(Integer.toString(score));
            startGame();
        }
        else
        {
            endGame();
        }
    }

    //When the Game ends
    private void endGame() {
        if(score>worldHighestScore)
        mDatabase.child("World Highest").setValue(score);
        Toast.makeText(this, "You Lost", Toast.LENGTH_SHORT).show();
        GAME_STATE = 0;
        timer.cancel();
        Intent endGameIntent = new Intent(Game.this, MainActivity.class);
        startActivity(endGameIntent);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        finish();
    }

    //OnStop
    @Override
    protected void onStop() {
        super.onStop();
       // timer.onFinish();
        timer.cancel();
    }

    //OnDestroy
    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }

    //OnPause
    @Override
    protected void onPause() {
        super.onPause();
        timer.cancel();
    }

    //Starts the game
    private void startGame() {
        timeText.setTextColor(Color.WHITE);
        timeBar.getProgressDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        GAME_STATE=1;
        vibrate();
        Toast.makeText(this, "Game Started", Toast.LENGTH_SHORT).show();

        final Random R_COLOR = new Random();
        final Random R_NAME = new Random();
            RANDOM_COLOR = R_COLOR.nextInt(4);
            RANDOM_NAME = R_NAME.nextInt(4);
        setAnswer(RANDOM_NAME);
        challengeText.setText(NAMES[RANDOM_NAME]);
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
       timer.start();
    }

    //Creates the Game Timer
    private void createTimer(long milliseconds)
    {
        timer = new CountDownTimer(milliseconds, 1000) {

            public void onTick(long millisUntilFinished) {
                int seconds = (int) millisUntilFinished % 60000 / 1000;
                double percent = (seconds / 2.0) * 100.0;
                if (seconds<=1) {
                    timeBar.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
                    timeText.setTextColor(Color.RED);
                }
                timeText.setText(Integer.toString(seconds));
                timeBar.setProgress((int) percent);
            }

            public void onFinish() {

                Toast.makeText(Game.this, "Oops! Timer Finished", Toast.LENGTH_SHORT).show();
               endGame();
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

    }