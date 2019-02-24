package asdev.amansoft.com.colorclick;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Date;

public class signin extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInAccount mAccount;
    private SignInButton googleSigninButton;
    private CircularImageView profilePic;
    private TextView profileName;
    private Button continueBtn, signOutBtn;
    private EditText phoneText;
    private TextInputLayout phoneTextContainer;
    private DatabaseReference mDatabaseRef;
    private Boolean firstTime = null;
    private ProgressDialog LoadingDialog;

    private final static int RC_SIGN_IN = 2;
    private int BackPressedState = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        if(new check().isValidUser())
        {
            startActivity(new Intent(signin.this, MainActivity.class));
            finish();
        }


        new helper().startBackgroundAnimation((RelativeLayout) findViewById(R.id.signin_screen));

        //Variable Initiation
        googleSigninButton = findViewById(R.id.google_login_button);
        mAuth = FirebaseAuth.getInstance();
        profilePic = findViewById(R.id.profile_pic);
        profileName = findViewById(R.id.account_name);
        continueBtn = findViewById(R.id.continue_button);
        signOutBtn = findViewById(R.id.signout_button);
        phoneText = findViewById(R.id.phone_text);
        phoneTextContainer = findViewById(R.id.phone_text_container);
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();

        LoadingDialog = new ProgressDialog(signin.this);
        LoadingDialog.setMessage("Please wait...");




        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                YoYo.with(Techniques.BounceInUp).duration(600).repeat(0).playOn(findViewById(R.id.account_info_container));
            }
        }, 250);


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
                    public void run() {
                        YoYo.with(Techniques.SlideOutDown).duration(600).repeat(0).playOn(findViewById(R.id.account_info_container));
                    }
                }, 200);
                signIn();
            }
        });


      /*  phoneText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                set_countrycode(phoneText);
            }
        });*/

        phoneText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                set_countrycode(phoneText);
            }
        });



        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isValidPhone()) {
                    LoadingDialog.show();
                    firebaseAuthWithGoogle(mAccount);
                }
                else
                {
                    phoneText.requestFocus();
                    Snackbar phoneMessage = Snackbar.make(findViewById(R.id.signin_screen),"Please enter a valid phone number eg: +1 00 000", Snackbar.LENGTH_LONG);
                    phoneMessage.show();
                }
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
        LoadingDialog.show();
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
                Snackbar message = Snackbar.make(findViewById(R.id.signin_screen), "Oops! Something went wrong, not able to sign you up", Snackbar.LENGTH_LONG);
                message.show();
                LoadingDialog.dismiss();
                YoYo.with(Techniques.BounceInUp).duration(600).repeat(0).playOn(findViewById(R.id.account_info_container));
            }
        }

    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            DatabaseReference checkUser = FirebaseDatabase.getInstance().getReference().child("Users").getRef();

                            checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                                    {

                                        AlertDialog.Builder builder1 = new AlertDialog.Builder(signin.this);
                                        builder1.setMessage("Welcome Back!\nYou are about to overwrite your contact data\nDo you want to overwrite? " );
                                        builder1.setCancelable(false);

                                        builder1.setPositiveButton(
                                                "Overwrite",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        dialog.cancel();
                                                        createNewUser();
                                                    }
                                                });

                                        builder1.setNegativeButton(
                                                "No",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        dialog.cancel();
                                                        goToActivity();
                                                    }
                                                });

                                        AlertDialog alert11 = builder1.create();
                                        alert11.show();
                                        LoadingDialog.dismiss();
                                    }
                                    else
                                    {
                                        Toast.makeText(signin.this, "Welcome "+ FirebaseAuth.getInstance().getCurrentUser().getDisplayName(), Toast.LENGTH_SHORT).show();
                                        createNewUser();
                                        LoadingDialog.dismiss();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    LoadingDialog.dismiss();
                                    Snackbar error = Snackbar.make(findViewById(R.id.signin_screen), "❌ Failed to connect to Server !", Snackbar.LENGTH_SHORT);
                                    error.show();
                                }
                            });


                        } else {
                            // If sign in fails, display a message to the user.
                            Snackbar message = Snackbar.make(findViewById(R.id.signin_screen), "❌ Oops! Something went wrong, not able to sign you up", Snackbar.LENGTH_LONG);
                            message.show();
                            FirebaseAuth.getInstance().signOut();
                        }

                        // ...
                    }
                });
    }

    private void createNewUser() {

        LoadingDialog.show();

        final String userName=  FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        final String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final String userEmail =  FirebaseAuth.getInstance().getCurrentUser().getEmail();
        final String userPhone = phoneText.getText().toString();
        final DatabaseReference userDataBaseRef = mDatabaseRef.child("Users").child(userID);


        userDataBaseRef.child("Name").setValue(userName).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                userDataBaseRef.child("Email").setValue(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        userDataBaseRef.child("Phone").setValue(userPhone).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Date CURRENT_TIME = Calendar.getInstance().getTime();
                                userDataBaseRef.child("RegisterDate").setValue(CURRENT_TIME.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        LoadingDialog.dismiss();
                                        goToActivity();
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });



        userDataBaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.hasChild("Personal Score")) {
                        userDataBaseRef.child("Personal Score").child("HighestScore").setValue(0);
                        userDataBaseRef.child("Personal Score").child("HighestScore_Time").setValue(0);
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Snackbar error = Snackbar.make(findViewById(R.id.signin_screen), "Failed to connect to Server !", Snackbar.LENGTH_SHORT);
                error.show();
            }
        });


    }

    private void goToActivity()
    {
        if(isFirstRun())
            goToFirstRun();
        else
            goToMainActivity();
    }

    private void goToMainActivity() {
        Intent MainActivityIntent = new Intent(signin.this, MainActivity.class);
        startActivity(MainActivityIntent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    private void goToFirstRun()
    {

        Intent MainActivityIntent = new Intent(signin.this, FirstRun.class);
        startActivity(MainActivityIntent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
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


    private void updateSignedInUI() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                YoYo.with(Techniques.BounceInUp).duration(600).repeat(0).playOn(findViewById(R.id.account_info_container));

            }
        }, 200);

        Picasso.get().load(mAccount.getPhotoUrl().toString()).into(profilePic);
        profileName.setText(mAccount.getDisplayName());
        profilePic.setVisibility(View.VISIBLE);
        profileName.setVisibility(View.VISIBLE);
        continueBtn.setVisibility(View.VISIBLE);
        signOutBtn.setVisibility(View.VISIBLE);
        phoneTextContainer.setVisibility(View.VISIBLE);
        googleSigninButton.setVisibility(View.GONE);
            LoadingDialog.dismiss();


    }

    private void updateSignedOutUI() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                YoYo.with(Techniques.SlideOutDown).duration(600).repeat(0).playOn(findViewById(R.id.account_info_container));

            }
        }, 200);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                YoYo.with(Techniques.BounceInUp).duration(600).repeat(0).playOn(findViewById(R.id.account_info_container));
                profilePic.setVisibility(View.GONE);
                profileName.setVisibility(View.GONE);
                continueBtn.setVisibility(View.GONE);
                signOutBtn.setVisibility(View.GONE);
                phoneTextContainer.setVisibility(View.GONE);
                googleSigninButton.setVisibility(View.VISIBLE);

            }
        }, 850);


    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        ++BackPressedState;

        if (BackPressedState == 2) {
            System.exit(0);
        }
        else
        {
            Snackbar exitMessage = Snackbar.make(findViewById(R.id.signin_screen), "Press again to exit !", Snackbar.LENGTH_SHORT);
            exitMessage.show();
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                    BackPressedState = 0;
            }
        }, 2000);

    }



    private void set_countrycode(EditText phoneText) {
        if (!new check().countryCodeExisting(phoneText)) {


            TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
            String countryCodeValue = tm.getNetworkCountryIso();

            if (phoneText.getText().length() <= 0)
                phoneText.setText("+" + Integer.toString(new CountryCode().getDialCode(countryCodeValue)) + " ");
            else if (phoneText.getText().charAt(0) == '+')
                phoneText.setText("+" + Integer.toString(new CountryCode().getDialCode(countryCodeValue)) + " " + phoneText.getText().toString().substring(1));
            else
                phoneText.setText("+" + Integer.toString(new CountryCode().getDialCode(countryCodeValue)) + " " + phoneText.getText());

            int existingValueLength = phoneText.getText().length();
            phoneText.setSelection(existingValueLength);

        }
    }

    public boolean isValidPhone()
    {
        Boolean validity = false;

        if(new check().isValidPhone(new CountryCode().getProcessedPhone(phoneText.getText().toString())))
        {
            validity=true;

        }
        else
        {
            YoYo.with(Techniques.Shake).duration(700).repeat(0).playOn(findViewById(R.id.phone_text_container));
        }
            return  validity;
    }
}