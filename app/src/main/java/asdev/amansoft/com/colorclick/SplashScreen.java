package asdev.amansoft.com.colorclick;


import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.crashlytics.android.Crashlytics;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.database.FirebaseDatabase;
import io.fabric.sdk.android.Fabric;

public class SplashScreen extends AppCompatActivity {

    private int SCREEN_TIME_OUT = 2000;
    private static Boolean PERSISTENCE_STATE = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!PERSISTENCE_STATE)
        {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            PERSISTENCE_STATE = true;
        }

        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_splash_screen);
        Fabric.with(this, new Crashlytics());

        MobileAds.initialize(getApplicationContext(),getString(R.string.admob_app_id));

     /*   new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent MainActivityIntent = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(MainActivityIntent);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();

            }
        },SCREEN_TIME_OUT);

*/  final MediaPlayer StartUpFX = MediaPlayer.create(this, R.raw.startup);
        StartUpFX.start();
        StartUpFX.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                StartUpFX.stop();
                StartUpFX.reset();

            }
        });



        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                YoYo.with(Techniques.BounceIn).duration(800).repeat(0).playOn(findViewById(R.id.splash_text));
                YoYo.with(Techniques.BounceInUp).duration(1000).repeat(0).playOn(findViewById(R.id.asdev_pro_text));
                //  YoYo.with(Techniques.BounceInUp).duration(500).repeat(0).playOn(findViewById(R.id.asdev_production_text));
            }
        },130);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SplashScreenAction();
            }
        },SCREEN_TIME_OUT);
    }

    private void SplashScreenAction()
    {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (new check().isValidUser()) {

                        Intent ValidMainActivityIntent = new Intent(SplashScreen.this, Promotion.class);
                        startActivity(ValidMainActivityIntent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        finish();
                    }

                else {
                    Intent MainActivityIntent = new Intent(SplashScreen.this, signin.class);
                    startActivity(MainActivityIntent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();

                }
            }
        }, SCREEN_TIME_OUT);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}