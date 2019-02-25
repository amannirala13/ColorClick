package asdev.amansoft.com.colorclick;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.squareup.picasso.Picasso;

public class info extends AppCompatActivity {

    private Button githubButton, instagramButton, backButton;
    private ImageView asdevLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        githubButton = findViewById(R.id.github_button);
        instagramButton = findViewById(R.id.instagram_button);
        backButton = findViewById(R.id.back_button);
        asdevLogo = findViewById(R.id.asdev_logo);

        Picasso.get().load(R.drawable.asdev).fit().into(asdevLogo);

        new helper().startBackgroundAnimation((RelativeLayout) findViewById(R.id.info_main_screen));

        githubButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("http://www.github.com/amannirala13");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        instagramButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("http://www.instagram.com/asdev_13");
                Intent intent2 = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent2);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(info.this, MainActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        animateScreen();
    }

    private void animateScreen() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                YoYo.with(Techniques.DropOut).duration(700).repeat(0).playOn(findViewById(R.id.cc_text));
                YoYo.with(Techniques.BounceInUp).duration(1400).repeat(0).playOn(findViewById(R.id.info_screen));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        YoYo.with(Techniques.BounceIn).duration(300).repeat(0).playOn(findViewById(R.id.asdev_logo));
                        YoYo.with(Techniques.BounceIn).duration(450).repeat(0).playOn(findViewById(R.id.asdev_title));
                        YoYo.with(Techniques.BounceIn).duration(600).repeat(0).playOn(findViewById(R.id.dev_name_text));
                        YoYo.with(Techniques.BounceIn).duration(750).repeat(0).playOn(findViewById(R.id.email_text));
                        YoYo.with(Techniques.BounceIn).duration(900).repeat(0).playOn(findViewById(R.id.github_button));
                        YoYo.with(Techniques.BounceIn).duration(1050).repeat(0).playOn(findViewById(R.id.instagram_button));
                        YoYo.with(Techniques.BounceIn).duration(1200).repeat(0).playOn(findViewById(R.id.icon_sc_text));
                        YoYo.with(Techniques.BounceIn).duration(1350).repeat(0).playOn(findViewById(R.id.back_button));
                    }
                },400);
            }
        },50);

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        YoYo.with(Techniques.Wobble).duration(700).repeat(0).playOn(findViewById(R.id.back_button));
    }
}
