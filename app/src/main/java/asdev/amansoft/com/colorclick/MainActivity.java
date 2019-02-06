package asdev.amansoft.com.colorclick;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private Button playButton;
    private DatabaseReference mDatabase;
    private TextView worldsHighestScoreText;
    private String worldsHighestScore;
    private RelativeLayout loadingContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playButton = findViewById(R.id.play_button);
        worldsHighestScoreText = findViewById(R.id.world_highest_score);
        loadingContainer = findViewById(R.id.loading_container);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent GameIntent = new Intent(MainActivity.this, Game.class);
                GameIntent.putExtra("WORLD_HIGHEST", worldsHighestScore);
                startActivity(GameIntent);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                worldsHighestScore = dataSnapshot.child("World Highest").getValue().toString();
                worldsHighestScoreText.setText(worldsHighestScore);
                loadingContainer.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Error connecting to Database", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
