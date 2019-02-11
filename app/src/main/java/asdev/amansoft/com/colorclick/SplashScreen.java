package asdev.amansoft.com.colorclick;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RelativeLayout;

import com.crashlytics.android.Crashlytics;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.fabric.sdk.android.Fabric;

public class SplashScreen extends AppCompatActivity {

    private int SCREEN_TIME_OUT = 1000;
    private Boolean firstTime = null;
    private Boolean PERSISTENCE_STATE = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_splash_screen);
        Fabric.with(this, new Crashlytics());



     /*   new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent MainActivityIntent = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(MainActivityIntent);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();

            }
        },SCREEN_TIME_OUT);

*/

    }

    @Override
    protected void onStart() {
        super.onStart();


            final MediaPlayer StartUpFX = MediaPlayer.create(this, R.raw.startup);
            StartUpFX.start();
            StartUpFX.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {


                    SplashScreenAction();
                    StartUpFX.stop();
                    StartUpFX.reset();

                }
            });



        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                YoYo.with(Techniques.ZoomInDown).duration(800).repeat(0).playOn(findViewById(R.id.splash_text));
                YoYo.with(Techniques.BounceInUp).duration(500).repeat(0).playOn(findViewById(R.id.asdev_production_text));
            }
        },100);
    }

    //Checks if the App was run fist time or not
    private boolean isFirstRun() {
        if (firstTime == null) {
            SharedPreferences mPreferences = this.getSharedPreferences("first_time", Context.MODE_PRIVATE);
            firstTime = mPreferences.getBoolean("firstTime", true);
            if (firstTime) {
                SharedPreferences.Editor editor = mPreferences.edit();
                editor.putBoolean("firstTime", false);
                editor.apply();
            }
        }
        return firstTime;

    }


    private void SplashScreenAction()
    {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (new check().isValidUser()) {

                    if (isFirstRun()) {
                        Intent MainActivityIntent = new Intent(SplashScreen.this, FirstRun.class);
                        startActivity(MainActivityIntent);
                        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                        finish();
                    } else {
                        Intent MainActivityIntent = new Intent(SplashScreen.this, MainActivity.class);
                        startActivity(MainActivityIntent);
                        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                        finish();
                    }


                } else {
                    Intent MainActivityIntent = new Intent(SplashScreen.this, signin.class);
                    startActivity(MainActivityIntent);
                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                    finish();

                }
            }
        }, SCREEN_TIME_OUT);
    }


    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if(savedInstanceState!=null)
            savedInstanceState.putBoolean("PERSISTENCE_STATE", PERSISTENCE_STATE);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        PERSISTENCE_STATE =savedInstanceState.getBoolean("PERSISTENCE_STATE");
    }


}