package asdev.amansoft.com.colorclick;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.view.View;
import android.widget.RelativeLayout;

public class helper {

    public void startBackgroundAnimation(RelativeLayout tagetScreen) {

        AnimationDrawable backgroundAnim = (AnimationDrawable) tagetScreen.getBackground();
        backgroundAnim.setEnterFadeDuration(1500);
        backgroundAnim.setExitFadeDuration(3000);
        backgroundAnim.start();
    }

    public void createBackgroundFx(Context context,MediaPlayer BackgroundFx)
    {
        BackgroundFx = MediaPlayer.create(context, R.raw.background_fx);
    }



    public void PlayBackGroundMusic(MediaPlayer BackgroundFx)

    {
        BackgroundFx.setVolume(30,30);
        BackgroundFx.isLooping();
        BackgroundFx.start();
    }

    public void StopBackGroundMusic(MediaPlayer BackgroundFx)

    {

        if (BackgroundFx != null) {

            BackgroundFx.stop();
            BackgroundFx.reset();

        }


    }

}
