package asdev.amansoft.com.colorclick;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

public class Promotion extends AppCompatActivity {
    private ProgressDialog loadingDialog;
    private  File localFile = null;
    private ImageView promotionImage;
    private Button closeButton;
    private int BackPressedState = 0;
    private DatabaseReference promoDB;
    private String promoLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        promotionImage = findViewById(R.id.promotion_image);
        closeButton = findViewById(R.id.promotion_close_button);

        loadingDialog = new ProgressDialog(Promotion.this);
        loadingDialog.setMessage("Please wait...");
        loadingDialog.setCancelable(false);

        promoDB = FirebaseDatabase.getInstance().getReference("Promotion");
        promoDB.child("Link").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                promoLink = dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Snackbar error = Snackbar.make(findViewById(R.id.promotion_image), "Failed to connect to Server !", Snackbar.LENGTH_SHORT);
                error.show();
            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ValidMainActivityIntent = new Intent(Promotion.this, MainActivity.class);
                startActivity(ValidMainActivityIntent);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
            }
        });


        promotionImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(promoLink);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        loadingDialog.show();
        final StorageReference Events = FirebaseStorage.getInstance().getReference().child("Events/CCpromotion.png");
        try {
            localFile = File.createTempFile("CCimages", "jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Events.getFile(localFile)
                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                       // Toast.makeText(Promotion.this, "Promoted Content", Toast.LENGTH_SHORT).show();
                        closeButton.setVisibility(View.VISIBLE);
                        loadingDialog.dismiss();
                        loadImage();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle failed download
                // ...
                loadingDialog.dismiss();
                startActivity(new Intent(Promotion.this, MainActivity.class));
                finish();
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadingDialog.setMessage("Its taking longer than usual. Please check you Internet connection!");
            }
        }, 48000);

    }

    private void loadImage() {

       Bitmap promotionContent = BitmapFactory.decodeFile(localFile.getPath());
       promotionImage.setImageBitmap(promotionContent);
      //  Picasso.get().load(localFile.getPath()).fit().into(promotionImage);

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        ++BackPressedState;

        if(BackPressedState==2) {
            finish();
            System.exit(0);
        }
        else {
            Snackbar exitMessage = Snackbar.make(findViewById(R.id.promotion_image), "Press again to exit !", Snackbar.LENGTH_SHORT);
            exitMessage.show();
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                BackPressedState=0;

            }
        },2000);



    }


}

