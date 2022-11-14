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
    ArrayList<Player> players;
    ArrayList<Player> players_completed;
    Place place;
    int playerPlaying;
    long time;
    Player playerEliminated;
    TextView txtViewPlayerEliminated;
    TextView txtViewEliminationInfo;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spyeliminated);

        setTitle(getResources().getString(R.string.SPYEliminatedActivity));

        Intent intent = getIntent();
        String checkFlag= intent.getStringExtra("flag");

        if(checkFlag.equals("FROM_REVEALSPY")){
            players = (ArrayList<Player>) getIntent().getSerializableExtra("PLAYERS");
            place = (Place) getIntent().getSerializableExtra("PLACE");
            playerPlaying = (int) getIntent().getSerializableExtra("PLAYER_PLAYING");
            time = (long) getIntent().getSerializableExtra("TIMER");
            playerEliminated = (Player) getIntent().getSerializableExtra("PLAYER_ELIMINATED");
            players_completed = (ArrayList<Player>) getIntent().getSerializableExtra("PLAYERS_COMPLETED");
        }

        txtViewPlayerEliminated = (TextView) findViewById(R.id.textPlayerEliminated);
        txtViewPlayerEliminated.setText(playerEliminated.getName());

        txtViewEliminationInfo = (TextView) findViewById(R.id.textEliminationInfo);
        String eliminationMsg = "The spy '"+ playerEliminated.getName() +"' has been eliminated and can´t play anymore.";
        txtViewEliminationInfo.setText(eliminationMsg);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_rest, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.shareButton:
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "Link to Playstore";
                String shareSubject = "Spyfall @ UBI!";
                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, shareSubject);
                startActivity(Intent.createChooser(sharingIntent, "Share using:"));
                break;
            case R.id.homeButton:
                // Ask if we want to the lobby and lose all the current page settings

                new AlertDialog.Builder(SpyEliminatedActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Are you going to the main page?")
                        .setMessage("Do you want to lose the current game state and go back to the main page?")
                        .setPositiveButton("Yes!", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                goToMainActivity();
                            }
                        })
                        .setNegativeButton("No!", null)
                        .show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void goToMainActivity () {
        Intent goToMainActivityIntent = new Intent(this, MainActivity.class);
        startActivity(goToMainActivityIntent);
    }

    public void goToGame (View v) {
        Intent goToGameONIntent = new Intent(this, GameONActivity.class);
        goToGameONIntent.putExtra("flag","FROM_SPYELIMINATED");
        goToGameONIntent.putExtra("PLAYERS", players);
        goToGameONIntent.putExtra("PLACE", place);
        goToGameONIntent.putExtra("PLAYER_PLAYING", getRandomNumber(0, players.size()));
        goToGameONIntent.putExtra("TIMER", time);
        goToGameONIntent.putExtra("PLAYERS_COMPLETED", players_completed);
        startActivity(goToGameONIntent);
    }
    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

}
