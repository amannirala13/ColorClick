package asdev.amansoft.com.colorclick;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import io.fabric.sdk.android.Fabric;

public class SplashScreen extends AppCompatActivity {

    private int SCREEN_TIME_OUT = 2500;
    private Boolean firstTime = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (new check().isValidUser()) {

                    if (isFirstRun()) {
                        //ToDO >> Trigger First Run Activity
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

    //Checks if the App was run fist time or not
    private boolean isFirstRun() {
        if (firstTime == null) {
            SharedPreferences mPreferences = this.getSharedPreferences("first_time", Context.MODE_PRIVATE);
            firstTime = mPreferences.getBoolean("firstTime", true);
            if (firstTime) {
                SharedPreferences.Editor editor = mPreferences.edit();
                editor.putBoolean("firstTime", false);
                editor.commit();
            }
        }
        return firstTime;

    }


}