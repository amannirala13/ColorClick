package asdev.amansoft.com.colorclick;

import android.graphics.drawable.AnimationDrawable;
import android.widget.RelativeLayout;

public class helper {

    public void startBackgroundAnimation(RelativeLayout tagetScreen) {

        AnimationDrawable backgroundAnim = (AnimationDrawable) tagetScreen.getBackground();
        backgroundAnim.setEnterFadeDuration(1500);
        backgroundAnim.setExitFadeDuration(3000);
        backgroundAnim.start();
    }

}
