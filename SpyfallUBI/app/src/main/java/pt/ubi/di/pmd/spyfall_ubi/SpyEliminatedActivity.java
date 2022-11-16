package pt.ubi.di.pmd.spyfall_ubi;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;

public class SpyEliminatedActivity extends AppCompatActivity {
    ArrayList<Player> playersActive;
    ArrayList<Player> playersCompleted;
    Place place;
    Player playerEliminated;
    TextView TxtViewPlayerEliminated;
    TextView TxtViewEliminationInfo;
    int playerPlaying;
    long time;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spyeliminated);

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Change toolbar title
        setTitle(getResources().getString(R.string.SPYEliminatedActivity));

        // Getting the flag from the intent that he came from
        Intent intent = getIntent();
        String checkFlag= intent.getStringExtra("flag");

        // Check flag and initialize objects
        if(checkFlag.equals("FROM_REVEALSPY")){
            playersActive = (ArrayList<Player>) getIntent().getSerializableExtra("PLAYERS");
            place = (Place) getIntent().getSerializableExtra("PLACE");
            playerPlaying = (int) getIntent().getSerializableExtra("PLAYER_PLAYING");
            time = (long) getIntent().getSerializableExtra("TIMER");
            playerEliminated = (Player) getIntent().getSerializableExtra("PLAYER_ELIMINATED");
            playersCompleted = (ArrayList<Player>) getIntent().getSerializableExtra("PLAYERS_COMPLETED");
        }

        TxtViewPlayerEliminated = (TextView) findViewById(R.id.textPlayerEliminated);
        TxtViewPlayerEliminated.setText(playerEliminated.getName());

        TxtViewEliminationInfo = (TextView) findViewById(R.id.textEliminationInfo);
        String eliminationMsg = getResources().getString(R.string.SpyEliminatedAct2)+ " " + playerEliminated.getName() + " " + getResources().getString(R.string.SpyEliminatedAct3);
        TxtViewEliminationInfo.setText(eliminationMsg);
    }

    // Inflating the toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_rest, menu);
        return true;
    }

    // Toolbar button clicked
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.shareButton:
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = getResources().getString(R.string.Share1);
                String shareSubject = "Spyfall @ UBI!";
                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, shareSubject);
                startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.Share2)));
                break;
            case R.id.homeButton:
                new AlertDialog.Builder(SpyEliminatedActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(getResources().getString(R.string.GoHome1))
                        .setMessage(getResources().getString(R.string.GoHome2))
                        .setPositiveButton(getResources().getString(R.string.YES), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                goToMainActivity();
                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.NO), null)
                        .show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // Go to MainActivity by clicking in a button
    public void goToMainActivity () {
        Intent goToMainActivityIntent = new Intent(this, MainActivity.class);
        startActivity(goToMainActivityIntent);
    }

    // Go to GameONActivity
    public void goToGame (View v) {
        Intent goToGameONIntent = new Intent(this, GameONActivity.class);
        goToGameONIntent.putExtra("flag","FROM_SPYELIMINATED");
        goToGameONIntent.putExtra("PLAYERS", playersActive);
        goToGameONIntent.putExtra("PLACE", place);
        goToGameONIntent.putExtra("PLAYER_PLAYING", getRandomNumber(0, playersActive.size()));
        goToGameONIntent.putExtra("TIMER", time);
        goToGameONIntent.putExtra("PLAYERS_COMPLETED", playersCompleted);
        startActivity(goToGameONIntent);
    }

    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
}
