package asdev.amansoft.com.colorclick;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

public class signin extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInAccount mAccount;
    private SignInButton googleSigninButton;
    private CircularImageView profilePic;
    private TextView profileName;
    private Button continureBtn, signOutBtn;

    private final static int RC_SIGN_IN = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        new helper().startBackgroundAnimation((RelativeLayout) findViewById(R.id.signin_screen));

        //Variable Initiation
        googleSigninButton = findViewById(R.id.google_login_button);
        mAuth = FirebaseAuth.getInstance();
        profilePic = findViewById(R.id.profile_pic);
        profileName = findViewById(R.id.account_name);
        continureBtn = findViewById(R.id.continue_button);
        signOutBtn = findViewById(R.id.signout_button);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() { YoYo.with(Techniques.BounceInUp).duration(600).repeat(0).playOn(findViewById(R.id.account_info_container)); }},250);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        googleSigninButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() { YoYo.with(Techniques.SlideOutDown).duration(600).repeat(0).playOn(findViewById(R.id.account_info_container)); }},200);
                signIn();
            }
        });



        continureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                firebaseAuthWithGoogle(mAccount);
            }
        });


        signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                mGoogleSignInClient.signOut();
                updateSignedOutUI();
            }
        });



    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                mAccount = task.getResult(ApiException.class);
                updateSignedInUI();
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                // ...
                Snackbar message = Snackbar.make(findViewById(R.id.signin_screen),"Oops! Something went wrong, not able to sign you up",Snackbar.LENGTH_LONG);
                message.show();
                YoYo.with(Techniques.BounceInUp).duration(600).repeat(0).playOn(findViewById(R.id.account_info_container));
            }
        }

    }



        private void firebaseAuthWithGoogle (GoogleSignInAccount acct){

            AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Intent MainActivityIntent = new Intent(signin.this, MainActivity.class);
                                startActivity(MainActivityIntent);
                            } else {
                                // If sign in fails, display a message to the user.
                                Snackbar message = Snackbar.make(findViewById(R.id.signin_screen),"Oops! Something went wrong, not able to sign you up",Snackbar.LENGTH_LONG);
                                message.show();
                            }

                            // ...
                        }
                    });
        }

    private void updateSignedInUI() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                YoYo.with(Techniques.BounceInUp).duration(600).repeat(0).playOn(findViewById(R.id.account_info_container));

            }
        },200);

        Picasso.get().load(mAccount.getPhotoUrl().toString()).into(profilePic);
        profileName.setText(mAccount.getDisplayName());
        profilePic.setVisibility(View.VISIBLE);
        profileName.setVisibility(View.VISIBLE);
        continureBtn.setVisibility(View.VISIBLE);
        signOutBtn.setVisibility(View.VISIBLE);


        googleSigninButton.setVisibility(View.GONE);

    }

    private void updateSignedOutUI() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                YoYo.with(Techniques.SlideOutDown).duration(600).repeat(0).playOn(findViewById(R.id.account_info_container));

            }
        },200);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                YoYo.with(Techniques.BounceInUp).duration(600).repeat(0).playOn(findViewById(R.id.account_info_container));
                profilePic.setVisibility(View.GONE);
                profileName.setVisibility(View.GONE);
                continureBtn.setVisibility(View.GONE);
                signOutBtn.setVisibility(View.GONE);
                googleSigninButton.setVisibility(View.VISIBLE);

            }
        },850);


    }
}