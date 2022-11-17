package pt.ubi.di.pmd.spyfall_ubi;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;

public class GameONActivity extends AppCompatActivity {
    ArrayList<Player> playersActive;
    ArrayList<Player> playersCompleted;
    Place place;
    int playerPlaying;
    TextView TxtViewPlayer;
    TextView TxtViewTimer;
    long timeUntilFinishTimer = -1;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gameon);

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Change toolbar title
        setTitle(getResources().getString(R.string.GameONActivity));

        // Getting the flag from the intent that he came from
        Intent intent = getIntent();
        String checkFlag= intent.getStringExtra("flag");

        TxtViewTimer = (TextView) findViewById(R.id.textViewTimer);
        TxtViewPlayer = (TextView) findViewById(R.id.textViewWhosPlaying);

        // Check flag and initialize objects
        if(checkFlag.equals("FROM_READY")){
            playersActive = (ArrayList<Player>) getIntent().getSerializableExtra("PLAYERS");
            playersCompleted = playersActive;
            place = (Place) getIntent().getSerializableExtra("PLACE");
            playerPlaying = (int) getIntent().getSerializableExtra("PLAYER_STARTING");
            TxtViewPlayer.setText(getResources().getString(R.string.playing)+ " " + playersActive.get(playerPlaying).getName());
        }
        if(checkFlag.equals("FROM_REVEALSPY") || checkFlag.equals("FROM_SPYELIMINATED")){
            playersActive = (ArrayList<Player>) getIntent().getSerializableExtra("PLAYERS");
            place = (Place) getIntent().getSerializableExtra("PLACE");
            playerPlaying = (int) getIntent().getSerializableExtra("PLAYER_PLAYING");
            timeUntilFinishTimer = (long) getIntent().getSerializableExtra("TIMER");
            TxtViewPlayer.setText(getResources().getString(R.string.playing)+ " " + playersActive.get(playerPlaying).getName());
            playersCompleted = (ArrayList<Player>) getIntent().getSerializableExtra("PLAYERS_COMPLETED");
        }

        // Create timer with 8 minutes in case of the first load of the view
        // or his previous time before it was stopped for voting
        new CountDownTimer((timeUntilFinishTimer != -1) ? timeUntilFinishTimer : 480000, 1000) {
            String min, sec;
            public void onTick(long millisUntilFinished) {
                timeUntilFinishTimer = millisUntilFinished;

                min = String.valueOf(millisUntilFinished / (60 * 1000) % 60);
                sec = String.valueOf(millisUntilFinished / 1000 % 60);

                if (Long.parseLong(sec) < 10)
                    sec = "0"+ millisUntilFinished / 1000 % 60;

                TxtViewTimer.setText(min+":"+sec);
            }

            public void onFinish() {
                TxtViewTimer.setText("finish");
                revealSpy(getWindow().getDecorView());
            }

        }.start();
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
                new AlertDialog.Builder(GameONActivity.this)
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

    // Go to RevealSPY activity by clicking in a button
    public void revealSpy(View v){
        Intent goToReavealSpyIntent = new Intent(this, RevealSpyActivity.class);
        goToReavealSpyIntent.putExtra("flag","FROM_GAMEON");
        goToReavealSpyIntent.putExtra("PLAYERS", playersActive);
        goToReavealSpyIntent.putExtra("PLACE", place);
        goToReavealSpyIntent.putExtra("PLAYER_VOTING", playerPlaying);
        goToReavealSpyIntent.putExtra("TIMER_LONG", timeUntilFinishTimer);
        goToReavealSpyIntent.putExtra("TIMER_STRING", TxtViewTimer.getText().toString());
        goToReavealSpyIntent.putExtra("PLAYERS_COMPLETED", playersCompleted);
        startActivity(goToReavealSpyIntent);
    }

    // Go to RevealLocation activity by clicking in a button
    // In case of a non-spie clicking the button, end the game (cheat)
    public void revealLocation(View v){
        if(playersActive.get(playerPlaying).getRole() == 1){
            Intent goToReavealSpyIntent = new Intent(this, RevealLocationActivity.class);
            goToReavealSpyIntent.putExtra("flag","FROM_GAMEON");
            goToReavealSpyIntent.putExtra("PLAYERS", playersActive);
            goToReavealSpyIntent.putExtra("PLACE", place);
            goToReavealSpyIntent.putExtra("PLAYER_VOTING", playerPlaying);
            goToReavealSpyIntent.putExtra("TIMER_LONG", timeUntilFinishTimer);
            goToReavealSpyIntent.putExtra("TIMER_STRING", TxtViewTimer.getText().toString());
            goToReavealSpyIntent.putExtra("PLAYERS_COMPLETED", playersCompleted);
            startActivity(goToReavealSpyIntent);
        } else {
            Toast.makeText(GameONActivity.this, getResources().getString(R.string.nonSpyRevealLocation),
                    Toast.LENGTH_LONG).show();

            goToLobby(getWindow().getDecorView());
        }
    }

    // Go to Lobby activity by clicking in a button
    public void goToLobby (View v) {
        Intent goToLobbyIntent = new Intent(this, LobbyActivity.class);
        goToLobbyIntent.putExtra("flag","FROM_GAMEON");
        goToLobbyIntent.putExtra("PLAYERS", playersActive);
        startActivity(goToLobbyIntent);
    }

    // Select previous player by clicking on a button
    public void playerBack(View v){
        playerPlaying -= 1;

        if(playerPlaying < 0)
            playerPlaying = playersActive.size() - 1;

        TxtViewPlayer.setText(getResources().getString(R.string.playing)+ " " + playersActive.get(playerPlaying).getName());
    }

    // Select next player by clicking on a button
    public void playerNext(View v){
        playerPlaying += 1;

        if(playerPlaying >= playersActive.size())
            playerPlaying = 0;

        TxtViewPlayer.setText(getResources().getString(R.string.playing)+ " " + playersActive.get(playerPlaying).getName());
    }
}
