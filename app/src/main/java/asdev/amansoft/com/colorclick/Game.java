package asdev.amansoft.com.colorclick;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class Game extends AppCompatActivity implements RewardedVideoAdListener {

    //Variable Initiation
    private CountDownTimer timer;
    private int RANDOM_NAME;
    private int RANDOM_COLOR;
    private int RANDOM_CASE;
    private int [] GENERATED_BUTTONS;
    private ProgressBar timeBar;
    private Button redButton, blueButton, greenButton, yellowButton;
    private TextView scoreText , highestScoretext, challengeText, timeText;
    private int COLOURS [] = {Color.parseColor("#FF1E1E"),Color.parseColor("#4169E1"), Color.parseColor("#00BF00"),Color.parseColor("#FFBF00")};
    private String NAMES [] = {"Red", "Blue", "Green", "Yellow"};
    private int GAME_STATE = 0;
    private int answer;
    private long score =0;
  //  private Vibrator newChallengePing;
    private long worldHighestScore, personalHighest;
    private MediaPlayer gameTimerFX, pointFx, looseFX;
    private final static int MAX_VOLUME = 100;
    private long OUT_TIME =2000;
    private RelativeLayout gameScreen;
    private RewardedVideoAd pointsAd;
    private DatabaseReference mDatabase, scoreDatabase;
    private ProgressDialog LoadingDialog;

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
        gameScreen = findViewById(R.id.game_screen);
        timeText = findViewById(R.id.time_text);
        scoreText = findViewById(R.id.score);
        highestScoretext = findViewById(R.id.high_score);
        pointsAd = MobileAds.getRewardedVideoAdInstance(this);
      //  newChallengePing = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        worldHighestScore = Long.parseLong(getIntent().getStringExtra("WORLD_HIGHEST"));
        personalHighest = Long.parseLong(getIntent().getStringExtra("PERSONAL_HIGHEST"));
        highestScoretext.setText(getIntent().getStringExtra("WORLD_HIGHEST"));

        LoadingDialog = new ProgressDialog(Game.this);
        LoadingDialog.setMessage("Please wait...");

        GENERATED_BUTTONS = new int[]{0, 1, 2, 3};

        pointsAd.setRewardedVideoAdListener(this);
        LoadAd();
        
        mDatabase = FirebaseDatabase.getInstance().getReference();
        scoreDatabase = mDatabase.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

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


        Date CURRENT_TIME_PLAYED = Calendar.getInstance().getTime();

        scoreDatabase.child("LastPlayed").setValue(CURRENT_TIME_PLAYED.toString());


        //Setting Welcome Text
        YoYo.with(Techniques.Pulse).duration(500).repeat(0).playOn(findViewById(R.id.challenge_text));
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


        redButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YoYo.with(Techniques.RubberBand).duration(200).repeat(0).playOn(findViewById(R.id.red_button));
                if(GAME_STATE == 1)
                checkAnswer(GENERATED_BUTTONS[0]);
            }
        });

        blueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YoYo.with(Techniques.RubberBand).duration(200).repeat(0).playOn(findViewById(R.id.blue_button));
                if(GAME_STATE == 1)
                    checkAnswer(GENERATED_BUTTONS[1]);
            }
        });

        greenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YoYo.with(Techniques.RubberBand).duration(200).repeat(0).playOn(findViewById(R.id.green_button));
                if(GAME_STATE == 1)
                    checkAnswer(GENERATED_BUTTONS[2]);
            }
        });

        yellowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YoYo.with(Techniques.RubberBand).duration(200).repeat(0).playOn(findViewById(R.id.yellow_button));
                if(GAME_STATE == 1)
                    checkAnswer(GENERATED_BUTTONS[3]);
            }
        });


    }

    private void LoadAd() {
        if(!pointsAd.isLoaded())
        {
            pointsAd.loadAd("ca-app-pub-3923725939846581/5361606697", new AdRequest.Builder().build());
        }
    }

    //Check the answer if it is correct or not
    private void checkAnswer(int input) {

        StopGameTimerFX();
        if(answer==input)
        {
            PlayPointFX();
            ++score;
            YoYo.with(Techniques.Shake).duration(200).repeat(0).playOn(findViewById(R.id.score));
            scoreText.setText(Long.toString(score));
            updateGameScreen(score);
            startGame();
        }
        else
        {
            timer.onFinish();
        }
    }

    private void updateGameScreen(long score) {

        if(score>200)
        {
            if(score>500)
            {
                if(score>1000)
                {
                        gameScreen.setBackground(getDrawable(R.drawable.backdrop_low));

                }else{
                    gameScreen.setBackground(getDrawable(R.drawable.almost_blue_game_gradient));
                }
            }else
            {
                gameScreen.setBackground(getDrawable(R.drawable.cheap_sugar_game_gradient));
            }

        }
    }

    //When the Game ends
    private void endGame() {
        GAME_STATE=0;
        StopPointFX();
        StopGameTimerFX();
        if(timer!=null)
        timer.cancel();
        PlayLooseFX();
        if(pointsAd.isLoaded())
        {
            ShowPromotionDialog();
        }
        else {
            AfterChoice();
        }

    }

    private void ShowPromotionDialog() {

        AlertDialog.Builder builder1 = new AlertDialog.Builder(Game.this);
        builder1.setMessage("Get extra points for viewing this Ad." );
        builder1.setCancelable(false);

        builder1.setPositiveButton(
                "Get extra points!",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                       ShowAd();
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                       AfterChoice();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    private void ShowAd() {
        if (pointsAd.isLoaded())
        {
            pointsAd.show();
        }
    }

    private void AfterChoice() {
        Date CURRENT_TIME = Calendar.getInstance().getTime();
        if(score>personalHighest) {
            scoreDatabase.child("Personal Score").child("HighestScore").setValue(score);
            scoreDatabase.child("Personal Score").child("HighestScore_Time").setValue(CURRENT_TIME.toString());
           // Toast.makeText(this, "   Congratulations!!! You broke your own Record 😍   ", Toast.LENGTH_LONG).show();


            if (score > worldHighestScore) {
                mDatabase.child("World").child("World Leader").setValue(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                mDatabase.child("World").child("World Highest").setValue(score);
                mDatabase.child("World").child("Time").setValue(CURRENT_TIME.toString());
                mDatabase.child("World").child("UserID").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
               // Toast.makeText(this, "   Congratulations!!! You broke the world Record 😍   ", Toast.LENGTH_LONG).show();

                //ToDO>>> Show world congo Screen
                Intent endGameIntent = new Intent(Game.this, WorldHighScore.class);
                endGameIntent.putExtra("SCORE", score);
                startActivity(endGameIntent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
           else
                {
                    //ToDO>>>> Show Personal High Score Screen
                    Intent endGameIntent = new Intent(Game.this, PersonalHighScore.class);
                    endGameIntent.putExtra("SCORE", score);
                    startActivity(endGameIntent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
            }

        }
        else
            {
            Intent endGameIntent = new Intent(Game.this, ShowScore.class);
            endGameIntent.putExtra("SCORE", score);
            startActivity(endGameIntent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }
       /* Intent endGameIntent = new Intent(Game.this, ShowScore.class);
        endGameIntent.putExtra("SCORE", score);
        startActivity(endGameIntent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();*/

        // Toast.makeText(this, "You Lost", Toast.LENGTH_SHORT).show();
        // GAME_STATE = 0;
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
        if(timer!=null)
            stopTimer();
        resetTimer();
        createTimer(OUT_TIME);
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
        setAnswer(RANDOM_COLOR);
        if(score>=100)
        {
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
        GENERATED_BUTTONS = getRandomButtons();
        setRandomButtons(GENERATED_BUTTONS);
        starttimer();


    }

    private void setRandomButtons(int[] randomButtons) {


        redButton.setBackgroundColor(COLOURS[randomButtons[0]]);
        redButton.setText(NAMES[randomButtons[0]]);

        blueButton.setBackgroundColor(COLOURS[randomButtons[1]]);
        blueButton.setText(NAMES[randomButtons[1]]);

        greenButton.setBackgroundColor(COLOURS[randomButtons[2]]);
        greenButton.setText(NAMES[randomButtons[2]]);

        yellowButton.setBackgroundColor(COLOURS[randomButtons[3]]);
        yellowButton.setText(NAMES[randomButtons[3]]);


    }

    //Sets the answer according to the Random Colours and text generated
    private void setAnswer(int random_color) {
        answer = random_color;
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
                OUT_TIME = millisUntilFinished;
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

    private void stopTimer()
    {
        if(timer!=null)
        timer.cancel();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Snackbar canGoBackMessage = Snackbar.make(findViewById(R.id.game_screen), "Can't go back at this stage !", Snackbar.LENGTH_SHORT);
        canGoBackMessage.show();
    }

    private void resetTimer()
    {
        OUT_TIME = 2000;
    }


/*
    //Vibrates the phone
    private void vibrate(){
        if (Build.VERSION.SDK_INT >= 26) {
            newChallengePing.vibrate(VibrationEffect.createOneShot(100,VibrationEffect.DEFAULT_AMPLITUDE));
        }else{
            newChallengePing.vibrate(100);
        }
    }

*/
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



    private int[] getRandomButtons()
    {
        int RANDOM_B1, RANDOM_B2, RANDOM_B3, RANDOM_B4;
        int returnArray[] = {0,1,2,3};
        final Random RANDOM_BX = new Random();

        RANDOM_B1 = RANDOM_BX.nextInt(4);
        RANDOM_B2 = RANDOM_BX.nextInt(4);
        if(RANDOM_B1!=RANDOM_B2)
        {
            RANDOM_B3 = RANDOM_BX.nextInt(4);
            if(RANDOM_B3!=RANDOM_B1 && RANDOM_B3!=RANDOM_B2)
            {
                RANDOM_B4= RANDOM_BX.nextInt(4);
                if(RANDOM_B4!=RANDOM_B1 && RANDOM_B4!=RANDOM_B2 && RANDOM_B4!=RANDOM_B3)
                {

                   // Toast.makeText(this, Integer.toString(RANDOM_B1)+Integer.toString(RANDOM_B2)+Integer.toString(RANDOM_B3)+Integer.toString(RANDOM_B4), Toast.LENGTH_SHORT).show();

                    returnArray= new int[]{RANDOM_B1, RANDOM_B2, RANDOM_B3, RANDOM_B4};
                }
                else{
                    getRandomButtons();
                }
            }
            else
            {
                getRandomButtons();
            }
        }
        else{
            getRandomButtons();
        }
        return returnArray;
    }


    @Override
    public void onRewardedVideoAdLoaded() {
        Snackbar reward = Snackbar.make(findViewById(R.id.game_screen), "✔ You have a chance for extra points for this turn!", Snackbar.LENGTH_LONG);
        reward.show();
    }

    @Override
    public void onRewardedVideoAdOpened() {
    }

    @Override
    public void onRewardedVideoStarted() {
        Snackbar rewardInfo = Snackbar.make(findViewById(R.id.game_screen), "Watch till end to get extra points !", Snackbar.LENGTH_SHORT);
        rewardInfo.show();
    }

    @Override
    public void onRewardedVideoAdClosed() {
        AfterChoice();
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {


        int InstantScore = rewardItem.getAmount();
        int RANDOM_SCORE_CUT;
        final Random R_EXTRA_SCORE_CUT = new Random();
        RANDOM_SCORE_CUT = R_EXTRA_SCORE_CUT.nextInt(500);
        InstantScore = InstantScore - RANDOM_SCORE_CUT;
        score = score+ InstantScore;
      //  Snackbar reward = Snackbar.make(findViewById(R.id.game_screen), "Wow ! You got "+ Integer.toString(InstantScore)+" points !", Snackbar.LENGTH_LONG);
      //  reward.show();
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {

    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {
        Snackbar reward = Snackbar.make(findViewById(R.id.game_screen), "❌ You don't have a chance for extra points for this turn! Play safe!", Snackbar.LENGTH_LONG);
        reward.show();
    }

    @Override
    public void onRewardedVideoCompleted() {

    }
}