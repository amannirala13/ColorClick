package asdev.amansoft.com.colorclick;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ShowScore extends AppCompatActivity {

    private TextView scoreText;
    private Button continueButton;
    private int score;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_score);
        scoreText = findViewById(R.id.show_score_your_score);
        continueButton = findViewById(R.id.show_score_continue_button);

        new helper().startBackgroundAnimation((RelativeLayout)findViewById(R.id.show_score_main_container));

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent MainActivityIntent = new Intent(ShowScore.this, MainActivity.class);
                startActivity(MainActivityIntent);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        score = getIntent().getIntExtra("SCORE",0);
        scoreText.setText(Integer.toString(score));

    }
}
