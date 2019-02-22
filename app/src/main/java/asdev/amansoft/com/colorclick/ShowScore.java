package asdev.amansoft.com.colorclick;

import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

public class ShowScore extends AppCompatActivity {

    private TextView scoreText;
    private Button continueButton;
    private int score;
    private int BackPressedState = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_score);
        scoreText = findViewById(R.id.show_score_your_score);
        continueButton = findViewById(R.id.show_score_continue_button);

        new helper().startBackgroundAnimation((RelativeLayout) findViewById(R.id.show_score_main_container));
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent MainActivityIntent = new Intent(ShowScore.this, MainActivity.class);
                startActivity(MainActivityIntent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                YoYo.with(Techniques.BounceInUp).duration(800).repeat(0).playOn(findViewById(R.id.show_score_container));
            }
        }, 100);

        score = getIntent().getIntExtra("SCORE", 0);
        scoreText.setText(Integer.toString(score));

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
            Snackbar exitMessage = Snackbar.make(findViewById(R.id.show_score_container), "Press again to exit !", Snackbar.LENGTH_SHORT);
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
