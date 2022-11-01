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

public class WhoAreYouResultActivity extends AppCompatActivity {
    ArrayList<Player> players;
    Place place;
    Integer player_visualizing = 0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whoareyouresult);

        Intent intent = getIntent();
        String checkFlag= intent.getStringExtra("flag");

        if(checkFlag.equals("FROM_WHOAREYOU")){
            players = (ArrayList<Player>) getIntent().getSerializableExtra("PLAYERS");
            place = (Place) getIntent().getSerializableExtra("PLACE");
            player_visualizing = (Integer) intent.getIntExtra("PLAYER_VISUALIZING", 0);
        }

        TextView txtViewPlayerName = (TextView) findViewById(R.id.textViewPlayerName);
        TextView txtViewlocationName = (TextView) findViewById(R.id.locationName);
        TextView txtViewhelp = (TextView) findViewById(R.id.help);
        TextView txtViewPLocationSpyCard = (TextView) findViewById(R.id.textViewLocationSpyCard);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        txtViewPlayerName.setText(players.get(player_visualizing).getName());

        if(players.get(player_visualizing).getRole() == 0){
            txtViewPLocationSpyCard.setText("Location card:");
            txtViewlocationName.setText(place.getName());
            txtViewhelp.setText("Figure out who the spy is.");
        } else {
            txtViewPLocationSpyCard.setText("You are a SPY!!");
            txtViewlocationName.setText("Spy");
            txtViewhelp.setText("Try to guess the rounds location.");
        }
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

                new AlertDialog.Builder(WhoAreYouResultActivity.this)
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

    public void whoAreYou (View v){
        if(player_visualizing < players.size() - 1){
            Intent goToWhoAreYouIntent = new Intent(this, WhoAreYouActivity.class);
            goToWhoAreYouIntent.putExtra("flag","FROM_WHOAREYOURESULT");
            goToWhoAreYouIntent.putExtra("PLAYERS", players);
            goToWhoAreYouIntent.putExtra("PLACE", place);
            goToWhoAreYouIntent.putExtra("PLAYER_VISUALIZING", player_visualizing + 1);
            startActivity(goToWhoAreYouIntent);
        }else{
            // Go to Ready? Activity
        }
    }

    public void goToMainActivity () {
        Intent goToMainActivityIntent = new Intent(this, MainActivity.class);
        startActivity(goToMainActivityIntent);
    }
}