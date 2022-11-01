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

public class WhoAreYouActivity extends AppCompatActivity {
    ArrayList<Player> players;
    Place place;
    Integer player_visualizing;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whoareyou);

        Intent intent = getIntent();
        String checkFlag= intent.getStringExtra("flag");

        if(checkFlag.equals("FROM_LOBBY")){
            System.out.println("FROM_LOBBY");
            players = (ArrayList<Player>) getIntent().getSerializableExtra("PLAYERS");
            place = (Place) getIntent().getSerializableExtra("PLACE");
            player_visualizing = 0;
        }
        else if(checkFlag.equals("FROM_WHOAREYOURESULT")){
            player_visualizing = (Integer) getIntent().getIntExtra("PLAYER_VISUALIZING", 0);
            players = (ArrayList<Player>) getIntent().getSerializableExtra("PLAYERS");
            place = (Place) getIntent().getSerializableExtra("PLACE");
        }

        TextView txtViewPlayerName = (TextView) findViewById(R.id.textViewPlayerName);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        txtViewPlayerName.setText(players.get(player_visualizing).getName());
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

                new AlertDialog.Builder(WhoAreYouActivity.this)
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

    public void whoAreYouResult (View v){
        Intent goToWhoAreYouResultIntent = new Intent(this, WhoAreYouResultActivity.class);
        goToWhoAreYouResultIntent.putExtra("flag", "FROM_WHOAREYOU");
        goToWhoAreYouResultIntent.putExtra("PLAYERS", players);
        goToWhoAreYouResultIntent.putExtra("PLACE", place);
        goToWhoAreYouResultIntent.putExtra("PLAYER_VISUALIZING", player_visualizing);
        startActivity(goToWhoAreYouResultIntent);
    }

    public void goToMainActivity () {
        Intent goToMainActivityIntent = new Intent(this, MainActivity.class);
        startActivity(goToMainActivityIntent);
    }
}
