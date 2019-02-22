package asdev.amansoft.com.colorclick;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import agency.tango.materialintroscreen.MaterialIntroActivity;
import agency.tango.materialintroscreen.MessageButtonBehaviour;
import agency.tango.materialintroscreen.SlideFragment;
import agency.tango.materialintroscreen.SlideFragmentBuilder;

public class FirstRun extends MaterialIntroActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_first_run);

        enableLastSlideAlphaExitTransition(true);

        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.colorPrimaryDark)
                .buttonsColor(R.color.confusedRed)
                .title("Welcome")
                .image(R.drawable.welcome)
                .description("\nColor Click\nby\nAS DEV Production")
                .build());

        addSlide(new SlideFragmentBuilder()
        .backgroundColor(R.color.colorPrimaryDark)
                .buttonsColor(R.color.confusedYellow)
                .image(R.drawable.confused)
                .title("Let's know about Color Click")
                .description("\nColor Click is a hyper casual game.\nIts a game that tests your reflexes and brain.")
                .build(),
                new MessageButtonBehaviour(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                                try{
                                    Uri uri = Uri.parse("market://details?id="+getPackageName());
                                    Intent intent2 = new Intent(Intent.ACTION_VIEW, uri);
                                    startActivity(intent2);
                                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                }catch (ActivityNotFoundException e)
                                {
                                    Uri uri = Uri.parse("http://play.google.com/store/apps/details?id="+getPackageName());
                                    Intent intent2 = new Intent(Intent.ACTION_VIEW, uri);
                                    startActivity(intent2);
                                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                }
                    }
                }, "Learn More")
        );

        addSlide(new SlideFragmentBuilder()
                        .backgroundColor(R.color.colorPrimaryDark)
                        .buttonsColor(R.color.confusedGreen)
                        .title("How to play?")
                        .image(R.drawable.component)
                        .description("\nYou need to click the color which the text has not what it says. Be aware, there is a timer of 2 seconds to be taken care of!")
                        .build(),
                new MessageButtonBehaviour(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(FirstRun.this, "Video Played", Toast.LENGTH_SHORT).show();
                    }
                }, "Play Video")
        );

        addSlide(new SlideFragmentBuilder()
                        .backgroundColor(R.color.colorPrimaryDark)
                        .buttonsColor(R.color.confusedBlue)
                        .title("Winners !")
                        .image(R.drawable.medal)
                        .description("\nThe player scoring the most, becomes the leader. The Winner of an event is the leader at the end of the event. To claim Event rewards the winner must provide his/her working contact number")
                        .build());

        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.colorPrimaryDark)
                .buttonsColor(R.color.confusedOrange)
                .title("You are good to go!")
                .image(R.drawable.gotcha)
                .description("\nLets play now. Best of luck!")
                .build());

    }

    @Override
    public void onFinish() {
        super.onFinish();
        startActivity(new Intent(FirstRun.this, MainActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();

    }
}
