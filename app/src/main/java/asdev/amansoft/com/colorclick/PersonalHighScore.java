package asdev.amansoft.com.colorclick;

import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

public class PersonalHighScore extends AppCompatActivity {

    private TextView scoreText;
    private Button continueBtn;
    private long score;
    private int BackPressedState = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_high_score);

        scoreText = findViewById(R.id.congo_score_text);
        continueBtn = findViewById(R.id.congo_continue_button);

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent MainActivityIntent = new Intent(PersonalHighScore.this, MainActivity.class);
                startActivity(MainActivityIntent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        score = getIntent().getLongExtra("SCORE",0);
        scoreText.setText(Long.toString(score));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                YoYo.with(Techniques.BounceInDown).duration(700).repeat(0).playOn(findViewById(R.id.score_icon));
                YoYo.with(Techniques.DropOut).duration(750).repeat(0).playOn(findViewById(R.id.new_record_text));
                YoYo.with(Techniques.Landing).duration(800).repeat(0).playOn(findViewById(R.id.congo_text));
                YoYo.with(Techniques.StandUp).duration(850).repeat(0).playOn(findViewById(R.id.congo_score_text));
                YoYo.with(Techniques.BounceInUp).repeat(900).repeat(0).playOn(findViewById(R.id.congo_continue_button));
            }
        },100);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        ++BackPressedState;

        if (BackPressedState == 2) {
            finish();
            System.exit(0);}
        else
        {
            Snackbar exitMessage = Snackbar.make(findViewById(R.id.personal_highscore_screen), "Press again to exit app !", Snackbar.LENGTH_SHORT);
            exitMessage.show();
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                BackPressedState=0;
            }
        }, 2000);

    }


}
